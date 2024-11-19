package pharmacy_management;

import pharmacy_management.inventory.InventoryManager;
import pharmacy_management.prescriptions.Prescription;
import user_management.UserController;
import utils.CSVReaderUtil;
import utils.CSVWriterUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class managing prescription operations in the HMS.
 * Handles:
 * - Prescription tracking
 * - Dispensing management
 * - CSV data persistence
 * - Inventory integration
 * - Status updates
 * Provides centralized control of prescription lifecycle.
 */
public class PrescriptionService {
  /** File path for prescription data storage */
  private static final String PRESCRIPTIONS_FILE = "SC2002HMS/data/Prescriptions.csv";
  /** Manager for medication inventory */
  private final InventoryManager inventoryManager;
  /** Controller for user management */
  private final UserController userController;

  /**
   * Initializes prescription service with required dependencies.
   * Sets up inventory and user management integration.
   * 
   * @param inventoryManager Inventory control system
   * @param userController User management system
   */
  public PrescriptionService(InventoryManager inventoryManager, UserController userController) {
    this.inventoryManager = inventoryManager;
    this.userController = userController;
  }

  /**
   * Retrieves all pending prescriptions.
   * Filters for:
   * - Pending status
   * - Valid prescriptions
   * - Available medications
   * Used for pharmacy dispensing workflow.
   * 
   * @return List of pending prescriptions
   */
  public List<Prescription> getPendingPrescriptions() {
    List<Prescription> pendingPrescriptions = new ArrayList<>();
    try {
      List<String[]> records = CSVReaderUtil.readCSV(PRESCRIPTIONS_FILE);
      boolean isFirstRow = true;
      for (String[] record : records) {
        if (isFirstRow) {
          isFirstRow = false;
          continue;
        }
        String status = record[4].trim();
        if ("Pending".equalsIgnoreCase(status)) {
          Prescription prescription = new Prescription(
              record[0].trim(),
              null,
              record[1].trim(),
              record[2].trim(),
              Integer.parseInt(record[3].trim()));
          pendingPrescriptions.add(prescription);
        }
      }
    } catch (Exception e) {
      System.err.println("Error reading prescriptions: " + e.getMessage());
    }
    return pendingPrescriptions;
  }

  /**
   * Processes prescription dispensing request.
   * Handles:
   * - Prescription validation
   * - Stock verification
   * - Inventory updates
   * - Status changes
   * - Data persistence
   * 
   * @param prescriptionId Target prescription
   * @return true if dispensed successfully
   */
  public boolean dispensePrescription(String prescriptionId) {
    try {
      List<String[]> records = CSVReaderUtil.readCSV(PRESCRIPTIONS_FILE);
      List<String[]> updatedRecords = new ArrayList<>();
      boolean prescriptionFound = false;
      boolean dispensed = false;
      updatedRecords.add(records.get(0));
      for (int i = 1; i < records.size(); i++) {
        String[] record = records.get(i);
        if (record[0].trim().equals(prescriptionId)) {
          prescriptionFound = true;
          if ("Pending".equalsIgnoreCase(record[4].trim())) {
            String medicationName = record[1].trim();
            int quantity = Integer.parseInt(record[3].trim());
            if (inventoryManager.dispenseMedication(medicationName, quantity)) {
              record[4] = "Fulfilled";
              dispensed = true;
              System.out.println("Successfully dispensed prescription: " + prescriptionId);
            } else {
              System.out.println("Failed to dispense: Insufficient stock");
            }
          } else {
            System.out.println("Prescription is not in Pending status");
          }
        }
        updatedRecords.add(record);
      }
      if (!prescriptionFound) {
        System.out.println("Prescription not found: " + prescriptionId);
        return false;
      }
      if (dispensed) {
        saveUpdatedPrescriptions(updatedRecords);
        return true;
      }
      return false;
    } catch (Exception e) {
      System.err.println("Error dispensing prescription: " + e.getMessage());
      return false;
    }
  }

  /**
   * Saves updated prescription records to CSV.
   * Handles:
   * - Data formatting
   * - File writing
   * - Error recovery
   * 
   * @param records Updated prescription records
   */
  private void saveUpdatedPrescriptions(List<String[]> records) {
    try {
      CSVWriterUtil.writeCSV(PRESCRIPTIONS_FILE, writer -> {
        for (String[] record : records) {
          writer.write(String.join(",", record) + "\n");
        }
      });
    } catch (Exception e) {
      System.err.println("Error saving prescriptions: " + e.getMessage());
    }
  }

  /**
   * Displays all pending prescriptions.
   * Shows:
   * - Prescription details
   * - Medication info
   * - Dosage instructions
   * - Quantity required
   * Used for pharmacy workflow management.
   */
  public void displayPendingPrescriptions() {
    List<Prescription> pendingPrescriptions = getPendingPrescriptions();
    if (pendingPrescriptions.isEmpty()) {
      System.out.println("No pending prescriptions found.");
      return;
    }
    System.out.println("\nPending Prescriptions:");
    System.out.println("----------------------------------------");
    System.out.printf("%-15s %-15s %-15s %-10s%n",
        "Prescription ID", "Medication", "Dosage", "Quantity");
    System.out.println("----------------------------------------");
    for (Prescription prescription : pendingPrescriptions) {
      System.out.printf("%-15s %-15s %-15s %-10d%n",
          prescription.getPrescriptionId(),
          prescription.getMedicationName(),
          prescription.getDosage(),
          prescription.getQuantity());
    }
    System.out.println("----------------------------------------");
  }
}