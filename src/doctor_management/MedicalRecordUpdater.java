package doctor_management;

import patient_management.MedicalRecord;

import java.util.Scanner;

public class MedicalRecordUpdater {
    public void updateRecords() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine();

        // Logic to fetch the patient's medical record using the ID
        MedicalRecord record = fetchMedicalRecord(patientId);

        if (record != null) {
            System.out.println("Current Medical Record: " + record.getDetails());
            System.out.print("Enter new diagnosis: ");
            String diagnosis = scanner.nextLine();
            System.out.print("Enter new prescription: ");
            String prescription = scanner.nextLine();

            record.setDiagnosis(diagnosis);
            record.setPrescription(prescription);

            System.out.println("Medical record updated successfully!");
        } else {
            System.out.println("Patient not found.");
        }
    }

    private MedicalRecord fetchMedicalRecord(String patientId) {
        // Logic to fetch the medical record from a database or list
        // For now, return a dummy record for testing
        return new MedicalRecord(patientId, "John Doe", "Flu", "Rest and hydration");
    }
}
