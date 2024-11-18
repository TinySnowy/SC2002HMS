package patient_management.model;

public class MedicalRecord {
    private final String patientId;
    private final String patientName;
    private String diagnosis;
    private String prescription;

    public MedicalRecord(String patientId, String patientName, String diagnosis, String prescription) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
    }

    // Getters
    public String getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getPrescription() {
        return prescription;
    }

    // Setters
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public void displayRecord() {
        System.out.println("\nMedical Record for " + patientName);
        System.out.println("----------------------------------------");
        System.out.println("Patient ID: " + patientId);
        System.out.println("Name: " + patientName);
        System.out.println("Diagnosis: " + (diagnosis.isEmpty() ? "None" : diagnosis));
        System.out.println("Prescription: " + (prescription.isEmpty() ? "None" : prescription));
        System.out.println("----------------------------------------");
    }

    @Override
    public String toString() {
        return String.format("Patient: %s (%s)\nDiagnosis: %s\nPrescription: %s",
                patientName, patientId, diagnosis, prescription);
    }
}