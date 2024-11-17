package admin_management.utils;

import java.util.Scanner;

public class InputValidator {
    /**
     * Gets validated integer input within a specified range
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
     * Validates staff name
     * @throws IllegalArgumentException if name is null or empty
     */
    public String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        return name.trim();
    }

    /**
     * Validates gender input
     * @throws IllegalArgumentException if gender is not 'M' or 'F'
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
     * Validates age within the range 18-100
     * @throws IllegalArgumentException if age is outside valid range
     */
    public int validateAge(int age) {
        if (age < 18 || age > 100) {
            throw new IllegalArgumentException("Age must be between 18 and 100");
        }
        return age;
    }

    /**
     * Validates password
     * @throws IllegalArgumentException if password is null or empty
     */
    public String validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        return password.trim();
    }

    /**
     * Validates staff ID format
     * @throws IllegalArgumentException if ID is invalid
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
     * Validates specialty or license number
     * @throws IllegalArgumentException if value is null or empty
     */
    public String validateSpecialtyOrLicense(String value, String type) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(type + " cannot be empty");
        }
        return value.trim();
    }
}