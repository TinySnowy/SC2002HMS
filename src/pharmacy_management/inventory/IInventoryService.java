package pharmacy_management.inventory;

import java.util.List;

import pharmacy_management.Medication;

/**
 * Interface defining the contract for medication inventory management.
 * Specifies essential operations for:
 * - Medication tracking
 * - Stock management
 * - Inventory queries
 * - Stock alerts
 * Provides standardized methods for pharmacy inventory control.
 */
public interface IInventoryService {
  /**
   * Retrieves all medications in the inventory.
   * Implementation should:
   * - Return complete medication list
   * - Include stock levels
   * - Sort by relevance
   * - Handle empty inventory
   * - Validate data consistency
   * 
   * @return List of all medications in stock
   */
  List<Medication> getAllMedications();

  /**
   * Retrieves specific medication by name.
   * Implementation should:
   * - Perform case-insensitive search
   * - Handle missing medications
   * - Validate input
   * - Return complete medication data
   * 
   * @param name Name of medication to find
   * @return Medication if found, null otherwise
   */
  Medication getMedicationByName(String name);

  /**
   * Updates stock level for specified medication.
   * Implementation should:
   * - Validate medication existence
   * - Check quantity validity
   * - Update stock level
   * - Handle negative quantities
   * - Persist changes
   * - Track stock history
   * 
   * @param medicationName Name of medication to update
   * @param quantity Change in stock (positive for addition, negative for removal)
   * @return true if update successful, false otherwise
   */
  boolean updateStock(String medicationName, int quantity);

  /**
   * Retrieves medications with low stock levels.
   * Implementation should:
   * - Check against threshold
   * - Sort by urgency
   * - Include stock levels
   * - Handle empty results
   * Used for inventory alerts and reordering.
   * 
   * @return List of medications below stock threshold
   */
  List<Medication> getLowStockMedications();

  /**
   * Adds new medication to inventory.
   * Implementation should:
   * - Validate medication data
   * - Check for duplicates
   * - Initialize stock level
   * - Set thresholds
   * - Persist changes
   * - Handle errors
   * 
   * @param medication New medication to add
   * @throws IllegalArgumentException if medication data invalid
   */
  void addMedication(Medication medication);
}
