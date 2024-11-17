package admin_management.handlers.inventory;
import admin_management.utils.InputValidator;

import pharmacy_management.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;


public class ReplenishmentRequestHandler {
    private final InventoryManager inventoryManager;
    private final IReplenishmentService replenishmentService;
    private final Scanner scanner;
    public ReplenishmentRequestHandler(InventoryManager inventoryManager, 
            IReplenishmentService replenishmentService) {
        this.inventoryManager = inventoryManager;
        this.replenishmentService = replenishmentService;
        this.scanner = new Scanner(System.in);
        new InputValidator();
    }

    public void viewPendingRequests() {
        List<ReplenishmentRequest> pendingRequests = replenishmentService.getPendingRequests();
        if (pendingRequests.isEmpty()) {
            System.out.println("No pending replenishment requests found.");
            return;
        }

        System.out.println("\nPending Replenishment Requests:");
        System.out.println("----------------------------------------");
        for (ReplenishmentRequest request : pendingRequests) {
            displayRequest(request);
        }
    }

    public void approveRequest() {
        try {
            viewPendingRequests();
            System.out.print("\nEnter Request ID to approve: ");
            String requestId = scanner.nextLine().trim();

            ReplenishmentRequest request = validateRequest(requestId);
            System.out.print("Enter approval notes: ");
            String notes = scanner.nextLine().trim();

            processApproval(request, notes);
            System.out.println("Request approved and inventory updated successfully");
        } catch (Exception e) {
            throw new IllegalStateException("Error approving request: " + e.getMessage());
        }
    }

    public void rejectRequest() {
        try {
            viewPendingRequests();
            System.out.print("\nEnter Request ID to reject: ");
            String requestId = scanner.nextLine().trim();

            ReplenishmentRequest request = validateRequest(requestId);
            System.out.print("Enter rejection reason: ");
            String notes = scanner.nextLine().trim();

            replenishmentService.updateRequestStatus(request.getRequestId(), ReplenishmentStatus.REJECTED, notes);
            System.out.println("Request rejected successfully");
        } catch (Exception e) {
            throw new IllegalStateException("Error rejecting request: " + e.getMessage());
        }
    }

    private void displayRequest(ReplenishmentRequest request) {
        System.out.println("Request ID: " + request.getRequestId());
        System.out.println("Medication: " + request.getMedicationName());
        System.out.println("Quantity: " + request.getQuantity());
        System.out.println("Pharmacist ID: " + request.getPharmacistId());
        System.out.println("Date: " + request.getRequestDate());
        System.out.println("----------------------------------------");
    }

    private ReplenishmentRequest validateRequest(String requestId) {
        ReplenishmentRequest request = replenishmentService.getRequestById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Request not found");
        }
        if (request.getStatus() != ReplenishmentStatus.PENDING) {
            throw new IllegalArgumentException("Request is not in pending status");
        }
        return request;
    }

    private void processApproval(ReplenishmentRequest request, String notes) {
        replenishmentService.updateRequestStatus(request.getRequestId(), 
                ReplenishmentStatus.APPROVED, notes);
        inventoryManager.addMedication(
                request.getMedicationName(),
                request.getQuantity(),
                LocalDate.now().plusYears(1).toString());
    }
}