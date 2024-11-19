package admin_management.handlers.staff;

import user_management.*;
import admin_management.managers.StaffManager;
import admin_management.utils.TableFormatter;
import java.util.List;

/**
 * Handles the display of staff information in various formats.
 * Provides functionality to view all staff, filtered staff lists, and detailed staff information.
 */
public class StaffDisplayHandler {
    /** Controller for user-related operations */
    private final UserController userController;
    /** Manager for staff-specific operations */
    private final StaffManager staffManager;
    
    /** Defines the width of each column in the display table:
     * ID(8), Name(25), Role(15), Gender(8), Age(5), Additional Info(25) */
    private static final int[] COLUMN_WIDTHS = {8, 25, 15, 8, 5, 25};
    /** Defines the headers for each column in the display table */
    private static final String[] HEADERS = {
        "ID", "Name", "Role", "Gender", "Age", "Additional Info"
    };

    /**
     * Constructs a StaffDisplayHandler with the specified user controller.
     * @param userController The controller for user operations
     */
    public StaffDisplayHandler(UserController userController) {
        this.userController = userController;
        this.staffManager = new StaffManager(userController);
    }

    /**
     * Displays a list of all staff members in a formatted table.
     * If no staff members exist, displays an appropriate message.
     */
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

    /**
     * Displays a single staff member's information in table row format.
     * @param staff The staff member to display
     */
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

    /**
     * Displays a filtered list of staff members based on specific criteria.
     * @param filteredStaff List of staff members matching the filter criteria
     * @param filterCriteria Description of the applied filter
     */
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

    /**
     * Retrieves role-specific additional information for a staff member.
     * @param staff The staff member
     * @return Specialty for doctors, license number for pharmacists, or "-" for others
     */
    private String getStaffAdditionalInfo(User staff) {
        if (staff instanceof Doctor) {
            return "Specialty: " + ((Doctor) staff).getSpecialty();
        } else if (staff instanceof Pharmacist) {
            return "License: " + ((Pharmacist) staff).getLicenseNumber();
        }
        return "-";
    }

    /**
     * Displays detailed information for a specific staff member.
     * Shows all basic information plus role-specific details.
     * @param staffId The ID of the staff member to display
     */
    public void displayStaffDetails(String staffId) {
        User staff = userController.getUserById(staffId);
        if (staff == null || staff instanceof Patient) {
            System.out.println("Staff member not found.");
            return;
        }

        // Display header and basic information
        System.out.println("\nDetailed Staff Information");
        System.out.println("━".repeat(50));
        System.out.printf("ID:     %-30s%n", staff.getId());
        System.out.printf("Name:   %-30s%n", staff.getName());
        System.out.printf("Role:   %-30s%n", staff.getRole());
        System.out.printf("Gender: %-30s%n", staff.getGender());
        System.out.printf("Age:    %-30d%n", staff.getAge());

        // Display role-specific information
        if (staff instanceof Doctor) {
            Doctor doctor = (Doctor) staff;
            System.out.printf("Specialty: %-30s%n", doctor.getSpecialty());
        } else if (staff instanceof Pharmacist) {
            Pharmacist pharmacist = (Pharmacist) staff;
            System.out.printf("License Number: %-30s%n", pharmacist.getLicenseNumber());
        }
        System.out.println("━".repeat(50));
    }
}