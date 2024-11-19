package admin_management;

import admin_management.menu.*;
import admin_management.handlers.*;
import admin_management.utils.*;
import appointment_management.AppointmentList;
import pharmacy_management.inventory.IReplenishmentService;
import pharmacy_management.inventory.InventoryManager;
import user_management.Administrator;
import user_management.UserController;
/**
 * Primary dashboard interface for hospital administrators.
 * Serves as the main entry point for all administrative functions in the hospital management system.
 * Coordinates access to various management subsystems including:
 * - Staff Management
 * - Inventory Control
 * - Appointment Management
 * - Feedback Monitoring
 */

 
public class AdminDashboard {

    /**
     * Constructs the administrative dashboard with necessary service dependencies.
     * Initializes the main menu with required controllers and services for full functionality.
     * 
     * @param userController Controller for managing user operations and authentication
     * @param inventoryManager Manager for handling inventory and stock control
     * @param replenishmentService Service for managing inventory replenishment requests
     * @param appointmentList List manager for hospital appointments
     */
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
   /**
     * Displays the administrative dashboard interface.
     * Entry point for administrators to access system functions.
     * Delegates to main menu for detailed operation handling.
     * Provides access to:
     * - Staff Management (view, add, remove staff)
     * - Inventory Management (stock control, replenishment)
     * - Appointment Management (view, filter appointments)
     * - Feedback Management (view reports, analyze feedback)
     */
    public void showDashboard() {
        mainMenu.display();
    }
}