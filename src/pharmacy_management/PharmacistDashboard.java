package pharmacy_management;

import user_management.Pharmacist;
import java.util.Scanner;

import pharmacy_management.appointments.AppointmentOutcome;
import pharmacy_management.appointments.AppointmentOutcomeService;
import pharmacy_management.inventory.IReplenishmentService;
import pharmacy_management.inventory.InventoryManager;
import pharmacy_management.inventory.ReplenishmentRequest;
import pharmacy_management.prescriptions.Prescription;

import java.util.List;

public class PharmacistDashboard {
    private final Pharmacist pharmacist;
    private final InventoryManager inventoryManager;
    private final AppointmentOutcomeService appointmentOutcomeService;
    private final IReplenishmentService replenishmentService;
    private final Scanner scanner;

    public PharmacistDashboard(Pharmacist pharmacist, InventoryManager inventoryManager,
            IReplenishmentService replenishmentService) {
        this.pharmacist = pharmacist;
        this.inventoryManager = inventoryManager;
        this.replenishmentService = replenishmentService;
        this.appointmentOutcomeService = new AppointmentOutcomeService(null);
        this.scanner = new Scanner(System.in);
    }

    public void showDashboard() {
        while (true) {
            displayMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewInventory();
                    break;
                case 2:
                    fulfillPrescription();
                    break;
                case 3:
                    requestRefill();
                    break;
                case 4:
                    viewAppointmentOutcomes();
                    break;
                case 5:
                    updatePrescriptionStatus();
                    break;
                case 6:
                    viewReplenishmentRequests();
                    break;
                case 7: {
                    System.out.println("Logging out of Pharmacist Dashboard...");
                    return;
                }
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\nPharmacist Dashboard - " + pharmacist.getName());
        System.out.println("----------------------------------------");
        System.out.println("1. View Medication Inventory");
        System.out.println("2. Fulfill Prescription");
        System.out.println("3. Submit Replenishment Request");
        System.out.println("4. View Appointment Outcome Record");
        System.out.println("5. Update Prescription Status");
        System.out.println("6. View Replenishment Requests");
        System.out.println("7. Log Out");
        System.out.println("----------------------------------------");
        System.out.print("Enter your choice: ");
    }

    private void viewInventory() {
        System.out.println("\nCurrent Inventory:");
        System.out.println("----------------------------------------");
        inventoryManager.checkInventory();
        inventoryManager.checkAllLowStock();
    }

    private void fulfillPrescription() {
        System.out.print("Enter Prescription ID to fulfill: ");
        String prescriptionId = scanner.nextLine();
        Prescription prescription = inventoryManager.getPrescriptionById(prescriptionId);

        if (prescription != null && "Pending".equals(prescription.getStatus())) {
            if (prescription.fulfill(inventoryManager)) {
                System.out.println("Prescription fulfilled successfully:");
                System.out.println(prescription);
            } else {
                System.out.println("Unable to fulfill prescription due to insufficient stock.");
                checkAndSuggestReplenishment(prescription.getMedicationName());
            }
        } else {
            System.out.println("Prescription not found or already fulfilled.");
        }
    }

    private void requestRefill() {
        System.out.print("Enter Medication Name: ");
        String medicationName = scanner.nextLine();

        System.out.print("Enter Quantity: ");
        int quantity = getValidatedInput("Enter quantity (minimum 1): ", 1);

        try {
            String requestId = replenishmentService.createRequest(medicationName, quantity, pharmacist.getId());
            System.out.println("\nReplenishment request submitted successfully!");
            System.out.println("Request ID: " + requestId);
            System.out.println("Status: PENDING");
            System.out.println("Medication: " + medicationName);
            System.out.println("Quantity: " + quantity);
            System.out.println("\nNote: Request will be reviewed by an administrator.");
        } catch (Exception e) {
            System.out.println("Error submitting replenishment request: " + e.getMessage());
        }
    }

    private void viewReplenishmentRequests() {
        List<ReplenishmentRequest> requests = replenishmentService.getRequestsByPharmacist(pharmacist.getId());

        if (requests.isEmpty()) {
            System.out.println("\nNo replenishment requests found.");
            return;
        }

        System.out.println("\nYour Replenishment Requests:");
        System.out.println("----------------------------------------");
        for (ReplenishmentRequest request : requests) {
            System.out.println("Request ID: " + request.getRequestId());
            System.out.println("Medication: " + request.getMedicationName());
            System.out.println("Quantity: " + request.getQuantity());
            System.out.println("Status: " + request.getStatus());
            System.out.println("Date: " + request.getRequestDate());
            if (request.getAdminNotes() != null && !request.getAdminNotes().isEmpty()) {
                System.out.println("Admin Notes: " + request.getAdminNotes());
            }
            System.out.println("----------------------------------------");
        }
    }

    private void viewAppointmentOutcomes() {
        List<AppointmentOutcome> pendingOutcomes = appointmentOutcomeService.getPendingPrescriptionOutcomes();
        if (pendingOutcomes.isEmpty()) {
            System.out.println("No pending prescription orders found.");
            return;
        }

        System.out.println("\nPending Prescription Orders:");
        System.out.println("----------------------------------------");
        for (AppointmentOutcome outcome : pendingOutcomes) {
            System.out.println("Appointment ID: " + outcome.getAppointmentId());
            System.out.println("Patient ID: " + outcome.getPatientId());
            System.out.println("Doctor ID: " + outcome.getDoctorId());
            System.out.println("Date: " + outcome.getAppointmentDate());
            System.out.println("Prescriptions:");

            for (Prescription prescription : outcome.getPrescriptions()) {
                if ("Pending".equals(prescription.getStatus())) {
                    System.out.println("- " + prescription);
                }
            }
            System.out.println("----------------------------------------");
        }
    }

    private void updatePrescriptionStatus() {
        System.out.print("Enter Prescription ID: ");
        String prescriptionId = scanner.nextLine();

        Prescription prescription = inventoryManager.getPrescriptionById(prescriptionId);
        if (prescription == null) {
            System.out.println("Prescription not found.");
            return;
        }

        System.out.println("\nCurrent Status: " + prescription.getStatus());
        System.out.println("1. Mark as Dispensed");
        System.out.println("2. Mark as Cancelled");
        System.out.print("Enter choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                if (prescription.fulfill(inventoryManager)) {
                    System.out.println("Prescription marked as dispensed successfully.");
                }
                break;
            case 2:
                prescription.cancel();
                System.out.println("Prescription cancelled successfully.");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void checkAndSuggestReplenishment(String medicationName) {
        System.out.println("\nWould you like to submit a replenishment request for " + medicationName + "? (yes/no)");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes")) {
            requestRefill();
        }
    }

    private int getValidatedInput(String prompt, int minimum) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = scanner.nextInt();
                scanner.nextLine();

                if (value >= minimum) {
                    return value;
                }
                System.out.println("Please enter a number greater than or equal to " + minimum);
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine();
            }
        }
    }
}