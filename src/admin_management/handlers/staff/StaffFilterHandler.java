package admin_management.handlers.staff;
import admin_management.managers.StaffManager;

import user_management.*;
import java.util.List;
import java.util.Scanner;

import admin_management.utils.InputValidator;
import admin_management.utils.TableFormatter;

/**
 * Handles the filtering and display of staff members based on their roles.
 * Provides functionality to filter staff members by role (Doctor, Pharmacist, Administrator)
 * and display the filtered results in a formatted table.
 */
public class StaffFilterHandler {
    /** Controller for user-related operations (currently unused but maintained for future use) */
    @SuppressWarnings("unused")
    private final UserController userController;
    
    /** Manager for staff-specific operations and queries */
    private final StaffManager staffManager;
    
    /** Scanner for reading user input during filter selection */
    private final Scanner scanner;
    
    /** Validator for ensuring valid user input */
    private final InputValidator validator;

    /**
     * Constructs a StaffFilterHandler with the specified user controller.
     * Initializes necessary components for staff filtering operations.
     * @param userController The controller for user operations
     */
    public StaffFilterHandler(UserController userController) {
        this.userController = userController;
        this.staffManager = new StaffManager(userController);
        this.scanner = new Scanner(System.in);
        this.validator = new InputValidator();
    }

    /**
     * Displays a menu for role selection and filters staff accordingly.
     * Allows users to filter staff members by their role (Doctor/Pharmacist/Administrator).
     */
    public void filterStaffByRole() {
        // Display filter options
        System.out.println("\nSelect Role to Filter:");
        System.out.println("1. Doctors");
        System.out.println("2. Pharmacists");
        System.out.println("3. Administrators");

        // Get and validate user choice
        int choice = validator.getValidatedInput(scanner, "Enter choice (1-3): ", 1, 3);
        String role = getRoleFromChoice(choice);

        // Display filtered results
        displayFilteredStaff(role);
    }

    /**
     * Converts the numeric choice to corresponding role string.
     * @param choice User's selection (1-3)
     * @return The corresponding role as a String
     * @throws IllegalArgumentException if choice is invalid
     */
    private String getRoleFromChoice(int choice) {
        switch (choice) {
            case 1: return "Doctor";
            case 2: return "Pharmacist";
            case 3: return "Administrator";
            default: throw new IllegalArgumentException("Invalid role choice");
        }
    }

    /**
     * Displays the filtered staff list in a formatted table.
     * Shows ID, Name, and role-specific information (Specialty for Doctors, License for Pharmacists).
     * @param role The role to filter by
     */
    private void displayFilteredStaff(String role) {
        // Get filtered staff list
        List<User> filteredStaff = staffManager.getStaffByRole(role);
        if (filteredStaff.isEmpty()) {
            System.out.println("No staff members found for role: " + role);
            return;
        }

        // Display table header
        System.out.println("\nFiltered Staff List - " + role + "s:");
        int[] columnWidths = {15, 30, 30};  // Width for ID, Name, and role-specific info
        String[] headers = {"ID", "Name", role.equals("Doctor") ? "Specialty" : "License"};
        TableFormatter.printTableHeader(headers, columnWidths);
        
        // Display each staff member's details
        for (User staff : filteredStaff) {
            displayStaffMemberDetails(staff);
        }
        
        // Close the table
        TableFormatter.printTableFooter(columnWidths);
    }

    /**
     * Displays a single staff member's details in table format.
     * Handles different staff types and their specific information.
     * @param staff The staff member to display
     */
    private void displayStaffMemberDetails(User staff) {
        int[] columnWidths = {15, 30, 30};
        String[] rowData;
        
        // Format data based on staff type
        if (staff instanceof Doctor) {
            rowData = new String[]{staff.getId(), staff.getName(), ((Doctor) staff).getSpecialty()};
            TableFormatter.printRow(rowData, columnWidths);
        } else if (staff instanceof Pharmacist) {
            rowData = new String[]{staff.getId(), staff.getName(), ((Pharmacist) staff).getLicenseNumber()};
            TableFormatter.printRow(rowData, columnWidths);
        } else {
            rowData = new String[]{staff.getId(), staff.getName(), ""};
            TableFormatter.printRow(rowData, columnWidths);
        }
    }
}