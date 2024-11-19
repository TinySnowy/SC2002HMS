package admin_management.menu;

import admin_management.handlers.inventory.*;
import pharmacy_management.inventory.IReplenishmentService;
import pharmacy_management.inventory.InventoryManager;
/**
 * Manages the inventory interface for administrators.
 * Provides a menu-driven interface for viewing inventory status, managing stock levels,
 * and handling replenishment requests. Extends MenuBase for common menu functionality.
 */
public class InventoryManagementMenu extends MenuBase {
    private final InventoryDisplayHandler displayHandler;
    private final ReplenishmentRequestHandler replenishmentHandler;

    public InventoryManagementMenu(InventoryManager inventoryManager,
            IReplenishmentService replenishmentService) {
        super();
        this.displayHandler = new InventoryDisplayHandler(inventoryManager);
        this.replenishmentHandler = new ReplenishmentRequestHandler(inventoryManager, replenishmentService);
    }

    @Override
    public void display() {
        boolean running = true;
        while (running) {
            menuPrinter.printInventoryMenu();
            int choice = getValidChoice(1, 4);
            running = handleChoice(choice);
        }
    }

    @Override
    protected boolean handleChoice(int choice) {
        try {
            switch (choice) {
                case 1:
                    displayHandler.viewInventory();
                    return true;
                case 2:
                    displayHandler.viewLowStockAlerts();
                    return true;
                case 3:
                    handleReplenishmentMenu();
                    return true;
                case 4:
                    return false;
                default:
                    return true;
            }
        } catch (Exception e) {
            showError(e.getMessage());
            return true;
        }
    }

    private void handleReplenishmentMenu() {
        boolean running = true;
        while (running) {
            menuPrinter.printReplenishmentMenu();
            int choice = getValidChoice(1, 4);
            running = handleReplenishmentChoice(choice);
        }
    }

    private boolean handleReplenishmentChoice(int choice) {
        try {
            switch (choice) {
                case 1:
                    replenishmentHandler.viewPendingRequests();
                    return true;
                case 2:
                    replenishmentHandler.approveRequest();
                    return true;
                case 3:
                    replenishmentHandler.rejectRequest();
                    return true;
                case 4:
                    return false;
                default:
                    return true;
            }
        } catch (Exception e) {
            showError(e.getMessage());
            return true;
        }
    }
}