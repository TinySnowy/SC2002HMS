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

public class AppointmentOutcomeService implements IAppointmentOutcomeService {
    private final Map<String, AppointmentOutcome> outcomes;
    private final UserController userController;
    private final MedicalRecordController medicalRecordController;
    private static final String OUTCOMES_FILE = "SC2002HMS/data/AppointmentOutcomes.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AppointmentOutcomeService(UserController userController) {
        this.outcomes = new HashMap<>();
        this.userController = userController;
        this.medicalRecordController = new MedicalRecordController();
        loadOutcomes();
    }

    private void loadOutcomes() {
        List<String[]> records = CSVReaderUtil.readCSV(OUTCOMES_FILE);
        boolean isFirstRow = true;

        for (String[] record : records) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            try {
                if (record.length < 6) {
                    System.err.println("Invalid outcome record format: insufficient fields");
                    continue;
                }

                String appointmentId = record[0].trim();
                String patientId = record[1].trim();
                String doctorId = record[2].trim();
                LocalDateTime date = LocalDateTime.parse(record[3].trim(), DATE_FORMATTER);
                String serviceType = record[4].trim();
                String notes = record[5].trim();

                AppointmentOutcome outcome = new AppointmentOutcome(
                    appointmentId, patientId, doctorId, date, serviceType, notes);

                // Load prescriptions if they exist
                if (record.length > 6 && !record[6].trim().isEmpty()) {
                    loadPrescriptions(outcome, record[6].trim());
                }

                outcomes.put(appointmentId, outcome);
                
            } catch (Exception e) {
                System.err.println("Error loading outcome record: " + e.getMessage());
            }
        }
    }

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