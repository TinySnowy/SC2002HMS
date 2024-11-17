package admin_management.handlers.staff;

import user_management.*;
import java.util.Scanner;
import admin_management.managers.StaffManager;

public class StaffRemovalHandler {
    private final UserController userController;
    private final StaffManager staffManager;
    private final StaffDisplayHandler displayHandler;
    private final Scanner scanner;

    public StaffRemovalHandler(UserController userController) {
        this.userController = userController;
        this.staffManager = new StaffManager(userController);
        this.displayHandler = new StaffDisplayHandler(userController);
        this.scanner = new Scanner(System.in);
    }

    public void removeStaff() {
        System.out.println("\nRemove Staff Member");
        System.out.println("----------------------------------------");

        displayHandler.viewAllStaff();
        System.out.print("\nEnter Staff ID to remove: ");
        String id = scanner.nextLine().trim();

        try {
            validateRemoval(id);
            confirmAndRemove(id);
        } catch (Exception e) {
            throw new IllegalStateException("Error removing staff: " + e.getMessage());
        }
    }

    private void validateRemoval(String id) {
        User user = userController.getUserById(id);
        if (user == null) {
            throw new IllegalArgumentException("Staff member not found");
        }
        if (user instanceof Patient) {
            throw new IllegalArgumentException("Cannot remove patients through staff management");
        }
    }

    private void confirmAndRemove(String id) {
        System.out.print("Are you sure you want to remove this staff member? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes")) {
            staffManager.removeStaff(id);
            System.out.println("Staff member removed successfully");
        } else {
            System.out.println("Operation cancelled");
        }
    }
}