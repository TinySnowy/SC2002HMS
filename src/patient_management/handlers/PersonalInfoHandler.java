package patient_management.handlers;

import patient_management.interfaces.IPersonalInfoManager;
import user_management.Patient;
import user_management.UserController;
import java.util.Scanner;

public class PersonalInfoHandler implements IPersonalInfoManager {
    private final Patient patient;
    private final UserController userController;
    private final Scanner scanner;

    public PersonalInfoHandler(Patient patient, UserController userController) {
        this.patient = patient;
        this.userController = userController;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void updatePersonalInfo() {
        displayCurrentInfo();

        try {
            System.out.print("Enter new contact number (or press Enter to keep current): ");
            String newContact = scanner.nextLine().trim();
            if (!newContact.isEmpty()) {
                patient.setContactInfo(newContact);
            }

            System.out.print("Enter new email (or press Enter to keep current): ");
            String newEmail = scanner.nextLine().trim();
            if (!newEmail.isEmpty()) {
                patient.setEmail(newEmail);
            }

            userController.updateUser(patient);
            System.out.println("Personal information updated successfully!");

        } catch (Exception e) {
            System.err.println("Error updating personal information: " + e.getMessage());
        }
    }

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