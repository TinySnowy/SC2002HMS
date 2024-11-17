package admin_management;

import user_management.*;
import pharmacy_management.*;
import appointment_management.*;
import java.time.LocalDate;
import java.util.*;

public class AdminDashboard {
    private final StaffManager staffManager;
    private final InventoryManager inventoryManager;
    private final IReplenishmentService replenishmentService;
    private final AppointmentList appointmentList;
    private final UserController userController;
    private final Scanner scanner;

    // Constructor
    public AdminDashboard(UserController userController, InventoryManager inventoryManager,
            IReplenishmentService replenishmentService, AppointmentList appointmentList) {
        this.userController = userController;
        this.staffManager = new StaffManager(userController);
        this.inventoryManager = inventoryManager;
        this.replenishmentService = replenishmentService;
        this.appointmentList = appointmentList;
        this.scanner = new Scanner(System.in);
    }

    // Main Dashboard Methods
    public void showDashboard() {
        boolean running = true;
        while (running) {
            try {
                displayMainMenu();
                int choice = getValidatedInput("Enter choice: ", 1, 9);
                running = handleMainMenuChoice(choice);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("\nAdmin Dashboard");
        System.out.println("----------------------------------------");
        System.out.println("1. Staff Management");
        System.out.println("2. Inventory Management");
        System.out.println("3. Appointment Management");
        System.out.println("4. Exit");
        System.out.println("----------------------------------------");
    }

    private boolean handleMainMenuChoice(int choice) {
        switch (choice) {
            case 1:
                showStaffManagement();
                break;
            case 2:
                showInventoryManagement();
                break;
            case 3:
                showAppointmentManagement();
                break;
            case 4:
                System.out.println("Logging out from Admin Dashboard...");
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        return true;
    }

    // Staff Management Methods
    private void showStaffManagement() {
        while (true) {
            System.out.println("\nStaff Management");
            System.out.println("----------------------------------------");
            System.out.println("1. View All Staff");
            System.out.println("2. Add New Staff");
            System.out.println("3. Remove Staff");
            System.out.println("4. Filter Staff by Role");
            System.out.println("5. Back to Main Menu");
            System.out.println("----------------------------------------");

            int choice = getValidatedInput("Enter choice: ", 1, 5);
            if (choice == 5)
                break;

            switch (choice) {
                case 1:
                    viewAllStaff();
                    break;
                case 2:
                    addNewStaff();
                    break;
                case 3:
                    removeStaff();
                    break;
                case 4:
                    filterStaffByRole();
                    break;
            }
        }
    }

    // Inventory Management Methods
    private void showInventoryManagement() {
        while (true) {
            System.out.println("\nInventory Management");
            System.out.println("----------------------------------------");
            System.out.println("1. View Current Inventory");
            System.out.println("2. View Low Stock Alerts");
            System.out.println("3. Manage Replenishment Requests");
            System.out.println("4. Back to Main Menu");
            System.out.println("----------------------------------------");

            int choice = getValidatedInput("Enter choice: ", 1, 4);
            if (choice == 4)
                break;

            switch (choice) {
                case 1:
                    viewInventory();
                    break;
                case 2:
                    viewLowStockAlerts();
                    break;
                case 3:
                    manageReplenishmentRequests();
                    break;
            }
        }
    }

    // Appointment Management Methods
    private void showAppointmentManagement() {
        while (true) {
            System.out.println("\nAppointment Management");
            System.out.println("----------------------------------------");
            System.out.println("1. View All Appointments");
            System.out.println("2. View Completed Appointments");
            System.out.println("3. Back to Main Menu");
            System.out.println("----------------------------------------");

            int choice = getValidatedInput("Enter choice: ", 1, 3);
            if (choice == 3)
                break;

            switch (choice) {
                case 1:
                    viewAllAppointments();
                    break;
                case 2:
                    viewCompletedAppointments();
                    break;
            }
        }
    }

    // Helper Methods
    private int getValidatedInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int input = Integer.parseInt(scanner.nextLine().trim());
                if (input >= min && input <= max)
                    return input;
                System.out.printf("Please enter a number between %d and %d%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    // ... continuing from previous code ...

    // Staff Management Implementation Methods
    private void viewAllStaff() {
        List<User> allStaff = staffManager.getAllStaff();
        if (allStaff.isEmpty()) {
            System.out.println("No staff members found.");
            return;
        }

        System.out.println("\nAll Staff Members:");
        System.out.println("--------------------------------------------------------------");
        System.out.printf("%-10s %-20s %-15s %-10s %-10s %-20s%n",
                "ID", "Name", "Role", "Gender", "Age", "Additional Info");
        System.out.println("--------------------------------------------------------------");

        for (User staff : allStaff) {
            String additionalInfo = getStaffAdditionalInfo(staff);
            System.out.printf("%-10s %-20s %-15s %-10s %-10d %-20s%n",
                    staff.getId(),
                    staff.getName(),
                    staff.getRole(),
                    staff.getGender(),
                    staff.getAge(),
                    additionalInfo);
        }
        System.out.println("--------------------------------------------------------------");
    }

    private String getStaffAdditionalInfo(User staff) {
        if (staff instanceof Doctor) {
            return "Specialty: " + ((Doctor) staff).getSpecialty();
        } else if (staff instanceof Pharmacist) {
            return "License: " + ((Pharmacist) staff).getLicenseNumber();
        }
        return "";
    }

    private void addNewStaff() {
        try {
            System.out.println("\nAdd New Staff Member");
            System.out.println("----------------------------------------");

            // Get basic information
            String id = getUniqueStaffId();
            System.out.print("Enter Name: ");
            String name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }

            System.out.print("Enter Gender (M/F): ");
            String gender = scanner.nextLine().trim().toUpperCase();
            if (!gender.equals("M") && !gender.equals("F")) {
                throw new IllegalArgumentException("Gender must be M or F");
            }

            int age = getValidatedInput("Enter Age (18-100): ", 18, 100);

            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();
            if (password.isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }

            // Get role-specific information and create staff member
            User newStaff = createStaffMember(id, name, password, gender, age);
            if (newStaff != null) {
                staffManager.addStaff(newStaff);
                System.out.println("Staff member added successfully!");
                System.out.println("Staff ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("Error adding staff: " + e.getMessage());
        }
    }

    private String getUniqueStaffId() {
        String prefix = "";
        System.out.println("Select Role:");
        System.out.println("1. Doctor (D)");
        System.out.println("2. Pharmacist (P)");
        System.out.println("3. Administrator (A)");

        int roleChoice = getValidatedInput("Enter choice (1-3): ", 1, 3);
        switch (roleChoice) {
            case 1:
                prefix = "D";
                break;
            case 2:
                prefix = "P";
                break;
            case 3:
                prefix = "A";
                break;
            default:
                throw new IllegalArgumentException("Invalid role choice");
        }

        // Get current max ID for the role and increment
        List<User> existingStaff = staffManager.getAllStaff();
        int maxId = 0;

        for (User staff : existingStaff) {
            String id = staff.getId();
            if (id.startsWith(prefix)) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    maxId = Math.max(maxId, num);
                } catch (NumberFormatException ignored) {
                }
            }
        }

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

    private void removeStaff() {
        System.out.println("\nRemove Staff Member");
        System.out.println("----------------------------------------");

        viewAllStaff();
        System.out.print("\nEnter Staff ID to remove: ");
        String id = scanner.nextLine().trim();

        try {
            User user = userController.getUserById(id);
            if (user == null) {
                throw new IllegalArgumentException("Staff member not found");
            }
            if (user instanceof Patient) {
                throw new IllegalArgumentException("Cannot remove patients through staff management");
            }

            System.out.print("Are you sure you want to remove this staff member? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if (confirm.equals("yes")) {
                staffManager.removeStaff(id);
                System.out.println("Staff member removed successfully");
            } else {
                System.out.println("Operation cancelled");
            }
        } catch (Exception e) {
            System.err.println("Error removing staff: " + e.getMessage());
        }
    }

    private void filterStaffByRole() {
        System.out.println("\nSelect Role to Filter:");
        System.out.println("1. Doctors");
        System.out.println("2. Pharmacists");
        System.out.println("3. Administrators");

        int choice = getValidatedInput("Enter choice (1-3): ", 1, 3);
        String role;

        switch (choice) {
            case 1:
                role = "Doctor";
                break;
            case 2:
                role = "Pharmacist";
                break;
            case 3:
                role = "Administrator";
                break;
            default:
                throw new IllegalArgumentException("Invalid role choice");
        }

        List<User> filteredStaff = staffManager.getStaffByRole(role);
        if (filteredStaff.isEmpty()) {
            System.out.println("No staff members found for role: " + role);
            return;
        }

        System.out.println("\nFiltered Staff List - " + role + "s:");
        System.out.println("----------------------------------------");
        for (User staff : filteredStaff) {
            System.out.printf("ID: %-10s Name: %-20s%n", staff.getId(), staff.getName());
            if (staff instanceof Doctor) {
                System.out.printf("Specialty: %s%n", ((Doctor) staff).getSpecialty());
            } else if (staff instanceof Pharmacist) {
                System.out.printf("License: %s%n", ((Pharmacist) staff).getLicenseNumber());
            }
            System.out.println("----------------------------------------");
        }
    }

    // Inventory Management Implementation Methods
    private void viewInventory() {
        System.out.println("\nCurrent Inventory Status:");
        System.out.println("----------------------------------------");
        inventoryManager.checkInventory();
    }

    private void viewLowStockAlerts() {
        System.out.println("\nLow Stock Alerts:");
        System.out.println("----------------------------------------");
        inventoryManager.checkAllLowStock();
    }

    private void manageReplenishmentRequests() {
        while (true) {
            System.out.println("\nReplenishment Request Management");
            System.out.println("----------------------------------------");
            System.out.println("1. View Pending Requests");
            System.out.println("2. Approve Request");
            System.out.println("3. Reject Request");
            System.out.println("4. Back");

            int choice = getValidatedInput("Enter choice (1-4): ", 1, 4);
            if (choice == 4)
                break;

            switch (choice) {
                case 1:
                    viewPendingRequests();
                    break;
                case 2:
                    approveReplenishmentRequest();
                    break;
                case 3:
                    rejectReplenishmentRequest();
                    break;
            }
        }
    }

    private void viewPendingRequests() {
        List<ReplenishmentRequest> pendingRequests = replenishmentService.getPendingRequests();
        if (pendingRequests.isEmpty()) {
            System.out.println("No pending replenishment requests found.");
            return;
        }

        System.out.println("\nPending Replenishment Requests:");
        System.out.println("----------------------------------------");
        for (ReplenishmentRequest request : pendingRequests) {
            displayReplenishmentRequest(request);
        }
    }

    private void displayReplenishmentRequest(ReplenishmentRequest request) {
        System.out.println("Request ID: " + request.getRequestId());
        System.out.println("Medication: " + request.getMedicationName());
        System.out.println("Quantity: " + request.getQuantity());
        System.out.println("Pharmacist ID: " + request.getPharmacistId());
        System.out.println("Date: " + request.getRequestDate());
        System.out.println("----------------------------------------");
    }

    private void approveReplenishmentRequest() {
        try {
            viewPendingRequests();
            System.out.print("\nEnter Request ID to approve: ");
            String requestId = scanner.nextLine().trim();

            ReplenishmentRequest request = replenishmentService.getRequestById(requestId);
            if (request == null) {
                throw new IllegalArgumentException("Request not found");
            }
            if (request.getStatus() != ReplenishmentStatus.PENDING) {
                throw new IllegalArgumentException("Request is not in pending status");
            }

            System.out.print("Enter approval notes: ");
            String notes = scanner.nextLine().trim();

            replenishmentService.updateRequestStatus(requestId, ReplenishmentStatus.APPROVED, notes);
            inventoryManager.addMedication(
                    request.getMedicationName(),
                    request.getQuantity(),
                    LocalDate.now().plusYears(1).toString());

            System.out.println("Request approved and inventory updated successfully");
        } catch (Exception e) {
            System.err.println("Error approving request: " + e.getMessage());
        }
    }

    private void rejectReplenishmentRequest() {
        try {
            viewPendingRequests();
            System.out.print("\nEnter Request ID to reject: ");
            String requestId = scanner.nextLine().trim();

            ReplenishmentRequest request = replenishmentService.getRequestById(requestId);
            if (request == null) {
                throw new IllegalArgumentException("Request not found");
            }
            if (request.getStatus() != ReplenishmentStatus.PENDING) {
                throw new IllegalArgumentException("Request is not in pending status");
            }

            System.out.print("Enter rejection reason: ");
            String notes = scanner.nextLine().trim();

            replenishmentService.updateRequestStatus(requestId, ReplenishmentStatus.REJECTED, notes);
            System.out.println("Request rejected successfully");
        } catch (Exception e) {
            System.err.println("Error rejecting request: " + e.getMessage());
        }
    }

    // Appointment Management Implementation Methods
    private void viewAllAppointments() {
        List<Appointment> appointments = appointmentList.getAllAppointments();
        displayAppointments(appointments, "All");
    }

    private void viewCompletedAppointments() {
        List<Appointment> completedAppointments = appointmentList.getAppointmentsByStatus("Completed");
        displayAppointments(completedAppointments, "Completed");
    }

    private void displayAppointments(List<Appointment> appointments, String type) {
        if (appointments.isEmpty()) {
            System.out.println("No " + type.toLowerCase() + " appointments found.");
            return;
        }

        System.out.println("\n" + type + " Appointments:");
        System.out.println("----------------------------------------");
        for (Appointment appointment : appointments) {
            System.out.println(appointment);
            if ("Completed".equals(appointment.getStatus())) {
                appointment.displayOutcome();
            }
            System.out.println("----------------------------------------");
        }
    }
}