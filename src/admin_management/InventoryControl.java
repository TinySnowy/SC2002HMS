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
        System.out.println("Admin approved refill for " + quantity + " units of " + medicationName);
        inventoryManager.requestRefill(medicationName, quantity);
    }
}
