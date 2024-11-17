package admin_management.handlers.staff;

import user_management.*;
import admin_management.managers.StaffManager;
import admin_management.utils.TableFormatter;
import java.util.List;

public class StaffDisplayHandler {
    private final UserController userController;
    private final StaffManager staffManager;
    
    // Define column widths for the table
    private static final int[] COLUMN_WIDTHS = {8, 25, 15, 8, 5, 25};
    private static final String[] HEADERS = {
        "ID", "Name", "Role", "Gender", "Age", "Additional Info"
    };

    public StaffDisplayHandler(UserController userController) {
        this.userController = userController;
        this.staffManager = new StaffManager(userController);
    }

    public void viewAllStaff() {
        List<User> allStaff = staffManager.getAllStaff();
        if (allStaff.isEmpty()) {
            System.out.println("\nNo staff members found.");
            return;
        }

        System.out.println("\nStaff List");
        TableFormatter.printTableHeader(HEADERS, COLUMN_WIDTHS);

        for (User staff : allStaff) {
            displayStaffMember(staff);
        }

        TableFormatter.printTableFooter(COLUMN_WIDTHS);
        System.out.println("Total Staff Members: " + allStaff.size());
    }

    private void displayStaffMember(User staff) {
        String[] rowData = {
            staff.getId(),
            staff.getName(),
            staff.getRole(),
            staff.getGender(),
            String.valueOf(staff.getAge()),
            getStaffAdditionalInfo(staff)
        };
        TableFormatter.printRow(rowData, COLUMN_WIDTHS);
    }

    public void displayFilteredStaff(List<User> filteredStaff, String filterCriteria) {
        if (filteredStaff.isEmpty()) {
            System.out.println("\nNo staff members found matching the criteria: " + filterCriteria);
            return;
        }

        System.out.println("\nFiltered Staff List - " + filterCriteria);
        TableFormatter.printTableHeader(HEADERS, COLUMN_WIDTHS);

        for (User staff : filteredStaff) {
            displayStaffMember(staff);
        }

        TableFormatter.printTableFooter(COLUMN_WIDTHS);
        System.out.println("Total Matching Staff Members: " + filteredStaff.size());
    }

    private String getStaffAdditionalInfo(User staff) {
        if (staff instanceof Doctor) {
            return "Specialty: " + ((Doctor) staff).getSpecialty();
        } else if (staff instanceof Pharmacist) {
            return "License: " + ((Pharmacist) staff).getLicenseNumber();
        }
        return "-";
    }

    public void displayStaffDetails(String staffId) {
        User staff = userController.getUserById(staffId);
        if (staff == null || staff instanceof Patient) {
            System.out.println("Staff member not found.");
            return;
        }

        System.out.println("\nDetailed Staff Information");
        System.out.println("━".repeat(50));
        System.out.printf("ID:     %-30s%n", staff.getId());
        System.out.printf("Name:   %-30s%n", staff.getName());
        System.out.printf("Role:   %-30s%n", staff.getRole());
        System.out.printf("Gender: %-30s%n", staff.getGender());
        System.out.printf("Age:    %-30d%n", staff.getAge());

        if (staff instanceof Doctor) {
            Doctor doctor = (Doctor) staff;
            System.out.printf(  "Specialty: %-30s%n", doctor.getSpecialty());
        } else if (staff instanceof Pharmacist) {
            Pharmacist pharmacist = (Pharmacist) staff;
            System.out.printf("License Number: %-30s%n", pharmacist.getLicenseNumber());
        }
        System.out.println("━".repeat(50));
    }
}