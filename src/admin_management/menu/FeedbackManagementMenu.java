package admin_management.menu;

import appointment_management.AppointmentList;
import appointment_management.feedback.FeedbackService;
import user_management.UserController;
import admin_management.handlers.AdminFeedbackViewer;
import admin_management.utils.MenuPrinter;

public class FeedbackManagementMenu extends MenuBase {
    private final AdminFeedbackViewer feedbackViewer;
    private final MenuPrinter menuPrinter;

    public FeedbackManagementMenu(FeedbackService feedbackService, 
                                 AppointmentList appointmentList,
                                 UserController userController) {
        this.feedbackViewer = new AdminFeedbackViewer(feedbackService, appointmentList, userController);
        this.menuPrinter = new MenuPrinter();
    }

    @Override
    public void display() {
        boolean running = true;
        while (running) {
            menuPrinter.printFeedbackMenu();
            int choice = getValidChoice(1, 5);
            running = handleChoice(choice);
        }
    }

    @Override
    protected boolean handleChoice(int choice) {
        switch (choice) {
            case 1:
                feedbackViewer.viewOverallSummary();
                return true;
            case 2:
                feedbackViewer.viewFeedbackByDoctor();
                return true;
            case 3:
                feedbackViewer.viewRecentFeedback();
                return true;
            case 4:
                feedbackViewer.generateFeedbackReport();
                return true;
            case 5:
                return false;
            default:
                return true;
        }
    }
}