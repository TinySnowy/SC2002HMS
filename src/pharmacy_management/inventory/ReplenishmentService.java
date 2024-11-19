package pharmacy_management.inventory;

//import java.time.LocalDate;
import java.util.*;

import utils.CSVReaderUtil;
import utils.CSVWriterUtil;

/**
 * Service class implementing medication replenishment management.
 * Handles:
 * - Request creation and tracking
 * - Status management
 * - Inventory updates
 * - CSV data persistence
 * - Request history maintenance
 * Provides complete management of medication restocking workflow.
 */
public class ReplenishmentService implements IReplenishmentService {
  /** Maps request IDs to their replenishment requests */
  private final Map<String, ReplenishmentRequest> requests = new HashMap<>();
  /** Service for inventory management operations */
  private final IInventoryService inventoryService;
  /** File path for request data persistence */
  private static final String REQUESTS_FILE = "SC2002HMS/data/Replenishment_Requests.csv";

  /**
   * Initializes replenishment service with inventory dependency.
   * Sets up request tracking and loads existing requests.
   * 
   * @param inventoryService Service for inventory operations
   */
  public ReplenishmentService(IInventoryService inventoryService) {
    this.inventoryService = inventoryService;
    loadRequests();
  }

  /**
   * Loads replenishment requests from CSV storage.
   * Processes:
   * - Request details
   * - Status information
   * - Administrative notes
   * Handles file reading and parsing errors.
   */
  private void loadRequests() {
    List<String[]> csvData = CSVReaderUtil.readCSV(REQUESTS_FILE);
    boolean isFirstRow = true;

    for (String[] row : csvData) {
      if (isFirstRow) {
        isFirstRow = false;
        continue;
      }

      try {
        if (row.length >= 6) {
          String requestId = row[0];
          String medicationName = row[1];
          int quantity = Integer.parseInt(row[2]);
          String pharmacistId = row[3];
          ReplenishmentStatus status = ReplenishmentStatus.valueOf(row[4]);
          String notes = row[5];

          ReplenishmentRequest request = new ReplenishmentRequest(requestId, medicationName, quantity, pharmacistId);
          request.updateStatus(status, notes);
          requests.put(requestId, request);
        }
      } catch (Exception e) {
        System.err.println("Error loading request record: " + e.getMessage());
      }
    }
  }

  /**
   * Creates new replenishment request.
   * Generates unique ID and initializes request tracking.
   * 
   * @param medicationName Target medication
   * @param quantity Amount requested
   * @param pharmacistId Requesting pharmacist
   * @return Generated request ID
   */
  @Override
  public String createRequest(String medicationName, int quantity, String pharmacistId) {
    String requestId = UUID.randomUUID().toString();
    ReplenishmentRequest request = new ReplenishmentRequest(requestId, medicationName, quantity, pharmacistId);
    requests.put(requestId, request);
    saveRequests();
    return requestId;
  }

  /**
   * Retrieves all pending replenishment requests.
   * Filters for PENDING status and sorts by date.
   * 
   * @return List of pending requests
   */
  public List<ReplenishmentRequest> getPendingRequests() {
    return requests.values().stream()
        .filter(r -> r.getStatus() == ReplenishmentStatus.PENDING)
        .collect(java.util.stream.Collectors.toList());
  }

  /**
   * Retrieves requests by pharmacist ID.
   * Returns all requests regardless of status.
   * 
   * @param pharmacistId Target pharmacist
   * @return List of pharmacist's requests
   */
  @Override
  public List<ReplenishmentRequest> getRequestsByPharmacist(String pharmacistId) {
    return requests.values().stream()
        .filter(r -> r.getPharmacistId().equals(pharmacistId))
        .collect(java.util.stream.Collectors.toList());
  }

  /**
   * Updates request status and processes inventory changes.
   * Handles approved requests by updating inventory.
   * 
   * @param requestId Target request
   * @param status New status
   * @param notes Administrative notes
   * @return true if update successful
   */
  @Override
  public boolean updateRequestStatus(String requestId, ReplenishmentStatus status, String notes) {
    ReplenishmentRequest request = requests.get(requestId);
    if (request == null) {
      return false;
    }

    request.updateStatus(status, notes);

    if (status == ReplenishmentStatus.APPROVED) {
      if (inventoryService.updateStock(request.getMedicationName(), request.getQuantity())) {
        saveRequests();
        return true;
      }
      return false;
    }

    saveRequests();
    return true;
  }

  /**
   * Retrieves specific request by ID.
   * 
   * @param requestId Target request ID
   * @return Request if found, null otherwise
   */
  @Override
  public ReplenishmentRequest getRequestById(String requestId) {
    return requests.get(requestId);
  }

  /**
   * Saves all requests to CSV storage.
   * Handles:
   * - Data formatting
   * - File writing
   * - Error recovery
   */
  private void saveRequests() {
    CSVWriterUtil.writeCSV(REQUESTS_FILE, writer -> {
      writer.write("Request ID,Medication Name,Quantity,Pharmacist ID,Status,Request Date,Notes\n");
      for (ReplenishmentRequest request : requests.values()) {
        writer.write(String.format("%s,%s,%d,%s,%s,%s,%s\n",
            request.getRequestId(),
            request.getMedicationName(),
            request.getQuantity(),
            request.getPharmacistId(),
            request.getStatus(),
            request.getRequestDate(),
            request.getAdminNotes() != null ? request.getAdminNotes() : ""));
      }
    });
  }
}