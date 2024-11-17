package pharmacy_management;

import java.time.LocalDateTime;

public class ReplenishmentRequest {
  private final String requestId;
  private final String medicationName;
  private final int quantity;
  private final String pharmacistId;
  private ReplenishmentStatus status;
  private final LocalDateTime requestDate;
  private String adminNotes;

  public ReplenishmentRequest(String requestId, String medicationName, int quantity, String pharmacistId) {
    this.requestId = requestId;
    this.medicationName = medicationName;
    this.quantity = quantity;
    this.pharmacistId = pharmacistId;
    this.status = ReplenishmentStatus.PENDING;
    this.requestDate = LocalDateTime.now();
  }

  public String getRequestId() {
    return requestId;
  }

  public String getMedicationName() {
    return medicationName;
  }

  public int getQuantity() {
    return quantity;
  }

  public String getPharmacistId() {
    return pharmacistId;
  }

  public ReplenishmentStatus getStatus() {
    return status;
  }

  public LocalDateTime getRequestDate() {
    return requestDate;
  }

  public String getAdminNotes() {
    return adminNotes;
  }

  public void updateStatus(ReplenishmentStatus status, String notes) {
    this.status = status;
    this.adminNotes = notes;
  }
}
