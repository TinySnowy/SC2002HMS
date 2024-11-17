package pharmacy_management.inventory;

import java.util.List;

public interface IReplenishmentService {
  String createRequest(String medicationName, int quantity, String pharmacistId);

  List<ReplenishmentRequest> getPendingRequests();

  List<ReplenishmentRequest> getRequestsByPharmacist(String pharmacistId);

  boolean updateRequestStatus(String requestId, ReplenishmentStatus status, String notes);

  ReplenishmentRequest getRequestById(String requestId);
}