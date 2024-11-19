package patient_management.utils;

import user_management.Patient;
import java.util.Scanner;

public class PatientMenuPrinter {
    private final Scanner scanner;

    public PatientMenuPrinter() {
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu(Patient patient) {
        System.out.println("\nPatient Dashboard - " + patient.getName());
        System.out.println("----------------------------------------");
        System.out.println("1. View Medical Records");
        System.out.println("2. View All Appointments");
        System.out.println("3. Schedule New Appointment");
        System.out.println("4. Manage Existing Appointments");
        System.out.println("5. View Appointment Outcomes");
        System.out.println("6. Update Personal Information");
        System.out.println("7. Submit Appointment Feedback");  // New option
        System.out.println("8. View My Feedback History");     // New option
        System.out.println("9. Logout");
        System.out.println("----------------------------------------");
    }

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