package pharmacy_management;

import user_management.Pharmacist;
import user_management.UserController;

import java.util.Scanner;

import pharmacy_management.appointments.AppointmentOutcome;
import pharmacy_management.appointments.AppointmentOutcomeService;
import pharmacy_management.inventory.IReplenishmentService;
import pharmacy_management.inventory.InventoryManager;
import pharmacy_management.inventory.ReplenishmentRequest;
import pharmacy_management.prescriptions.Prescription;
//import pharmacy_management.PrescriptionService;

import java.util.List;

public class PharmacistDashboard {
    private final Pharmacist pharmacist;
    private final InventoryManager inventoryManager;
    private final PrescriptionService prescriptionService;
    private final AppointmentOutcomeService appointmentOutcomeService;
    private final IReplenishmentService replenishmentService;
    private final Scanner scanner;

    public PharmacistDashboard(Pharmacist pharmacist, InventoryManager inventoryManager,
            IReplenishmentService replenishmentService, UserController userController) {
        this.pharmacist = pharmacist;
        this.inventoryManager = inventoryManager;
        this.replenishmentService = replenishmentService;
        this.prescriptionService = new PrescriptionService(inventoryManager, userController);
        this.appointmentOutcomeService = new AppointmentOutcomeService(userController);
        this.scanner = new Scanner(System.in);
    }

    public void showDashboard() {
        while (true) {
            displayMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewInventory();
                    break;
                case 2:
                    viewAndFulfillPrescriptions();
                    break;
                case 3:
                    requestRefill();
                    break;
                case 4:
                    viewAppointmentOutcomes();
                    break;
                case 5:
                    viewReplenishmentRequests();
                    break;
                case 6: {
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
        System.out.println("2. View & Fulfill Prescriptions");
        System.out.println("3. Submit Replenishment Request");
        System.out.println("4. View Appointment Outcomes");
        System.out.println("5. View Replenishment Requests");
        System.out.println("6. Log Out");
        System.out.println("----------------------------------------");
        System.out.print("Enter your choice: ");
    }

    private void viewInventory() {
        System.out.println("\nCurrent Inventory:");
        System.out.println("----------------------------------------");
        inventoryManager.checkInventory();
        inventoryManager.checkAllLowStock();

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewAndFulfillPrescriptions() {
        while (true) {
            System.out.println("\nPrescription Management");
            System.out.println("----------------------------------------");
            System.out.println("1. View Pending Prescriptions");
            System.out.println("2. Dispense Prescription");
            System.out.println("3. Return to Main Menu");
            System.out.print("\nEnter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    prescriptionService.displayPendingPrescriptions();
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                    break;
                case 2:
                    dispensePrescription();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void dispensePrescription() {
        prescriptionService.displayPendingPrescriptions();

        System.out.print("\nEnter Prescription ID to dispense (or 'cancel' to go back): ");
        String prescriptionId = scanner.nextLine().trim();

        if (prescriptionId.equalsIgnoreCase("cancel")) {
            return;
        }

        if (prescriptionService.dispensePrescription(prescriptionId)) {
            System.out.println("Prescription successfully dispensed!");
            System.out.println("Inventory has been automatically updated.");
        } else {
            System.out.println("Failed to dispense prescription.");
            System.out.println("Would you like to submit a replenishment request? (yes/no)");
            String response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("yes")) {
                requestRefill();
            }
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void requestRefill() {
        System.out.println("\nSubmit Replenishment Request");
        System.out.println("----------------------------------------");

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

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewReplenishmentRequests() {
        List<ReplenishmentRequest> requests = replenishmentService.getRequestsByPharmacist(pharmacist.getId());

        if (requests.isEmpty()) {
            System.out.println("\nNo replenishment requests found.");
        } else {
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

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewAppointmentOutcomes() {
        List<AppointmentOutcome> pendingOutcomes = appointmentOutcomeService.getPendingPrescriptionOutcomes();

        if (pendingOutcomes.isEmpty()) {
            System.out.println("No pending prescription orders found.");
        } else {
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

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private int getValidatedInput(String prompt, int minimum) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (value >= minimum) {
                    return value;
                }
                System.out.println("Please enter a number greater than or equal to " + minimum);
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Consume invalid input
            }
        }
    }
}