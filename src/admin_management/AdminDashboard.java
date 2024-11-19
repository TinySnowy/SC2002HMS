package admin_management;

import admin_management.menu.*;
import admin_management.handlers.*;
import admin_management.utils.*;
import appointment_management.AppointmentList;
import pharmacy_management.inventory.IReplenishmentService;
import pharmacy_management.inventory.InventoryManager;
import user_management.Administrator;
import user_management.UserController;

public class AdminDashboard {
    private final AdminMainMenu mainMenu;

    public AdminDashboard(UserController userController, 
                         InventoryManager inventoryManager,
                         IReplenishmentService replenishmentService,
                         AppointmentList appointmentList) {
        this.mainMenu = new AdminMainMenu(
            userController,
            inventoryManager,
            replenishmentService,
            appointmentList
        );
    }

    public void showDashboard() {
        mainMenu.display();
    }
}