package pharmacy_management.inventory;

import java.util.*;

import pharmacy_management.Medication;

import java.time.LocalDate;
import utils.CSVReaderUtil;
import utils.CSVWriterUtil;

/**
 * Service class implementing inventory management operations.
 * Manages:
 * - Medication database
 * - Stock levels
 * - Inventory persistence
 * - Stock alerts
 * Provides core functionality for pharmacy inventory control.
 */
public class InventoryService implements IInventoryService {
  /** Central storage for medication inventory */
  private final Map<String, Medication> medications = new HashMap<>();
  /** Path to medication inventory data file */
  private static final String INVENTORY_FILE = "SC2002HMS/data/Medicine_List.csv";

  /**
   * Initializes inventory service and loads existing data.
   * Sets up medication tracking and loads CSV data.
   */
  public InventoryService() {
    loadInventory();
  }

  /**
   * Loads medication inventory from CSV storage.
   * Processes:
   * - Medication details
   * - Stock levels
   * - Thresholds
   * - Expiry dates
   * Handles file reading and parsing errors.
   */
  private void loadInventory() {
    List<String[]> records = CSVReaderUtil.readCSV(INVENTORY_FILE);
    boolean isFirstRow = true;

    for (String[] record : records) {
      if (isFirstRow) {
        isFirstRow = false;
        continue;
      }

      try {
        String name = record[0];
        int stock = Integer.parseInt(record[1]);
        int threshold = Integer.parseInt(record[2]);
        LocalDate expiry = LocalDate.parse(record[3]);

        medications.put(name, new Medication(name, stock, threshold, expiry));
      } catch (Exception e) {
        System.err.println("Error loading medication: " + e.getMessage());
      }
    }
  }

  /**
   * Retrieves complete list of medications.
   * Returns defensive copy to protect inventory data.
   * 
   * @return List of all medications in inventory
   */
  @Override
  public List<Medication> getAllMedications() {
    return new ArrayList<>(medications.values());
  }

  /**
   * Finds medication by name.
   * Case-sensitive lookup in medication database.
   * 
   * @param name Name of medication to find
   * @return Medication if found, null otherwise
   */
  @Override
  public Medication getMedicationByName(String name) {
    return medications.get(name);
  }

  /**
   * Updates medication stock level.
   * Handles:
   * - Medication existence check
   * - Stock update
   * - Stock persistence
   * 
   * @param medicationName Name of medication to update
   * @param quantity Quantity to add to current stock
   * @return True if stock was updated successfully, false otherwise
   */
  @Override
  public boolean updateStock(String medicationName, int quantity) {
    try {
      // First check if medication exists, if not create it
      Medication medication = medications.get(medicationName);
      if (medication == null) {
        // Create new medication with default threshold
        medication = new Medication(
            medicationName,
            quantity,
            10, // default threshold
            LocalDate.now().plusYears(1) // default expiry 1 year from now
        );
        medications.put(medicationName, medication);
      } else {
        medication.updateStock(quantity);
      }

      saveInventory(); // Save changes to CSV
      return true;
    } catch (Exception e) {
      System.err.println("Error updating stock: " + e.getMessage());
      return false;
    }
  }

  @Override
  public List<Medication> getLowStockMedications() {
    return medications.values().stream()
        .filter(Medication::isLowStock)
        .collect(java.util.stream.Collectors.toList());
  }

  @Override
  public void addMedication(Medication medication) {
    medications.put(medication.getName(), medication);
    saveInventory();
  }

  private void saveInventory() {
    CSVWriterUtil.writeCSV(INVENTORY_FILE, writer -> {
      writer.write("Medication Name,Current Stock,Low Stock Threshold,Expiry Date\n");
      for (Medication med : medications.values()) {
        writer.write(String.format("%s,%d,%d,%s\n",
            med.getName(),
            med.getStockLevel(),
            med.getLowStockThreshold(),
            med.getExpiryDate()));
      }
    });
  }
}
