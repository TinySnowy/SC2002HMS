package pharmacy_management.appointments;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import user_management.Patient;
import utils.CSVReaderUtil;
import utils.CSVWriterUtil;
import user_management.UserController;
import pharmacy_management.prescriptions.Prescription;
import patient_management.model.MedicalRecord;
import patient_management.controllers.MedicalRecordController;

/**
 * Service class managing appointment outcomes in the HMS.
 * Handles:
 * - Outcome data persistence
 * - Prescription tracking
 * - CSV file operations
 * - Medical record updates
 * Provides centralized management of appointment results and prescriptions.
 */
public class AppointmentOutcomeService implements IAppointmentOutcomeService {
    /** Maps appointment IDs to their medical outcomes */
    private final Map<String, AppointmentOutcome> outcomes;
    
    /** Controller for user management operations */
    private final UserController userController;
    
    /** Controller for medical record management */
    private final MedicalRecordController medicalRecordController;
    
    /** File path for outcome data persistence */
    private static final String OUTCOMES_FILE = "SC2002HMS/data/AppointmentOutcomes.csv";
    
    /** Format pattern for date-time storage */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Initializes the outcome service with required dependencies.
     * Sets up data structures and loads existing outcomes.
     * 
     * @param userController Controller for user operations
     */
    public AppointmentOutcomeService(UserController userController) {
        this.outcomes = new HashMap<>();
        this.userController = userController;
        this.medicalRecordController = new MedicalRecordController();
        loadOutcomes();
    }

    /**
     * Loads appointment outcomes from CSV storage.
     * Processes:
     * - Basic outcome information
     * - Prescription details
     * - Data validation
     * Handles file reading and parsing errors.
     */
    private void loadOutcomes() {
        List<String[]> records = CSVReaderUtil.readCSV(OUTCOMES_FILE);
        boolean isFirstRow = true;

        for (String[] record : records) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            try {
                // Validate record format
                if (record.length < 6) {
                    System.err.println("Invalid outcome record format: insufficient fields");
                    continue;
                }

                // Parse basic outcome data
                String appointmentId = record[0].trim();
                String patientId = record[1].trim();
                String doctorId = record[2].trim();
                LocalDateTime date = LocalDateTime.parse(record[3].trim(), DATE_FORMATTER);
                String serviceType = record[4].trim();
                String notes = record[5].trim();

                // Create outcome record
                AppointmentOutcome outcome = new AppointmentOutcome(
                    appointmentId, patientId, doctorId, date, serviceType, notes);

                // Load associated prescriptions if they exist
                if (record.length > 6 && !record[6].trim().isEmpty()) {
                    loadPrescriptions(outcome, record[6].trim());
                }

                outcomes.put(appointmentId, outcome);
                
            } catch (Exception e) {
                System.err.println("Error loading outcome record: " + e.getMessage());
            }
        }
    }

    /**
     * Loads prescription data for an outcome.
     * Parses prescription strings and creates prescription objects.
     * Links prescriptions to patients and outcomes.
     * 
     * @param outcome Outcome to add prescriptions to
     * @param prescriptionData Raw prescription data string
     */
    private void loadPrescriptions(AppointmentOutcome outcome, String prescriptionData) {
        String[] prescriptions = prescriptionData.split(";");
        for (String prescData : prescriptions) {
            try {
                String[] parts = prescData.split("\\|");
                if (parts.length >= 4) {
                    String prescId = parts[0].trim();
                    String medicationName = parts[1].trim();
                    String dosage = parts[2].trim();
                    int quantity = Integer.parseInt(parts[3].trim());

                    Patient patient = (Patient) userController.getUserById(outcome.getPatientId());
                    if (patient != null) {
                        Prescription prescription = new Prescription(
                            prescId, patient, medicationName, dosage, quantity);
                        outcome.addPrescription(prescription);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading prescription: " + e.getMessage());
            }
        }
    }

    private void saveOutcomesToCSV() {
        try {
            CSVWriterUtil.writeCSV(OUTCOMES_FILE, writer -> {
                // Write header
                writer.write("AppointmentId,PatientId,DoctorId,Date,ServiceType,Notes,Prescriptions\n");
                
                // Write outcomes
                for (AppointmentOutcome outcome : outcomes.values()) {
                    StringBuilder prescriptionData = new StringBuilder();
                    for (Prescription p : outcome.getPrescriptions()) {
                        if (prescriptionData.length() > 0) {
                            prescriptionData.append(";");
                        }
                        prescriptionData.append(String.format("%s|%s|%s|%d",
                            p.getPrescriptionId(),
                            p.getMedicationName(),
                            p.getDosage(),
                            p.getQuantity()));
                    }

                    writer.write(String.format("%s,%s,%s,%s,%s,%s,%s\n",
                        outcome.getAppointmentId(),
                        outcome.getPatientId(),
                        outcome.getDoctorId(),
                        outcome.getAppointmentDate().format(DATE_FORMATTER),
                        outcome.getServiceType(),
                        outcome.getConsultationNotes(),
                        prescriptionData.toString()));
                }
            });
        } catch (Exception e) {
            System.err.println("Error saving outcomes: " + e.getMessage());
        }
    }

    @Override
    public List<AppointmentOutcome> getPendingPrescriptionOutcomes() {
        List<AppointmentOutcome> pendingOutcomes = outcomes.values().stream()
            .filter(outcome -> outcome.getPrescriptions().stream()
                .anyMatch(p -> "Pending".equals(p.getStatus())))
            .collect(java.util.stream.Collectors.toList());
            
        return pendingOutcomes;
    }

    @Override
    public boolean updatePrescriptionStatus(String appointmentId, String prescriptionId, String status) {
        AppointmentOutcome outcome = outcomes.get(appointmentId);
        if (outcome == null) {
            System.err.println("Outcome not found for appointment: " + appointmentId);
            return false;
        }

        boolean updated = false;
        for (Prescription prescription : outcome.getPrescriptions()) {
            if (prescription.getPrescriptionId().equals(prescriptionId)) {
                prescription.setStatus(status);
                updated = true;

                // Update medical record if prescription is fulfilled
                if ("Fulfilled".equals(status)) {
                    MedicalRecord record = medicalRecordController.getRecordByPatientId(outcome.getPatientId());
                    if (record != null) {
                        medicalRecordController.updateRecord(
                            outcome.getPatientId(),
                            record.getDiagnosis(),
                            String.format("%s %s", prescription.getMedicationName(), prescription.getDosage()));
                    }
                }
                break;
            }
        }

        if (updated) {
            saveOutcomesToCSV();
        }
        return updated;
    }

    @Override
    public AppointmentOutcome getOutcomeByAppointmentId(String appointmentId) {
        return outcomes.get(appointmentId);
    }

    @Override
    public boolean saveAppointmentOutcome(AppointmentOutcome outcome) {
        if (outcome == null || outcome.getAppointmentId() == null) {
            return false;
        }
        outcomes.put(outcome.getAppointmentId(), outcome);
        saveOutcomesToCSV();
        return true;
    }
}