package admin_management.menu;

import user_management.UserController;
import pharmacy_management.inventory.IReplenishmentService;
import pharmacy_management.inventory.InventoryManager;
import appointment_management.AppointmentList;
import appointment_management.feedback.FeedbackService;
import admin_management.handlers.AdminFeedbackViewer;
import admin_management.utils.MenuPrinter;

/**
 * Main menu interface for administrative functions in the hospital management system.
 * Provides centralized access to staff management, inventory control, appointment
 * management, and feedback monitoring through a menu-driven interface.
 * Extends MenuBase to inherit common menu functionality.
 */
public class AdminMainMenu extends MenuBase {
    /** Menu handler for staff-related operations */
    private final StaffManagementMenu staffMenu;
    
    /** Menu handler for inventory-related operations */
    private final InventoryManagementMenu inventoryMenu;
    
    /** Menu handler for appointment-related operations */
    private final AppointmentManagementMenu appointmentMenu;
    
    /** Menu handler for feedback-related operations */
    private final FeedbackManagementMenu feedbackMenu;
    
    /** Utility for printing menu options */
    private final MenuPrinter menuPrinter;

    /**
     * Constructs the AdminMainMenu with all necessary controllers and services.
     * Initializes sub-menus for different administrative functions.
     * 
     * @param userController Controller for user-related operations
     * @param inventoryManager Manager for inventory operations
     * @param replenishmentService Service for handling replenishment requests
     * @param appointmentList List of system appointments
     */
    public AdminMainMenu(UserController userController, 
                        InventoryManager inventoryManager,
                        IReplenishmentService replenishmentService, 
                        AppointmentList appointmentList) {
        super();
        // Initialize menu printer
        this.menuPrinter = new MenuPrinter();
        
        // Initialize sub-menus with their required dependencies
        this.staffMenu = new StaffManagementMenu(userController);
        this.inventoryMenu = new InventoryManagementMenu(inventoryManager, replenishmentService);
        this.appointmentMenu = new AppointmentManagementMenu(appointmentList);
        this.feedbackMenu = new FeedbackManagementMenu(
            new FeedbackService(), 
            appointmentList, 
            userController
        );
    }

    /**
     * Displays the main administrative menu and handles user interaction.
     * Continues displaying menu until user chooses to exit.
     * Menu options include:
     * 1. Staff Management
     * 2. Inventory Management
     * 3. Appointment Management
     * 4. Feedback Management
     * 5. Logout
     */
    @Override
    public void display() {
        boolean running = true;
        while (running) {
            // Display main menu options
            menuPrinter.printMainMenu();
            
            // Get valid user choice (1-5)
            int choice = getValidChoice(1, 5);
            
            // Process user selection
            running = handleChoice(choice);
        }
    }

    /**
     * Handles the user's menu selection and directs to appropriate sub-menu.
     * 
     * @param choice User's menu selection (1-5)
     * @return true to continue displaying menu, false to logout
     */
    @Override
    protected boolean handleChoice(int choice) {
        switch (choice) {
            case 1:
                // Access staff management functions
                staffMenu.display();
                return true;
                
            case 2:
                // Access inventory management functions
                inventoryMenu.display();
                return true;
                
            case 3:
                // Access appointment management functions
                appointmentMenu.display();
                return true;
                
            case 4:
                // Access feedback management functions
                feedbackMenu.display();
                return true;
                
            case 5:
                // Logout from admin dashboard
                System.out.println("Logging out from Admin Dashboard...");
                return false;
                
            default:
                return true;
        }
    }
}