package patient_management.model;

import user_management.Patient;
import java.time.format.DateTimeFormatter;

/**
 * Model class representing a patient's medical record in the HMS.
 * Contains:
 * - Patient identification
 * - Medical history
 * - Current diagnosis
 * - Active prescriptions
 * Provides structured storage and access to patient medical information.
 */
public class MedicalRecord {
    /** Unique identifier for the patient */
    private final String patientId;
    
    /** Patient's full name */
    private final String patientName;
    
    /** Current medical diagnosis */
    private String diagnosis;
    
    /** Active medical prescriptions */
    private String prescription;
    
    /** Formatter for standardized date display */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Constructs a new MedicalRecord with required patient information.
     * 
     * @param patientId Unique identifier for the patient
     * @param patientName Patient's full name
     * @param diagnosis Initial medical diagnosis
     * @param prescription Initial medical prescriptions
     * @throws IllegalArgumentException if patientId or patientName is null/empty
     */
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