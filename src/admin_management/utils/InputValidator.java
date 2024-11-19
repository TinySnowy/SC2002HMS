package admin_management.utils;

import java.util.Scanner;

/**
 * Utility class for validating and sanitizing user input in the hospital management system.
 * Provides comprehensive validation for staff-related data including personal information,
 * credentials, and professional details. Ensures data integrity and format consistency.
 */
public class InputValidator {
    /**
     * Gets validated integer input within a specified range.
     * Continuously prompts user until valid input is received.
     * Handles number format exceptions and range validation.
     */
    public int getValidatedInput(Scanner scanner, String prompt, int min, int max) {
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

    /**
     * Validates staff name.
     * Ensures name is not null or empty.
     * Trims whitespace from input.
     * Used for staff registration and updates.
     */
    public String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        return name.trim();
    }

    /**
     * Validates gender input.
     * Accepts 'M' or 'F' (case-insensitive).
     * Standardizes output to uppercase.
     * Critical for maintaining consistent gender records.
     */
    public String validateGender(String gender) {
        if (gender == null) {
            throw new IllegalArgumentException("Gender cannot be null");
        }
        
        String upperGender = gender.toUpperCase().trim();
        if (!upperGender.equals("M") && !upperGender.equals("F")) {
            throw new IllegalArgumentException("Gender must be M or F");
        }
        return upperGender;
    }

    /**
     * Validates staff member age.
     * Ensures age is between 18 and 100.
     * Used for staff registration and updates.
     * Maintains reasonable age restrictions for employment.
     */
    public int validateAge(int age) {
        if (age < 18 || age > 100) {
            throw new IllegalArgumentException("Age must be between 18 and 100");
        }
        return age;
    }

    /**
     * Validates password.
     * Currently only checks for non-empty input.
     * Could be extended for password strength requirements.
     * Used in authentication and account creation.
     */
    public String validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        return password.trim();
    }

    /**
     * Validates staff ID format.
     * Ensures ID follows pattern: [D/P/A][numbers]
     * D for Doctor, P for Pharmacist, A for Admin
     * Critical for staff identification and role assignment.
     */
    public String validateStaffId(String staffId) {
        if (staffId == null || staffId.trim().isEmpty()) {
            throw new IllegalArgumentException("Staff ID cannot be empty");
        }
        
        String id = staffId.trim();
        if (id.length() < 2) {
            throw new IllegalArgumentException("Invalid staff ID format");
        }
        
        char prefix = id.charAt(0);
        if (prefix != 'D' && prefix != 'P' && prefix != 'A') {
            throw new IllegalArgumentException("Invalid staff ID prefix");
        }
        
        try {
            Integer.parseInt(id.substring(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid staff ID number format");
        }
        
        return id;
    }

    /**
     * Validates specialty or license information.
     * Used for doctor specialties and pharmacist licenses.
     * Ensures professional credentials are properly recorded.
     * Generic validation for both types to maintain code reuse.
     */
    public String validateSpecialtyOrLicense(String value, String type) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(type + " cannot be empty");
        }
        return value.trim();
    }
}