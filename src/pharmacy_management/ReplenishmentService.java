package pharmacy_management;

import java.util.*;
import java.time.LocalDate;
import utils.CSVReaderUtil;
import utils.CSVWriterUtil;

public class ReplenishmentService implements IReplenishmentService {
  private final Map<String, ReplenishmentRequest> requests = new HashMap<>();
  private final IInventoryService inventoryService;
  private static final String REQUESTS_FILE = "SC2002HMS/data/Replenishment_Requests.csv";

  public ReplenishmentService(IInventoryService inventoryService) {
    this.inventoryService = inventoryService;
    loadRequests();
  }

  @Override
  public String createRequest(String medicationName, int quantity, String pharmacistId) {
    String requestId = UUID.randomUUID().toString();
    ReplenishmentRequest request = new ReplenishmentRequest(requestId, medicationName, quantity, pharmacistId);
    requests.put(requestId, request);
    saveRequests();
    return requestId;
  }

  @Override
  public List<ReplenishmentRequest> getRequestsByPharmacist(String pharmacistId) {
    return requests.values().stream()
        .filter(r -> r.getPharmacistId().equals(pharmacistId))
        .collect(java.util.stream.Collectors.toList());
  }

  @Override
  public boolean updateRequestStatus(String requestId, ReplenishmentStatus status, String notes) {
    ReplenishmentRequest request = requests.get(requestId);
    if (request == null)
      return false;

    request.updateStatus(status, notes);
    if (status == ReplenishmentStatus.APPROVED) {
      inventoryService.updateStock(request.getMedicationName(), request.getQuantity());
    }

    saveRequests();
    return true;
  }

  @Override
  public ReplenishmentRequest getRequestById(String requestId) {
    return requests.get(requestId);
  }

  private void loadRequests() {
    List<String[]> csvData = CSVReaderUtil.readCSV(REQUESTS_FILE);
    for (String[] row : csvData) {
      if (row.length >= 6) { // Ensure correct number of fields
        String requestId = row[0];
        String medicationName = row[1];
        int quantity = Integer.parseInt(row[2]);
        String pharmacistId = row[3];
        ReplenishmentStatus status = ReplenishmentStatus.valueOf(row[4]);
        String notes = row[5]; // Optional notes field

        // Create and store the request
        ReplenishmentRequest request = new ReplenishmentRequest(requestId, medicationName, quantity, pharmacistId);
        request.updateStatus(status, notes); // Update status and notes
        requests.put(requestId, request);
      }
    }
  }

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
