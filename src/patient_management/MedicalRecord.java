package patient_management;

import user_management.Patient;

public class MedicalRecord {
    private Patient patient;
    private String diagnosis;
    private String prescription;
    private String medicalHistory;

    public MedicalRecord(Patient patient) {
        this.patient = patient;
        this.medicalHistory = "No medical records available."; // Default message
        this.diagnosis = "";
        this.prescription = "";
    }

    // Method to get the details of the medical record
    public String getDetails() {
        return "Patient ID: " + patient.getId() +
                ", Name: " + patient.getName() +
                ", Diagnosis: " + (diagnosis.isEmpty() ? "N/A" : diagnosis) +
                ", Prescription: " + (prescription.isEmpty() ? "N/A" : prescription);
    }

    // Setters for diagnosis and prescription
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
        updateMedicalHistory();
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
        updateMedicalHistory();
    }

//    public void setNotes(String notes) {
//        this.notes = notes;
//    }

    public String getPatientId() {
        return patient.getId();
    }

    public String getPatientName() {
        return patient.getName();
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getPrescription() {
        return prescription;
    }

//    public String getNotes() {
//        return notes;
//    }
    // Updates the medical history based on the latest diagnosis and prescription
    private void updateMedicalHistory() {
        this.medicalHistory = "Diagnosis: " + (diagnosis.isEmpty() ? "N/A" : diagnosis) +
                "\nPrescription: " + (prescription.isEmpty() ? "N/A" : prescription);
    }

    // Method to display the medical history
    public void displayRecord() {
        System.out.println("\nMedical Record for " + patient.getName() + ":");
        System.out.println(medicalHistory);
    }
}
