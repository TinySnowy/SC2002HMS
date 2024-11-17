package admin_management.handlers.staff;

import user_management.*;
import java.util.Scanner;

import admin_management.utils.InputValidator;
import admin_management.managers.StaffManager;

public class StaffCreationHandler {
    @SuppressWarnings("unused")
    private final UserController userController;
    private final StaffManager staffManager;
    private final Scanner scanner;
    private final InputValidator validator;

    public StaffCreationHandler(UserController userController) {
        this.userController = userController;
        this.staffManager = new StaffManager(userController);
        this.scanner = new Scanner(System.in);
        this.validator = new InputValidator();
    }

    public void addNewStaff() {
        try {
            System.out.println("\nAdd New Staff Member");
            System.out.println("----------------------------------------");

            // Get basic information
            String id = getUniqueStaffId();
            System.out.print("Enter Name: ");
            String name = validator.validateName(scanner.nextLine());

            System.out.print("Enter Gender (M/F): ");
            String gender = validator.validateGender(scanner.nextLine());

            System.out.print("Enter Age (18-100): ");
            int age = validator.validateAge(Integer.parseInt(scanner.nextLine().trim()));

            System.out.print("Enter Password: ");
            String password = validator.validatePassword(scanner.nextLine());

            // Create staff member
            User newStaff = createStaffMember(id, name, password, gender, age);
            System.out.println(newStaff);
            if (newStaff != null) {
                staffManager.addStaff(newStaff);
                System.out.println("Staff member added successfully!");
                System.out.println("Staff ID: " + id);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid age format");
        } catch (Exception e) {
            throw new IllegalStateException("Error adding staff: " + e.getMessage());
        }
    }

    private String getUniqueStaffId() {
        System.out.println("Select Role:");
        System.out.println("1. Doctor (1)");
        System.out.println("2. Pharmacist (2)");
        System.out.println("3. Administrator (3)");

        int roleChoice = validator.getValidatedInput(scanner, "Enter choice (1-3): ", 1, 3);
        String prefix = getRolePrefix(roleChoice);
        return generateUniqueId(prefix);
    }

    private String getRolePrefix(int roleChoice) {
        switch (roleChoice) {
            case 1:
                return "D";
            case 2:
                return "P";
            case 3:
                return "A";
            default:
                throw new IllegalArgumentException("Invalid role choice");
        }
    }

    private String generateUniqueId(String prefix) {
        int maxId = staffManager.getAllStaff().stream()
                .map(User::getId)
                .filter(id -> id.startsWith(prefix))
                .map(id -> {
                    try {
                        return Integer.parseInt(id.substring(1));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max(Integer::compareTo)
                .orElse(0);

        return String.format("%s%03d", prefix, maxId + 1);
    }

    private User createStaffMember(String id, String name, String password, String gender, int age) {
        String role = id.substring(0, 1);
        User newStaff;

        switch (role) {
            case "D":
                System.out.print("Enter Medical Specialty: ");
                String specialty = scanner.nextLine().trim();
                newStaff = new Doctor(id, name, password, specialty, gender, age, true);
                break;
            case "P":
                System.out.print("Enter License Number: ");
                String license = scanner.nextLine().trim();
                newStaff = new Pharmacist(id, name, password, license, gender, age, true);
                break;
            case "A":
                newStaff = new Administrator(id, name, password, gender, age, true);
                break;
            default:
                throw new IllegalArgumentException("Invalid role prefix");
        }
        return newStaff;
    }
}