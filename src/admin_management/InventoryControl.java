package admin_management;

import pharmacy_management.InventoryManager;

public class InventoryControl {
    private InventoryManager inventoryManager;

    public InventoryControl(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    public void viewInventory() {
        System.out.println("Admin View - Current Inventory:");
        inventoryManager.checkInventory();
    }

    public void approveRefill(String medicationName, int quantity) {
        String defaultExpiryDate = "12/31/2024"; // Placeholder expiry date
        System.out.println("Admin approved refill for " + quantity + " units of " + medicationName + " with expiry date " + defaultExpiryDate);
        inventoryManager.requestRefill(medicationName, quantity, defaultExpiryDate);
    }
}
