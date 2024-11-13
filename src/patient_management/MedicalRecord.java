package patient_management;

import user_management.Patient;

public class MedicalRecord {
    private Patient patient;
    private String medicalHistory;

    public MedicalRecord(Patient patient) {
        this.patient = patient;
        this.medicalHistory = "No medical records available."; // Default message
    }

    public void updateRecord(String record) {
        this.medicalHistory = record;
    }

    public void displayRecord() {
        System.out.println("\nMedical Record for " + patient.getName() + ":");
        System.out.println(medicalHistory);
    }
}
