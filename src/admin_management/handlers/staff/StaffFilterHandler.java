package admin_management.handlers.staff;
import admin_management.managers.StaffManager;

import user_management.*;
import java.util.List;
import java.util.Scanner;

import admin_management.utils.InputValidator;
import admin_management.utils.TableFormatter;

public class StaffFilterHandler {
    @SuppressWarnings("unused")
    private final UserController userController;
    private final StaffManager staffManager;
    private final Scanner scanner;
    private final InputValidator validator;

    public StaffFilterHandler(UserController userController) {
        this.userController = userController;
        this.staffManager = new StaffManager(userController);
        this.scanner = new Scanner(System.in);
        this.validator = new InputValidator();
    }

    public void filterStaffByRole() {
        System.out.println("\nSelect Role to Filter:");
        System.out.println("1. Doctors");
        System.out.println("2. Pharmacists");
        System.out.println("3. Administrators");

        int choice = validator.getValidatedInput(scanner, "Enter choice (1-3): ", 1, 3);
        String role = getRoleFromChoice(choice);

        displayFilteredStaff(role);
    }

    private String getRoleFromChoice(int choice) {
        switch (choice) {
            case 1: return "Doctor";
            case 2: return "Pharmacist";
            case 3: return "Administrator";
            default: throw new IllegalArgumentException("Invalid role choice");
        }
    }

    private void displayFilteredStaff(String role) {
        List<User> filteredStaff = staffManager.getStaffByRole(role);
        if (filteredStaff.isEmpty()) {
            System.out.println("No staff members found for role: " + role);
            return;
        }

        System.out.println("\nFiltered Staff List - " + role + "s:");
        int[] columnWidths = {15, 30, 30};
        String[] headers = {"ID", "Name", role.equals("Doctor") ? "Specialty" : "License"};
        TableFormatter.printTableHeader(headers, columnWidths);
        
        for (User staff : filteredStaff) {
            displayStaffMemberDetails(staff);
        }
        TableFormatter.printTableFooter(columnWidths);
    }

    private void displayStaffMemberDetails(User staff) {
        int[] columnWidths = {15, 30, 30};
        String[] rowData;
        
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