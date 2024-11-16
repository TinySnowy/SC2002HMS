package pharmacy_management;

import user_management.Pharmacist;
import java.util.Scanner;

public class PharmacistDashboard {
    private Pharmacist pharmacist;
    private InventoryManager inventoryManager;
    private Scanner scanner;

    public PharmacistDashboard(Pharmacist pharmacist, InventoryManager inventoryManager) {
        this.pharmacist = pharmacist;
        this.inventoryManager = inventoryManager;
        this.scanner = new Scanner(System.in);
    }

    public void showDashboard() {
        while (true) {
            System.out.println("\nPharmacist Dashboard - " + pharmacist.getName());
            System.out.println("1. View Inventory");
            System.out.println("2. Fulfill Prescription");
            System.out.println("3. Request Refill");
            System.out.println("4. Log Out");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewInventory();
                case 2:
                    fulfillPrescription();
                case 3:
                    requestRefill();
                case 4: {
                    System.out.println("Logging out of Pharmacist Dashboard...");
                    return;
                }
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void viewInventory() {
        System.out.println("\nCurrent Inventory:");
        inventoryManager.checkInventory();
    }

    private void fulfillPrescription() {
        System.out.print("Enter Prescription ID to fulfill: ");
        String prescriptionId = scanner.nextLine();
        Prescription prescription = inventoryManager.getPrescriptionById(prescriptionId);

        if (prescription != null && "Pending".equals(prescription.getStatus())) {
            if (prescription.fulfill(inventoryManager)) {
                System.out.println("Prescription fulfilled: " + prescription);
            } else {
                System.out.println("Unable to fulfill prescription due to insufficient stock.");
            }
        } else {
            System.out.println("Prescription not found or already fulfilled.");
        }
    }

    private void requestRefill() {
        System.out.print("Enter Medication Name: ");
        String medicationName = scanner.nextLine();
        System.out.print("Enter Quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter Expiry Date (YYYY-MM-DD): ");
        String expiryDate = scanner.nextLine();

        inventoryManager.requestRefill(medicationName, quantity, expiryDate);
        System.out.println("Refill request submitted for " + quantity + " units of " + medicationName + " (Expiry: "
                + expiryDate + ").");
    }
}
