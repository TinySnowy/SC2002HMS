package admin_management.handlers.staff;

import user_management.*;
import java.util.Scanner;
import admin_management.managers.StaffManager;

/**
 * Handles the removal of staff members from the system.
 * Provides functionality to safely remove staff members with proper validation
 * and confirmation to prevent accidental deletions.
 */
public class StaffRemovalHandler {
    /** Controller for user-related operations and validation */
    private final UserController userController;
    
    /** Manager for staff-specific operations and removal */
    private final StaffManager staffManager;
    
    /** Handler for displaying staff information before removal */
    private final StaffDisplayHandler displayHandler;
    
    /** Scanner for reading user input during removal process */
    private final Scanner scanner;

    /**
     * Constructs a StaffRemovalHandler with the specified user controller.
     * Initializes necessary components for staff removal operations.
     * @param userController The controller for user operations
     */
    public StaffRemovalHandler(UserController userController) {
        this.userController = userController;
        this.staffManager = new StaffManager(userController);
        this.displayHandler = new StaffDisplayHandler(userController);
        this.scanner = new Scanner(System.in);
    }

    /**
     * Initiates the staff removal process.
     * Displays current staff list, prompts for staff ID, validates the removal,
     * and confirms with user before executing the removal.
     * 
     * @throws IllegalStateException if there's an error during the removal process
     */
    public void removeStaff() {
        // Display header
        System.out.println("\nRemove Staff Member");
        System.out.println("----------------------------------------");

        // Show current staff list for reference
        displayHandler.viewAllStaff();
        
        // Get staff ID to remove
        System.out.print("\nEnter Staff ID to remove: ");
        String id = scanner.nextLine().trim();

        try {
            // Validate and confirm removal
            validateRemoval(id);
            confirmAndRemove(id);
        } catch (Exception e) {
            throw new IllegalStateException("Error removing staff: " + e.getMessage());
        }
    }

    /**
     * Validates if the given staff ID can be removed.
     * Checks if the user exists and is not a patient.
     * 
     * @param id Staff ID to validate
     * @throws IllegalArgumentException if staff member doesn't exist or is a patient
     */
    private void validateRemoval(String id) {
        User user = userController.getUserById(id);
        if (user == null) {
            throw new IllegalArgumentException("Staff member not found");
        }
        if (user instanceof Patient) {
            throw new IllegalArgumentException("Cannot remove patients through staff management");
        }
    }

    /**
     * Confirms removal with user and executes the removal if confirmed.
     * Asks for confirmation and only proceeds with removal if user confirms with "yes".
     * 
     * @param id Staff ID to remove
     */
    private void confirmAndRemove(String id) {
        // Ask for confirmation
        System.out.print("Are you sure you want to remove this staff member? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        // Process based on confirmation
        if (confirm.equals("yes")) {
            staffManager.removeStaff(id);
            System.out.println("Staff member removed successfully");
        } else {
            System.out.println("Operation cancelled");
        }
    }
}