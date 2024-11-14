package pharmacy_management;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {
    // Map to store medication, quantity, and expiry date
    private Map<String, Map<String, Integer>> inventory; // {MedicationName: {ExpiryDate: Quantity}}
    private int lowStockThreshold = 10;

    public InventoryManager() {
        inventory = new HashMap<>();
    }

    // Add medication with expiry date and quantity
    public void addMedication(String medicationName, int quantity, String expiryDate) {
        inventory.putIfAbsent(medicationName, new HashMap<>());
        Map<String, Integer> stockByDate = inventory.get(medicationName);
        stockByDate.put(expiryDate, stockByDate.getOrDefault(expiryDate, 0) + quantity);

        System.out.println("Added " + quantity + " units of " + medicationName + " (Expiry: " + expiryDate + ") to inventory.");
        checkLowStock(medicationName);
    }

    // Dispense medication (considering expiry)
    public boolean dispenseMedication(String medicationName, int quantity) {
        if (!inventory.containsKey(medicationName)) {
            System.out.println("Medication not available in inventory.");
            return false;
        }

        Map<String, Integer> stockByDate = inventory.get(medicationName);
        for (Map.Entry<String, Integer> entry : stockByDate.entrySet()) {
            String expiryDate = entry.getKey();
            int stock = entry.getValue();

            if (stock >= quantity) {
                stockByDate.put(expiryDate, stock - quantity);
                System.out.println("Dispensed " + quantity + " units of " + medicationName + " (Expiry: " + expiryDate + ").");
                return true;
            }
        }
        System.out.println("Insufficient stock for " + medicationName + " or stock expired.");
        return false;
    }

    // Request refill
    public void requestRefill(String medicationName, int quantity, String expiryDate) {
        System.out.println("Refill request for " + quantity + " units of " + medicationName + " (Expiry: " + expiryDate + ").");
        addMedication(medicationName, quantity, expiryDate);
    }

    // Check inventory with expiry dates
    public void checkInventory() {
        System.out.println("Current Inventory:");
        for (Map.Entry<String, Map<String, Integer>> entry : inventory.entrySet()) {
            String medicationName = entry.getKey();
            Map<String, Integer> stockByDate = entry.getValue();
            System.out.println("Medication: " + medicationName);
            for (Map.Entry<String, Integer> dateEntry : stockByDate.entrySet()) {
                System.out.println("  Expiry: " + dateEntry.getKey() + ", Quantity: " + dateEntry.getValue());
            }
        }
    }

    // Check low stock for a medication
    private void checkLowStock(String medicationName) {
        Map<String, Integer> stockByDate = inventory.get(medicationName);
        int totalStock = stockByDate.values().stream().mapToInt(Integer::intValue).sum();

        if (totalStock < lowStockThreshold) {
            System.out.println("Warning: Low stock for " + medicationName + ". Total stock: " + totalStock + " units.");
        }
    }
}
