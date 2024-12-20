package admin_management.handlers.staff;

import user_management.*;
import java.util.Scanner;
import admin_management.utils.InputValidator;
import admin_management.managers.StaffManager;

/**
 * Handles the creation of new staff members in the system.
 * Supports creation of Doctors, Pharmacists, and Administrators with appropriate validation.
 */
public class StaffCreationHandler {
    /** Controller for user-related operations */
    private final UserController userController;
    /** Manager for staff-specific operations */
    private final StaffManager staffManager;
    /** Scanner for reading user input */
    private final Scanner scanner;
    /** Validator for input validation */
    private final InputValidator validator;

    /**
     * Constructs a StaffCreationHandler with the specified user controller.
     * @param userController The controller for user operations
     */
    public StaffCreationHandler(UserController userController) {
        this.userController = userController;
        this.staffManager = new StaffManager(userController);
        this.scanner = new Scanner(System.in);
        this.validator = new InputValidator();
    }

    /**
     * Handles the process of adding a new staff member.
     * Collects and validates all required information based on staff role.
     * Creates appropriate staff object and adds to the system.
     * 
     * Process:
     * 1. Select staff role (Doctor/Pharmacist/Administrator)
     * 2. Enter staff ID with role-specific prefix
     * 3. Enter basic information (name, gender, age)
     * 4. Enter role-specific information (specialty/license)
     * 5. Create and save staff member
     */
    public void addNewStaff() {
        try {
            System.out.println("\nAdd New Staff Member");
            System.out.println("----------------------------------------");
            System.out.println("Select Role:");
            System.out.println("1. Doctor");
            System.out.println("2. Pharmacist");
            System.out.println("3. Administrator");
            System.out.print("Enter choice (1-3): ");
            int roleChoice = Integer.parseInt(scanner.nextLine().trim());

            String rolePrefix;
            switch (roleChoice) {
                case 1:
                    rolePrefix = "D";
                    break;
                case 2:
                    rolePrefix = "P";
                    break;
                case 3:
                    rolePrefix = "A";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid role choice");
            }

            // Role-specific ID prefix validation
            System.out.print("Enter Staff ID (" + rolePrefix + "XXX format): ");
            String id = scanner.nextLine().trim().toUpperCase();
            if (!id.startsWith(rolePrefix) || id.length() != 4) {
                throw new IllegalArgumentException("Invalid ID format. Should be " + rolePrefix + "XXX");
            }

            // Basic staff information
            System.out.print("Enter Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter Gender (M/F): ");
            String gender = scanner.nextLine().trim().toUpperCase();
            if (!gender.equals("M") && !gender.equals("F")) {
                throw new IllegalArgumentException("Gender must be M or F");
            }

            System.out.print("Enter Age (18-100): ");
            int age = Integer.parseInt(scanner.nextLine().trim());
            if (age < 18 || age > 100) {
                throw new IllegalArgumentException("Age must be between 18 and 100");
            }

            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();
            if (password.isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }

            User newStaff;
            switch (roleChoice) {
                case 1:
                    System.out.print("Enter Medical Specialty: ");
                    String specialty = scanner.nextLine().trim();
                    newStaff = new Doctor(id, name, password, specialty, gender, age, true);
                    break;
                case 2:
                    System.out.print("Enter License Number: ");
                    String license = scanner.nextLine().trim();
                    newStaff = new Pharmacist(id, name, password, license, gender, age, true);
                    break;
                case 3:
                    newStaff = new Administrator(id, name, password, gender, age, true);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid role choice");
            }

            staffManager.addStaff(newStaff);
            System.out.println("\nStaff member added successfully!");
            System.out.println("Staff ID: " + id);
            System.out.println("Name: " + name);
            System.out.println("Role: " + newStaff.getRole());

        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid number");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}