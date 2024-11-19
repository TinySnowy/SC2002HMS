package patient_management.utils;

import user_management.Patient;
import java.util.Scanner;

/**
 * Utility class for printing patient menu interfaces in the HMS.
 * Handles:
 * - Menu display
 * - Input validation
 * - Error messaging
 * - User interface formatting
 * Provides consistent menu presentation for patient interactions.
 */
public class PatientMenuPrinter {
    /** Scanner for user input handling */
    private final Scanner scanner;

    /**
     * Constructs a new PatientMenuPrinter.
     * Initializes input scanner for menu interactions.
     */
    public PatientMenuPrinter() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the main patient dashboard menu.
     * Shows options for:
     * - Medical record access
     * - Appointment management
     * - Personal information
     * - Feedback submission
     * - System navigation
     * 
     * @param patient Current patient user
     */
    public void displayMenu(Patient patient) {
        System.out.println("\nPatient Dashboard - " + patient.getName());
        System.out.println("----------------------------------------");
        System.out.println("1. View Medical Records");
        System.out.println("2. View All Appointments");
        System.out.println("3. Schedule New Appointment");
        System.out.println("4. Manage Existing Appointments");
        System.out.println("5. View Appointment Outcomes");
        System.out.println("6. Update Personal Information");
        System.out.println("7. Submit Appointment Feedback");
        System.out.println("8. View My Feedback History");
        System.out.println("9. Logout");
        System.out.println("----------------------------------------");
    }

    /**
     * Gets and validates user menu input.
     * Ensures:
     * - Input is numeric
     * - Input is within valid range
     * - Error handling for invalid input
     * 
     * @param prompt Message to display for input
     * @param min Minimum valid input value
     * @param max Maximum valid input value
     * @return Validated user input
     */
    public int getValidatedInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int input = Integer.parseInt(scanner.nextLine().trim());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf("Please enter a number between %d and %d%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    public void displayError(String message) {
        System.out.println("\nError: " + message);
    }

    public void close() {
        scanner.close();
    }
}