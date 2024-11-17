package admin_management.menu;

import admin_management.handlers.staff.*;
import user_management.UserController;

public class StaffManagementMenu extends MenuBase {
    private final StaffDisplayHandler displayHandler;
    private final StaffCreationHandler creationHandler;
    private final StaffRemovalHandler removalHandler;
    private final StaffFilterHandler filterHandler;

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