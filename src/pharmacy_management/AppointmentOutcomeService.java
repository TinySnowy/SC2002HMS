package pharmacy_management;

import java.time.LocalDateTime;
import java.util.*;
import user_management.Patient;
import utils.CSVReaderUtil;
import utils.CSVWriterUtil;
import user_management.UserController;

public class AppointmentOutcomeService implements IAppointmentOutcomeService {
    private final Map<String, AppointmentOutcome> outcomes = new HashMap<>();
    private final UserController userController;
    private static final String APPOINTMENTS_FILE = "SC2002HMS/data/Appointments.csv";
    private static final String PRESCRIPTIONS_FILE = "SC2002HMS/data/Prescriptions.csv";

    public AppointmentOutcomeService(UserController userController) {
        this.userController = userController;
        loadOutcomes();
    }

    private void loadOutcomes() {
        List<String[]> records = CSVReaderUtil.readCSV(APPOINTMENTS_FILE);
        boolean isFirstRow = true;

        for (String[] record : records) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            try {
                String appointmentId = record[0];
                String patientId = record[1];
                String doctorId = record[2];
                LocalDateTime date = LocalDateTime.parse(record[3].replace(" ", "T"));
                String status = record[4];
                String serviceType = record[5];
                String notes = record.length > 6 ? record[6] : "";

                AppointmentOutcome outcome = new AppointmentOutcome(
                    appointmentId, patientId, doctorId, date, serviceType, notes);
                outcomes.put(appointmentId, outcome);
            } catch (Exception e) {
                System.err.println("Error loading appointment outcome: " + e.getMessage());
            }
        }

        loadPrescriptions();
    }

    private void loadPrescriptions() {
        List<String[]> records = CSVReaderUtil.readCSV(PRESCRIPTIONS_FILE);
        boolean isFirstRow = true;

        for (String[] record : records) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            try {
                String prescriptionId = record[0];
                String appointmentId = record[1];
                String patientId = record[2];
                String medicationName = record[3];
                String dosage = record[4];
                int quantity = Integer.parseInt(record[5]);
                String status = record[6];

                // Get patient from UserController
                Patient patient = (Patient) userController.getUserById(patientId);
                if (patient == null) {
                    System.err.println("Patient not found for prescription: " + prescriptionId);
                    continue;
                }

                AppointmentOutcome outcome = outcomes.get(appointmentId);
                if (outcome != null) {
                    Prescription prescription = new Prescription(prescriptionId, patient, 
                        medicationName, dosage, quantity);
                    prescription.setStatus(status);
                    outcome.addPrescription(prescription);
                }
            } catch (Exception e) {
                System.err.println("Error loading prescription: " + e.getMessage());
            }
        }
    }

    @Override
    public List<AppointmentOutcome> getPendingPrescriptionOutcomes() {
        return outcomes.values().stream()
            .filter(outcome -> outcome.getPrescriptions().stream()
                .anyMatch(p -> p.getStatus().equals("Pending")))
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public boolean updatePrescriptionStatus(String appointmentId, String prescriptionId, String status) {
        AppointmentOutcome outcome = outcomes.get(appointmentId);
        if (outcome == null) return false;

        boolean updated = false;
        for (Prescription prescription : outcome.getPrescriptions()) {
            if (prescription.getPrescriptionId().equals(prescriptionId)) {
                prescription.setStatus(status);
                updated = true;
                break;
            }
        }

        if (updated) {
            savePrescriptions();
        }
        return updated;
    }

    @Override
    public AppointmentOutcome getOutcomeByAppointmentId(String appointmentId) {
        return outcomes.get(appointmentId);
    }

    private void savePrescriptions() {
        CSVWriterUtil.writeCSV(PRESCRIPTIONS_FILE, writer -> {
            writer.write("PrescriptionID,AppointmentID,PatientID,MedicationName,Dosage,Quantity,Status\n");
            for (AppointmentOutcome outcome : outcomes.values()) {
                for (Prescription prescription : outcome.getPrescriptions()) {
                    writer.write(String.format("%s,%s,%s,%s,%s,%d,%s\n",
                        prescription.getPrescriptionId(),
                        outcome.getAppointmentId(),
                        prescription.getPatient().getId(),
                        prescription.getMedicationName(),
                        prescription.getDosage(),
                        prescription.getQuantity(),
                        prescription.getStatus()
                    ));
                }
            }
        });
    }
}
