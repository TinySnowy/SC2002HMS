package patient_management.handlers;

import patient_management.model.MedicalRecord;
import user_management.Patient;
import user_management.UserController;
import java.time.format.DateTimeFormatter;

/**
 * Handles viewing and display of patient medical records in the HMS.
 * Manages:
 * - Medical record display
 * - Patient information formatting
 * - Record section organization
 * - Data presentation
 * Provides formatted display of patient medical information.
 */
public class PatientDetailViewer {
    /** Controller for user management operations */
    private final UserController userController;
    
    /** Formatter for date display */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Constructs a new PatientDetailViewer.
     * Initializes user management integration.
     */
    public PatientDetailViewer() {
        this.userController = UserController.getInstance();
    }

    /**
     * Displays complete patient medical record.
     * Shows:
     * - Personal information
     * - Medical details
     * - Current diagnosis
     * - Active prescriptions
     * 
     * @param record Medical record to display
     */
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

        // Display header
        System.out.println("\n==========================================");
        System.out.println("           MEDICAL RECORD");
        System.out.println("==========================================");
        
        // Display personal information section
        displayPersonalInformation(record, patient);
        
        // Display medical information section
        displayMedicalInformation(record);
        
        System.out.println("==========================================");
    }

    /**
     * Displays patient's personal information section.
     * Shows:
     * - Patient ID
     * - Name
     * - Date of birth
     * - Gender
     * - Blood group
     * - Contact details
     * 
     * @param record Medical record containing patient data
     * @param patient Patient object with personal details
     */
    private void displayPersonalInformation(MedicalRecord record, Patient patient) {
        System.out.println("PERSONAL INFORMATION");
        System.out.println("------------------------------------------");
        System.out.println("Patient ID      : " + record.getPatientId());
        System.out.println("Name           : " + record.getPatientName());
        System.out.println("Date of Birth  : " + patient.getDateOfBirth().format(DATE_FORMATTER));
        System.out.println("Gender         : " + patient.getGender());
        System.out.println("Blood Group    : " + patient.getBloodGroup());
        System.out.println("Contact Number : " + patient.getContactInfo());
        System.out.println("Email          : " + patient.getEmail());
    }

    /**
     * Displays patient's medical information section.
     * Shows:
     * - Current diagnosis
     * - Active prescriptions
     * Handles empty fields with appropriate messaging.
     * 
     * @param record Medical record containing medical data
     */
    private void displayMedicalInformation(MedicalRecord record) {
        System.out.println("\nMEDICAL INFORMATION");
        System.out.println("------------------------------------------");
        System.out.println("Current Diagnosis   : " + 
            (record.getDiagnosis().isEmpty() ? "None" : record.getDiagnosis()));
        System.out.println("Current Prescription: " + 
            (record.getPrescription().isEmpty() ? "None" : record.getPrescription()));
    }
}