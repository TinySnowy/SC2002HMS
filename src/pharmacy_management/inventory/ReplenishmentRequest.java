package pharmacy_management.inventory;

import java.time.LocalDateTime;

/**
 * Model class representing a medication replenishment request in the HMS.
 * Contains:
 * - Request identification
 * - Medication details
 * - Quantity information
 * - Pharmacist tracking
 * - Status management
 * - Administrative notes
 * Provides structured storage for medication restocking requests.
 */
public class ReplenishmentRequest {
  /** Unique identifier for the request */
  private final String requestId;
  /** Name of medication to be replenished */
  private final String medicationName;
  /** Quantity of medication requested */
  private final int quantity;
  /** ID of pharmacist making request */
  private final String pharmacistId;
  /** Current status of the request */
  private ReplenishmentStatus status;
  /** Timestamp of request creation */
  private final LocalDateTime requestDate;
  /** Administrative notes for request processing */
  private String adminNotes;

  /**
   * Constructs a new replenishment request with required details.
   * Initializes request with PENDING status and current timestamp.
   * 
   * @param requestId Unique request identifier
   * @param medicationName Name of medication needed
   * @param quantity Amount requested
   * @param pharmacistId ID of requesting pharmacist
   * @throws IllegalArgumentException if required parameters are invalid
   */
  public ReplenishmentRequest(String requestId, String medicationName, int quantity, String pharmacistId) {
    this.requestId = requestId;
    this.medicationName = medicationName;
    this.quantity = quantity;
    this.pharmacistId = pharmacistId;
    this.status = ReplenishmentStatus.PENDING;
    this.requestDate = LocalDateTime.now();
  }

  /**
   * Retrieves request's unique identifier.
   * Used for request tracking and lookup.
   * 
   * @return Request ID string
   */
  public String getRequestId() {
    return requestId;
  }

  /**
   * Retrieves name of requested medication.
   * Used for inventory matching and fulfillment.
   * 
   * @return Medication name string
   */
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
