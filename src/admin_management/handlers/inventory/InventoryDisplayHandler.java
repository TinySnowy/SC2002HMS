package admin_management.handlers.inventory;

import pharmacy_management.inventory.InventoryManager;

/**
 * Handles the display of inventory information for administrators.
 * This class provides methods to view current inventory status and low stock alerts.
 */
public class InventoryDisplayHandler {
    /** The inventory manager instance used to retrieve inventory data */
    private final InventoryManager inventoryManager;

    /**
     * Constructs an InventoryDisplayHandler with the specified inventory manager.
     * @param inventoryManager The inventory manager to use for data retrieval
     */
    public InventoryDisplayHandler(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    /**
     * Displays the current status of all items in the inventory.
     * Shows quantity and other relevant details for each item.
     */
    public void viewInventory() {
        System.out.println("\nCurrent Inventory Status:");
        System.out.println("----------------------------------------");
        inventoryManager.checkInventory();
    }

    /**
     * Displays alerts for items that have fallen below their minimum stock threshold.
     * Helps administrators identify items that need to be restocked.
     */
    public void viewLowStockAlerts() {
        System.out.println("\nLow Stock Alerts:");
        System.out.println("----------------------------------------");
        inventoryManager.checkAllLowStock();
    }
}