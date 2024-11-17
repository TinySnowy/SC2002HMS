package admin_management.menu;

import admin_management.handlers.appointment.AppointmentDisplayHandler;
import appointment_management.AppointmentList;

public class AppointmentManagementMenu extends MenuBase {
    private final AppointmentDisplayHandler displayHandler;

    public AppointmentManagementMenu(AppointmentList appointmentList) {
        super();
        this.displayHandler = new AppointmentDisplayHandler(appointmentList);
    }

    @Override
    public void display() {
        boolean running = true;
        while (running) {
            menuPrinter.printAppointmentMenu();
            int choice = getValidChoice(1, 3);
            running = handleChoice(choice);
        }
    }

    @Override
    protected boolean handleChoice(int choice) {
        try {
            switch (choice) {
                case 1:
                    displayHandler.viewAllAppointments();
                    return true;
                case 2:
                    displayHandler.viewCompletedAppointments();
                    return true;
                case 3:
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