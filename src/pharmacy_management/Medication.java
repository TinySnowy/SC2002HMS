package pharmacy_management;

import java.time.LocalDate;

public class Medication {
  private final String name;
  private int stockLevel;
  private int lowStockThreshold;
  private final LocalDate expiryDate;

  public Medication(String name, int stockLevel, int lowStockThreshold, LocalDate expiryDate) {
    this.name = name;
    this.stockLevel = stockLevel;
    this.lowStockThreshold = lowStockThreshold;
    this.expiryDate = expiryDate;
  }

  public String getName() {
    return name;
  }

  public int getStockLevel() {
    return stockLevel;
  }

  public int getLowStockThreshold() {
    return lowStockThreshold;
  }

  public void setLowStockThreshold(int lowStockThreshold) {
    this.lowStockThreshold = lowStockThreshold;
  }

  public LocalDate getExpiryDate() {
    return expiryDate;
  }

  public boolean isLowStock() {
    return stockLevel < lowStockThreshold;
  }

  public boolean isExpired() {
    return LocalDate.now().isAfter(expiryDate);
  }

  public void updateStock(int quantity) {
    this.stockLevel += quantity;
  }
}
