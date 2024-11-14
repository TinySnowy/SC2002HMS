package admin_management;

import user_management.*;
import pharmacy_management.InventoryManager;
import appointment_management.AppointmentList;
import java.util.Scanner;

public class AdminDashboard {
    private StaffManager staffManager;
    private InventoryControl inventoryControl;
    private AppointmentViewer appointmentViewer;
    private Scanner scanner;

    public AdminDashboard(InventoryManager inventoryManager, AppointmentList appointmentList) {
        this.staffManager = new StaffManager();
        this.inventoryControl = new InventoryControl(inventoryManager);
        this.appointmentViewer = new AppointmentViewer(appointmentList);
        this.scanner = new Scanner(System.in);
    }

    public void showMenu() {
        while (true) {
            System.out.println("\nAdmin Dashboard");
            System.out.println("1. View All Staff");
            System.out.println("2. Add Staff");
            System.out.println("3. Remove Staff");
            System.out.println("4. View Inventory");
            System.out.println("5. Approve Refill Request");
            System.out.println("6. View All Appointments");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> staffManager.listAllStaff();
                case 2 -> addStaff();
                case 3 -> removeStaff();
                case 4 -> inventoryControl.viewInventory();
                case 5 -> approveRefill();
                case 6 -> appointmentViewer.viewAllAppointments();
                case 7 -> {
                    System.out.println("Exiting Admin Dashboard.");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void addStaff() {
        System.out.print("Enter User ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Role (Patient, Doctor, Pharmacist, Administrator): ");
        String role = scanner.nextLine();

        User newUser = switch (role) {
            case "Patient" -> new Patient(id, "password", name, "contact@example.com","john.doe@example.com");
            case "Doctor" -> new Doctor(id, "password", "General Medicine", name); // Updated with name parameter
            case "Pharmacist" -> new Pharmacist(id, "password", "License123", name);
            case "Administrator" -> new Administrator(id, "password", name);
            default -> {
                System.out.println("Invalid role. Staff not added.");
                yield null;
            }
        };

        if (newUser != null) {
            staffManager.addStaff(newUser);
        }
    }

    private void removeStaff() {
        System.out.print("Enter User ID to remove: ");
        String id = scanner.nextLine();
        staffManager.removeStaff(id);
    }

    private void approveRefill() {
        System.out.print("Enter Medication Name: ");
        String medName = scanner.nextLine();
        System.out.print("Enter Quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // consume newline
        inventoryControl.approveRefill(medName, quantity);
    }
}
