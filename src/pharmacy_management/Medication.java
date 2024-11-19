package pharmacy_management;

import java.time.LocalDate;

/**
 * Model class representing a medication in the HMS pharmacy inventory.
 * Manages:
 * - Medication identification
 * - Stock tracking
 * - Threshold alerts
 * - Expiry monitoring
 * Provides core functionality for medication inventory management.
 */
public class Medication {
  /** Name identifier for the medication */
  private final String name;
  /** Current quantity in stock */
  private int stockLevel;
  /** Threshold for low stock alerts */
  private int lowStockThreshold;
  /** Expiration date of the medication */
  private final LocalDate expiryDate;

  /**
   * Constructs a new medication with required details.
   * Initializes stock tracking and alert thresholds.
   * 
   * @param name Unique medication name
   * @param stockLevel Initial quantity in stock
   * @param lowStockThreshold Alert threshold level
   * @param expiryDate Medication expiry date
   * @throws IllegalArgumentException if name is null or stock levels negative
   */
  public Medication(String name, int stockLevel, int lowStockThreshold, LocalDate expiryDate) {
    this.name = name;
    this.stockLevel = stockLevel;
    this.lowStockThreshold = lowStockThreshold;
    this.expiryDate = expiryDate;
  }

  /**
   * Retrieves medication name.
   * Used for identification and inventory matching.
   * 
   * @return Medication name string
   */
  public String getName() {
    return name;
  }

  /**
   * Retrieves current stock level.
   * Used for inventory tracking and dispensing decisions.
   * 
   * @return Current quantity in stock
   */
  public int getStockLevel() {
    return stockLevel;
  }

  /**
   * Retrieves low stock alert threshold.
   * Used for inventory management and reordering.
   * 
   * @return Low stock threshold value
   */
  public int getLowStockThreshold() {
    return lowStockThreshold;
  }

  /**
   * Updates low stock threshold level.
   * Used for adjusting inventory alert settings.
   * 
   * @param lowStockThreshold New threshold value
   * @throws IllegalArgumentException if threshold negative
   */
  public void setLowStockThreshold(int lowStockThreshold) {
    this.lowStockThreshold = lowStockThreshold;
  }

  /**
   * Retrieves medication expiry date.
   * Used for stock validity monitoring.
   * 
   * @return Expiration date
   */
  public LocalDate getExpiryDate() {
    return expiryDate;
  }

  /**
   * Checks if stock level is below threshold.
   * Used for inventory alerts and reordering.
   * 
   * @return true if stock below threshold
   */
  public boolean isLowStock() {
    return stockLevel < lowStockThreshold;
  }

  /**
   * Checks if medication is expired.
   * Used for stock validity and safety.
   * 
   * @return true if current date past expiry
   */
  public boolean isExpired() {
    return LocalDate.now().isAfter(expiryDate);
  }

  /**
   * Updates current stock level.
   * Handles both additions and removals.
   * Used for inventory management.
   * 
   * @param quantity Change in stock (positive for addition, negative for removal)
   * @throws IllegalStateException if resulting stock would be negative
   */
  public void updateStock(int quantity) {
    this.stockLevel += quantity;
  }
}
