package admin_management.menu;

import appointment_management.AppointmentList;
import appointment_management.feedback.FeedbackService;
import user_management.UserController;
import admin_management.handlers.AdminFeedbackViewer;
import admin_management.utils.MenuPrinter;

/**
 * Manages the feedback interface for administrators.
 * Provides a menu-driven interface for viewing feedback summaries, doctor-specific feedback,
 * recent feedback entries, and generating feedback reports.
 * Extends MenuBase to inherit common menu functionality.
 */
public class FeedbackManagementMenu extends MenuBase {
    /** Handler for viewing and analyzing feedback */
    private final AdminFeedbackViewer feedbackViewer;
    
    /** Utility for printing menu options */
    private final MenuPrinter menuPrinter;

    /**
     * Constructs a FeedbackManagementMenu with necessary services and controllers.
     * Initializes the feedback viewer for managing feedback operations.
     * 
     * @param feedbackService Service for managing feedback data
     * @param appointmentList List of appointments for reference
     * @param userController Controller for user operations
     */
    public FeedbackManagementMenu(FeedbackService feedbackService, 
                                 AppointmentList appointmentList,
                                 UserController userController) {
        this.feedbackViewer = new AdminFeedbackViewer(feedbackService, appointmentList, userController);
        this.menuPrinter = new MenuPrinter();
    }

    /**
     * Displays the feedback management menu and handles user interaction.
     * Menu options include:
     * 1. View Overall Summary
     * 2. View Feedback by Doctor
     * 3. View Recent Feedback
     * 4. Generate Feedback Report
     * 5. Return to Main Menu
     */
    @Override
    public void display() {
        boolean running = true;
        while (running) {
            // Display menu options
            menuPrinter.printFeedbackMenu();
            
            // Get valid user choice (1-5)
            int choice = getValidChoice(1, 5);
            
            // Process user selection
            running = handleChoice(choice);
        }
    }

    /**
     * Handles the user's menu selection and executes corresponding actions.
     * 
     * @param choice User's menu selection (1-5)
     * @return true to continue displaying menu, false to exit
     */
    @Override
    protected boolean handleChoice(int choice) {
        switch (choice) {
            case 1:
                // Display overall feedback summary
                feedbackViewer.viewOverallSummary();
                return true;
                
            case 2:
                // View feedback filtered by doctor
                feedbackViewer.viewFeedbackByDoctor();
                return true;
                
            case 3:
                // Display most recent feedback entries
                feedbackViewer.viewRecentFeedback();
                return true;
                
            case 4:
                // Generate comprehensive feedback report
                feedbackViewer.generateFeedbackReport();
                return true;
                
            case 5:
                // Return to main menu
                return false;
                
            default:
                return true;
        }
    }
}