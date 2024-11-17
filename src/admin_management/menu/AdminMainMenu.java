package admin_management.menu;

import user_management.UserController;
import pharmacy_management.InventoryManager;
import pharmacy_management.IReplenishmentService;
import appointment_management.AppointmentList;

public class AdminMainMenu extends MenuBase {
    private final StaffManagementMenu staffMenu;
    private final InventoryManagementMenu inventoryMenu;
    private final AppointmentManagementMenu appointmentMenu;

    public AdminMainMenu(UserController userController, InventoryManager inventoryManager,
            IReplenishmentService replenishmentService, AppointmentList appointmentList) {
        super();
        this.staffMenu = new StaffManagementMenu(userController);
        this.inventoryMenu = new InventoryManagementMenu(inventoryManager, replenishmentService);
        this.appointmentMenu = new AppointmentManagementMenu(appointmentList);
    }

    @Override
    public void display() {
        boolean running = true;
        while (running) {
            menuPrinter.printMainMenu();
            int choice = getValidChoice(1, 4);
            running = handleChoice(choice);
        }
    }

    @Override
    protected boolean handleChoice(int choice) {
        switch (choice) {
            case 1:
                staffMenu.display();
                return true;
            case 2:
                inventoryMenu.display();
                return true;
            case 3:
                appointmentMenu.display();
                return true;
            case 4:
                System.out.println("Logging out from Admin Dashboard...");
                return false;
            default:
                return true;
        }
    }
}