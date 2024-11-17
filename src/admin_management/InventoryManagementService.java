package admin_management;

import pharmacy_management.*;

import java.time.LocalDate;
import java.util.*;

public class InventoryManagementService implements IInventoryManagement {
  private final InventoryManager inventoryManager;
  private final IReplenishmentService replenishmentService;

  public InventoryManagementService(InventoryManager inventoryManager,
      IReplenishmentService replenishmentService) {
    this.inventoryManager = inventoryManager;
    this.replenishmentService = replenishmentService;
  }

  @Override
  public void updateStockLevel(String medicationName, int quantity) {
    // Use the existing inventory update mechanism
    inventoryManager.addMedication(medicationName, quantity,
        LocalDate.now().plusYears(1).toString()); // Default 1-year expiry
    System.out.println("Stock level updated successfully");
  }

  @Override
  public void setLowStockThreshold(String medicationName, int threshold) {
    inventoryManager.setLowStockThreshold(medicationName, threshold);
    System.out.println("Low stock threshold updated successfully");
  }

  @Override
  public void approveReplenishmentRequest(String requestId, String notes) {
    ReplenishmentRequest request = replenishmentService.getRequestById(requestId);
    if (request == null) {
      throw new IllegalArgumentException("Request not found");
    }
    replenishmentService.updateRequestStatus(requestId, ReplenishmentStatus.APPROVED, notes);
    System.out.println("Replenishment request approved successfully");
  }

  @Override
  public void rejectReplenishmentRequest(String requestId, String notes) {
    ReplenishmentRequest request = replenishmentService.getRequestById(requestId);
    if (request == null) {
      throw new IllegalArgumentException("Request not found");
    }
    replenishmentService.updateRequestStatus(requestId, ReplenishmentStatus.REJECTED, notes);
    System.out.println("Replenishment request rejected successfully");
  }

  @Override
  public List<ReplenishmentRequest> getPendingRequests() {
    return replenishmentService.getPendingRequests();
  }

  @Override
  public void displayInventoryStatus() {
    System.out.println("\nCurrent Inventory Status:");
    System.out.println("----------------------------------------");
    inventoryManager.checkInventory();
    System.out.println("\nLow Stock Alerts:");
    inventoryManager.checkAllLowStock();
  }

  @Override
  public void displayReplenishmentRequests(List<ReplenishmentRequest> requests) {
    if (requests.isEmpty()) {
      System.out.println("No replenishment requests found.");
      return;
    }

    System.out.println("\nReplenishment Requests:");
    System.out.println("----------------------------------------");
    for (ReplenishmentRequest request : requests) {
      System.out.println("Request ID: " + request.getRequestId());
      System.out.println("Medication: " + request.getMedicationName());
      System.out.println("Quantity: " + request.getQuantity());
      System.out.println("Status: " + request.getStatus());
      System.out.println("Pharmacist ID: " + request.getPharmacistId());
      System.out.println("Date: " + request.getRequestDate());
      if (request.getAdminNotes() != null && !request.getAdminNotes().isEmpty()) {
        System.out.println("Notes: " + request.getAdminNotes());
      }
      System.out.println("----------------------------------------");
    }
  }
}
