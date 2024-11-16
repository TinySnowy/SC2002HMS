package admin_management;

import user_management.*;
import pharmacy_management.InventoryManager;
import appointment_management.AppointmentList;
import appointment_management.Appointment;

import java.util.Scanner;
import java.util.List;

public class AdminDashboard {
    private final StaffManager staffManager;
    private final InventoryControl inventoryControl;
    private final AppointmentViewer appointmentViewer;
    private final InventoryManager inventoryManager;
    private final AppointmentList appointmentList;
    private final UserController userController;
    private final Scanner scanner;

    public AdminDashboard(UserController userController, InventoryManager inventoryManager,
            AppointmentList appointmentList) {
        this.userController = userController;
        this.staffManager = new StaffManager(userController); // Pass UserController to StaffManager
        this.inventoryControl = new InventoryControl(inventoryManager);
        this.appointmentViewer = new AppointmentViewer(appointmentList);
        this.inventoryManager = inventoryManager;
        this.appointmentList = appointmentList;
        this.scanner = new Scanner(System.in);
    }

    public void showMenu() {
        boolean running = true;
        while (running) {
            try {
                displayMenuOptions();
                int choice = getValidatedInput("Enter choice: ", 1, 9);
                running = handleMenuChoice(choice);
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                scanner.nextLine(); // Clear the scanner buffer
            }
        }
    }

    private void displayMenuOptions() {
        System.out.println("\nAdmin Dashboard");
        System.out.println("----------------------------------------");
        System.out.println("1. View All Staff");
        System.out.println("2. Add Staff");
        System.out.println("3. Remove Staff");
        System.out.println("4. View Inventory");
        System.out.println("5. Approve Refill Request");
        System.out.println("6. View Low Stock Alerts");
        System.out.println("7. Filter Staff");
        System.out.println("8. View Completed Appointment Outcomes");
        System.out.println("9. Exit");
        System.out.println("----------------------------------------");
    }

    private boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                staffManager.listAllStaff();
                break;
            case 2:
                addStaff();
                break;
            case 3:
                removeStaff();
                break;
            case 4:
                inventoryControl.viewInventory();
                break;
            case 5:
                approveRefill();
                break;
            case 6:
                viewLowStockAlerts();
                break;
            case 7:
                filterStaff();
                break;
            case 8:
                viewCompletedAppointments();
                break;
            case 9:
                System.out.println("Exiting Admin Dashboard.");
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        return true;
    }

    private int getValidatedInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int input = scanner.nextInt();
                scanner.nextLine(); // consume newline
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf("Please enter a number between %d and %d%n", min, max);
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
    }

    private void addStaff() {
        try {
            System.out.print("Enter User ID: ");
            String id = scanner.nextLine();

            if (userController.getUserById(id) != null) {
                System.out.println("Error: User ID already exists.");
                return;
            }

            System.out.print("Enter Name: ");
            String name = scanner.nextLine();

            System.out.println("Select Role:");
            System.out.println("1. Doctor");
            System.out.println("2. Pharmacist");
            System.out.println("3. Administrator");
            int roleChoice = getValidatedInput("Enter choice (1-3): ", 1, 3);

            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            User newUser = createStaffMember(id, name, password, roleChoice);
            if (newUser != null) {
                staffManager.addStaff(newUser);
                System.out.println("Staff added successfully.");
            }
        } catch (Exception e) {
            System.err.println("Error adding staff: " + e.getMessage());
        }
    }

    private User createStaffMember(String id, String name, String password, int roleChoice) {
        switch (roleChoice) {
            case 1:
                System.out.print("Enter Specialty: ");
                String specialty = scanner.nextLine();
                return new Doctor(id, name, password, specialty, true);
            case 2:
                System.out.print("Enter License Number: ");
                String license = scanner.nextLine();
                return new Pharmacist(id, name, password, license, true);
            case 3:
                return new Administrator(id, name, password, true);
            default:
                System.out.println("Invalid role selection.");
                return null;
        }
    }

    private void removeStaff() {
        System.out.print("Enter User ID to remove: ");
        String id = scanner.nextLine();

        User user = userController.getUserById(id);
        if (user == null) {
            System.out.println("Error: User not found.");
            return;
        }

        if (user instanceof Patient) {
            System.out.println("Error: Cannot remove patients through staff management.");
            return;
        }

        staffManager.removeStaff(id);
    }

    private void approveRefill() {
        try {
            System.out.print("Enter Medication Name: ");
            String medName = scanner.nextLine();
            int quantity = getValidatedInput("Enter Quantity: ", 1, Integer.MAX_VALUE);
            inventoryControl.approveRefill(medName, quantity);
        } catch (Exception e) {
            System.err.println("Error approving refill: " + e.getMessage());
        }
    }

    private void viewLowStockAlerts() {
        System.out.println("\nLow Stock Alerts:");
        System.out.println("----------------------------------------");
        inventoryManager.checkAllLowStock();
    }

    private void filterStaff() {
        System.out.println("\nFilter Staff by:");
        System.out.println("1. Role");
        System.out.println("2. Back to Main Menu");

        int choice = getValidatedInput("Enter choice: ", 1, 2);
        if (choice == 1) {
            staffManager.filterStaff("role");
        }
    }

    private void viewCompletedAppointments() {
        List<Appointment> completedAppointments = appointmentList.getAppointmentsByStatus("Completed");

        if (completedAppointments.isEmpty()) {
            System.out.println("No completed appointments found.");
            return;
        }

        System.out.println("\nCompleted Appointment Outcomes:");
        System.out.println("----------------------------------------");
        for (Appointment appointment : completedAppointments) {
            System.out.println(appointment);
            appointment.displayOutcome();
            System.out.println("----------------------------------------");
        }
    }
}