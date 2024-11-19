package pharmacy_management.inventory;

import java.util.List;

/**
 * Interface defining the contract for medication replenishment operations.
 * Specifies essential operations for:
 * - Stock replenishment requests
 * - Request tracking
 * - Status management
 * - Request history
 * Provides standardized methods for managing medication restocking process.
 */
public interface IReplenishmentService {
  /**
   * Creates a new replenishment request.
   * Implementation should:
   * - Validate medication existence
   * - Check quantity validity
   * - Generate request ID
   * - Record pharmacist details
   * - Initialize request status
   * - Persist request data
   * 
   * @param medicationName Name of medication to restock
   * @param quantity Amount requested
   * @param pharmacistId ID of requesting pharmacist
   * @return Generated request ID or error message
   */
  String createRequest(String medicationName, int quantity, String pharmacistId);

  /**
   * Retrieves all pending replenishment requests.
   * Implementation should:
   * - Filter for pending status
   * - Sort by priority/date
   * - Include request details
   * - Handle empty results
   * Used for processing outstanding requests.
   * 
   * @return List of pending replenishment requests
   */
  List<ReplenishmentRequest> getPendingRequests();

  /**
   * Retrieves requests made by specific pharmacist.
   * Implementation should:
   * - Validate pharmacist ID
   * - Filter by requester
   * - Sort by date
   * - Include all statuses
   * - Handle access control
   * 
   * @param pharmacistId ID of requesting pharmacist
   * @return List of pharmacist's replenishment requests
   */
  List<ReplenishmentRequest> getRequestsByPharmacist(String pharmacistId);

  /**
   * Updates status of existing replenishment request.
   * Implementation should:
   * - Validate request existence
   * - Check status validity
   * - Update request record
   * - Handle inventory updates
   * - Record status notes
   * - Persist changes
   * - Notify relevant parties
   * 
   * @param requestId ID of request to update
   * @param status New status value
   * @param notes Additional status notes
   * @return true if update successful, false otherwise
   */
  boolean updateRequestStatus(String requestId, ReplenishmentStatus status, String notes);

  /**
   * Retrieves specific replenishment request.
   * Implementation should:
   * - Validate request ID
   * - Return complete request data
   * - Handle missing requests
   * - Check access rights
   * 
   * @param requestId ID of request to retrieve
   * @return ReplenishmentRequest if found, null otherwise
   */
  ReplenishmentRequest getRequestById(String requestId);
}