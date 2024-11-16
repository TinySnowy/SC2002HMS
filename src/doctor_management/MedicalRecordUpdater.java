package doctor_management;

import patient_management.MedicalRecord;
import patient_management.MedicalRecordController;
import java.util.Scanner;
import java.io.Closeable;

public class MedicalRecordUpdater implements Closeable {
    private final MedicalRecordController recordController;
    private final Scanner scanner;

    public MedicalRecordUpdater(MedicalRecordController recordController) {
        this.recordController = recordController;
        this.scanner = new Scanner(System.in);
    }

    public void updateRecords(String patientId) {
        try {
            // Fetch the medical record using the controller
            MedicalRecord record = recordController.getRecordByPatientId(patientId);
            if (record == null) {
                System.out.println("Patient not found.");
                return;
            }

            // Display current record
            System.out.println("\nCurrent Medical Record:");
            System.out.println("----------------------------------------");
            System.out.println("Patient ID: " + record.getPatientId());
            System.out.println("Patient Name: " + record.getPatientName());
            System.out.println("Current Diagnosis: " +
                    (record.getDiagnosis().isEmpty() ? "None" : record.getDiagnosis()));
            System.out.println("Current Prescription: " +
                    (record.getPrescription().isEmpty() ? "None" : record.getPrescription()));
            System.out.println("----------------------------------------");

            // Get new information
            System.out.print("\nEnter new diagnosis (or press Enter to keep current): ");
            String newDiagnosis = scanner.nextLine().trim();

            System.out.print("Enter new prescription (or press Enter to keep current): ");
            String newPrescription = scanner.nextLine().trim();

            // Update only if new values are provided
            if (!newDiagnosis.isEmpty() || !newPrescription.isEmpty()) {
                String diagnosis = newDiagnosis.isEmpty() ? record.getDiagnosis() : newDiagnosis;
                String prescription = newPrescription.isEmpty() ? record.getPrescription() : newPrescription;

                // Update the record
                recordController.updateRecord(patientId, diagnosis, prescription);

                System.out.println("\nMedical record updated successfully!");
                System.out.println("----------------------------------------");
                System.out.println("Updated Record:");
                System.out.println("Diagnosis: " + diagnosis);
                System.out.println("Prescription: " + prescription);
                System.out.println("----------------------------------------");
            } else {
                System.out.println("No changes made to the medical record.");
            }

        } catch (Exception e) {
            System.err.println("Error updating medical record: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}