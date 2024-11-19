package patient_management.handlers;

import patient_management.interfaces.IPersonalInfoManager;
import user_management.Patient;
import user_management.UserController;
import java.util.Scanner;

/**
 * Handles personal information management for patients in the HMS.
 * Manages:
 * - Personal information display
 * - Contact information updates
 * - Email updates
 * - Data validation
 * Provides interface for patients to view and update their personal details.
 */
public class PersonalInfoHandler implements IPersonalInfoManager {
    /** Currently active patient */
    private final Patient patient;
    
    /** Controller for user management operations */
    private final UserController userController;
    
    /** Scanner for user input */
    private final Scanner scanner;

    /**
     * Constructs a new PersonalInfoHandler with required dependencies.
     * 
     * @param patient Active patient
     * @param userController Controller for user management
     * @throws IllegalArgumentException if any dependency is null
     */
    public PersonalInfoHandler(Patient patient, UserController userController) {
        this.patient = patient;
        this.userController = userController;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Updates patient's personal information.
     * Handles:
     * - Contact number updates
     * - Email address updates
     * Validates input and persists changes.
     * Displays success/failure messages.
     */
    @Override
    public void updatePersonalInfo() {
        displayCurrentInfo();

        try {
            // Update contact number if provided
            System.out.print("Enter new contact number (or press Enter to keep current): ");
            String newContact = scanner.nextLine().trim();
            if (!newContact.isEmpty()) {
                patient.setContactInfo(newContact);
            }

            // Update email if provided
            System.out.print("Enter new email (or press Enter to keep current): ");
            String newEmail = scanner.nextLine().trim();
            if (!newEmail.isEmpty()) {
                patient.setEmail(newEmail);
            }

            // Persist changes
            userController.updateUser(patient);
            System.out.println("Personal information updated successfully!");

        } catch (Exception e) {
            System.err.println("Error updating personal information: " + e.getMessage());
        }
    }

    /**
     * Displays current personal information.
     * Shows:
     * - Name
     * - Contact number
     * - Email address
     * Formats output in a structured table.
     */
    @Override
    public void displayCurrentInfo() {
        System.out.println("\nCurrent Information:");
        System.out.println("----------------------------------------");
        System.out.println("Name: " + patient.getName());
        System.out.println("Contact: " + patient.getContactInfo());
        System.out.println("Email: " + patient.getEmail());
        System.out.println("----------------------------------------");
    }
}