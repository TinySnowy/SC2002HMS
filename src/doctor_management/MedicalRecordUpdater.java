package doctor_management;

import patient_management.MedicalRecord;
import patient_management.MedicalRecordController;

import java.util.Scanner;

public class MedicalRecordUpdater {
    private MedicalRecordController recordController;

    public MedicalRecordUpdater(MedicalRecordController recordController) {
        this.recordController = recordController;
    }

    public void updateRecords(String patientId) {
        Scanner scanner = new Scanner(System.in);

        // Fetch the medical record using the controller
        MedicalRecord record = recordController.getRecordByPatientId(patientId);

        if (record != null) {
            System.out.println("Current Medical Record: " + record.getDetails());
            System.out.print("Enter new diagnosis: ");
            String diagnosis = scanner.nextLine();
            System.out.print("Enter new prescription: ");
            String prescription = scanner.nextLine();
            System.out.print("Enter any additional notes: ");
            String notes = scanner.nextLine();

            record.setDiagnosis(diagnosis);
            record.setPrescription(prescription);
//            record.setNotes(notes);

            recordController.updateRecord(record); // Update in controller
            recordController.saveRecordsToCSV("data/Medical_Record.csv"); // Save changes

            System.out.println("Medical record updated successfully!");
        } else {
            System.out.println("Patient not found.");
        }
    }
}
