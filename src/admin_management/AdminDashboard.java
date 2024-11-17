package admin_management;

import admin_management.menu.AdminMainMenu;
import user_management.UserController;
import pharmacy_management.InventoryManager;
import pharmacy_management.IReplenishmentService;
import appointment_management.AppointmentList;

public class AdminDashboard {
    private final AdminMainMenu mainMenu;

    public AdminDashboard(UserController userController, 
                         InventoryManager inventoryManager,
                         IReplenishmentService replenishmentService, 
                         AppointmentList appointmentList) {
        this.mainMenu = new AdminMainMenu(userController, 
                                        inventoryManager,
                                        replenishmentService, 
                                        appointmentList);
    }

    public void showDashboard() {
        try {
            mainMenu.display();
        } catch (Exception e) {
            System.err.println("Critical error in Admin Dashboard: " + e.getMessage());
        }
    }
}