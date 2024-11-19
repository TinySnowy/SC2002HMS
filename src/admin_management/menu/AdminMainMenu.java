package admin_management.menu;

import user_management.UserController;
import pharmacy_management.inventory.IReplenishmentService;
import pharmacy_management.inventory.InventoryManager;
import appointment_management.AppointmentList;
import appointment_management.feedback.FeedbackService;
import admin_management.handlers.AdminFeedbackViewer;
import admin_management.utils.MenuPrinter;

public class AdminMainMenu extends MenuBase {
    private final StaffManagementMenu staffMenu;
    private final InventoryManagementMenu inventoryMenu;
    private final AppointmentManagementMenu appointmentMenu;
    private final FeedbackManagementMenu feedbackMenu;
    private final MenuPrinter menuPrinter;

    public AdminMainMenu(UserController userController, InventoryManager inventoryManager,
            IReplenishmentService replenishmentService, AppointmentList appointmentList) {
        super();
        this.menuPrinter = new MenuPrinter();
        this.staffMenu = new StaffManagementMenu(userController);
        this.inventoryMenu = new InventoryManagementMenu(inventoryManager, replenishmentService);
        this.appointmentMenu = new AppointmentManagementMenu(appointmentList);
        this.feedbackMenu = new FeedbackManagementMenu(
            new FeedbackService(), 
            appointmentList, 
            userController
        );
    }

    @Override
    public void display() {
        boolean running = true;
        while (running) {
            menuPrinter.printMainMenu();
            int choice = getValidChoice(1, 5);
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
                feedbackMenu.display();
                return true;
            case 5:
                System.out.println("Logging out from Admin Dashboard...");
                return false;
            default:
                return true;
        }
    }
}