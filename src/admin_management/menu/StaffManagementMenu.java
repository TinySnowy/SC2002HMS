package admin_management.menu;

import admin_management.handlers.staff.*;
import user_management.UserController;

public class StaffManagementMenu extends MenuBase {
    private final StaffDisplayHandler displayHandler;
    private final StaffCreationHandler creationHandler;
    private final StaffRemovalHandler removalHandler;
    
    
    private final StaffFilterHandler filterHandler;
   /**
     * Constructs a StaffManagementMenu with the specified user controller.
     * Initializes all necessary handlers for staff management operations.
     * 
     * @param userController Controller for user-related operations
     */
    public StaffManagementMenu(UserController userController) {
        super();
        this.displayHandler = new StaffDisplayHandler(userController);
        this.creationHandler = new StaffCreationHandler(userController);
        this.removalHandler = new StaffRemovalHandler(userController);
        this.filterHandler = new StaffFilterHandler(userController);
    }

    @Override
    public void display() {
        boolean running = true;
        while (running) {
            menuPrinter.printStaffMenu();
            int choice = getValidChoice(1, 5);
            running = handleChoice(choice);
        }
    }

    /**
     * Displays the staff management menu and handles user interaction.
     * Menu options include:
     * 1. View All Staff
     * 2. Add New Staff
     * 3. Remove Staff
     * 4. Update Staff Information
     * 5. Filter Staff by Role
     * 6. Return to Main Menu
     */
    @Override
    protected boolean handleChoice(int choice) {
        try {
            switch (choice) {
                case 1:
                    displayHandler.viewAllStaff();
                    return true;
                case 2:
                    creationHandler.addNewStaff();
                    return true;
                case 3:
                    removalHandler.removeStaff();
                    return true;
                case 4:
                    filterHandler.filterStaffByRole();
                    return true;
                case 5:
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