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
        System.out.println("2. View Appointments");
        System.out.println("3. Schedule New Appointment");
        System.out.println("4. Manage Existing Appointments");
        System.out.println("5. View Appointment Outcomes");
        System.out.println("6. Update Personal Information");
        System.out.println("7. Log Out");
        System.out.println("----------------------------------------");
    }

    public int getValidatedInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int input = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf("Please enter a number between %d and %d%n", min, max);
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    public void displayError(String message) {
        System.err.println("Error: " + message);
    }

    public void displaySuccess(String message) {
        System.out.println("Success: " + message);
    }

    public void displayDivider() {
        System.out.println("----------------------------------------");
    }

    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}