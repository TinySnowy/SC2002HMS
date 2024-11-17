package admin_management;

import pharmacy_management.ReplenishmentRequest;
import java.util.List;

public interface IInventoryManagement {
  void updateStockLevel(String medicationName, int quantity);

  void setLowStockThreshold(String medicationName, int threshold);

  void approveReplenishmentRequest(String requestId, String notes);

  void rejectReplenishmentRequest(String requestId, String notes);

  List<ReplenishmentRequest> getPendingRequests();

  void displayInventoryStatus();

  void displayReplenishmentRequests(List<ReplenishmentRequest> requests);
}
