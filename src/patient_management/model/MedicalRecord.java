package patient_management.model;

import user_management.Patient;
import java.time.format.DateTimeFormatter;

public class MedicalRecord {
    private final String patientId;
    private final String patientName;
    private String diagnosis;
    private String prescription;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

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
        System.out.println("\n==========================================");
        System.out.println("           MEDICAL RECORD");
        System.out.println("==========================================");
        
        // Personal Information Section
        System.out.println("PERSONAL INFORMATION");
        System.out.println("------------------------------------------");
        System.out.println("Patient ID      : " + patientId);
        System.out.println("Name           : " + patientName);
        
        // Medical Information Section
        System.out.println("\nMEDICAL INFORMATION");
        System.out.println("------------------------------------------");
        System.out.println("Current Diagnosis   : " + (diagnosis.isEmpty() ? "None" : diagnosis));
        System.out.println("Current Prescription: " + (prescription.isEmpty() ? "None" : prescription));
        System.out.println("==========================================");
    }

    @Override
    public String toString() {
        return String.format("Patient: %s (%s)\nDiagnosis: %s\nPrescription: %s",
                patientName, patientId, diagnosis, prescription);
    }
}