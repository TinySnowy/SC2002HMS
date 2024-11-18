package patient_management.handlers;

import patient_management.model.MedicalRecord;
import user_management.Patient;
import user_management.UserController;
import java.time.format.DateTimeFormatter;

public class PatientDetailViewer {
    private final UserController userController;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public PatientDetailViewer() {
        this.userController = UserController.getInstance();
    }

    public void displayFullPatientRecord(MedicalRecord record) {
        if (record == null) {
            System.out.println("No medical record found.");
            return;
        }

        // Get patient details
        Patient patient = (Patient) userController.getUserById(record.getPatientId());
        if (patient == null) {
            System.out.println("Patient details not found.");
            return;
        }

        System.out.println("\n==========================================");
        System.out.println("           MEDICAL RECORD");
        System.out.println("==========================================");
        
        // Personal Information Section
        System.out.println("PERSONAL INFORMATION");
        System.out.println("------------------------------------------");
        System.out.println("Patient ID      : " + record.getPatientId());
        System.out.println("Name           : " + record.getPatientName());
        System.out.println("Date of Birth  : " + patient.getDateOfBirth().format(DATE_FORMATTER));
        System.out.println("Gender         : " + patient.getGender());
        System.out.println("Blood Group    : " + patient.getBloodGroup());
        System.out.println("Contact Number : " + patient.getContactInfo());
        System.out.println("Email          : " + patient.getEmail());
        
        // Medical Information Section
        System.out.println("\nMEDICAL INFORMATION");
        System.out.println("------------------------------------------");
        System.out.println("Current Diagnosis   : " + (record.getDiagnosis().isEmpty() ? "None" : record.getDiagnosis()));
        System.out.println("Current Prescription: " + (record.getPrescription().isEmpty() ? "None" : record.getPrescription()));
        System.out.println("==========================================");
    }
}