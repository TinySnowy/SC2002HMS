package admin_management.handlers.inventory;

import pharmacy_management.InventoryManager;

public class InventoryDisplayHandler {
    private final InventoryManager inventoryManager;

    public InventoryDisplayHandler(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    public void viewInventory() {
        System.out.println("\nCurrent Inventory Status:");
        System.out.println("----------------------------------------");
        inventoryManager.checkInventory();
    }

    public void viewLowStockAlerts() {
        System.out.println("\nLow Stock Alerts:");
        System.out.println("----------------------------------------");
        inventoryManager.checkAllLowStock();
    }
}