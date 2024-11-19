package admin_management.utils;

/**
 * Utility class for printing standardized menu interfaces across the hospital management system.
 * Provides consistent formatting and layout for all system menus, ensuring a uniform user experience.
 * Each method handles printing of a specific menu type with standardized separators and formatting.
 */
public class MenuPrinter {
    /** 
     * Constant separator line used for visual menu separation.
     * Creates consistent visual breaks between menu sections.
     */
    private static final String SEPARATOR = "----------------------------------------";

    /**
     * Prints the main administrative dashboard menu.
     * Displays core system functions including:
     * - Staff Management
     * - Inventory Management
     * - Appointment Management
     * - Feedback Reports
     * - Logout Option
     */
    public void printMainMenu() {
        System.out.println("\nAdmin Dashboard");
        System.out.println(SEPARATOR);
        System.out.println("1. Staff Management");
        System.out.println("2. Inventory Management");
        System.out.println("3. Appointment Management");
        System.out.println("4. View Feedback Reports");
        System.out.println("5. Logout");
        System.out.println(SEPARATOR);
    }

    /**
     * Prints the staff management menu.
     * Displays options for managing hospital staff including:
     * - Viewing staff listings
     * - Adding new staff
     * - Removing staff
     * - Role-based filtering
     * - Navigation back to main menu
     */
    public void printStaffMenu() {
        System.out.println("\nStaff Management");
        System.out.println(SEPARATOR);
        System.out.println("1. View All Staff");
        System.out.println("2. Add New Staff");
        System.out.println("3. Remove Staff");
        System.out.println("4. Filter Staff by Role");
        System.out.println("5. Back to Main Menu");
        System.out.println(SEPARATOR);
    }

    /**
     * Prints the inventory management menu.
     * Displays options for managing hospital inventory including:
     * - Current inventory view
     * - Low stock alerts
     * - Replenishment request management
     * - Navigation back to main menu
     */
    public void printInventoryMenu() {
        System.out.println("\nInventory Management");
        System.out.println(SEPARATOR);
        System.out.println("1. View Current Inventory");
        System.out.println("2. View Low Stock Alerts");
        System.out.println("3. Manage Replenishment Requests");
        System.out.println("4. Back to Main Menu");
        System.out.println(SEPARATOR);
    }

    /**
     * Prints the appointment management menu.
     * Displays options for managing hospital appointments including:
     * - All appointments view
     * - Completed appointments view
     * - Navigation back to main menu
     */
    public void printAppointmentMenu() {
        System.out.println("\nAppointment Management");
        System.out.println(SEPARATOR);
        System.out.println("1. View All Appointments");
        System.out.println("2. View Completed Appointments");
        System.out.println("3. Back to Main Menu");
        System.out.println(SEPARATOR);
    }

    /**
     * Prints the replenishment request management menu.
     * Displays options for managing inventory replenishment including:
     * - Pending requests view
     * - Request approval
     * - Request rejection
     * - Navigation back
     */
    public void printReplenishmentMenu() {
        System.out.println("\nReplenishment Request Management");
        System.out.println(SEPARATOR);
        System.out.println("1. View Pending Requests");
        System.out.println("2. Approve Request");
        System.out.println("3. Reject Request");
        System.out.println("4. Back");
        System.out.println(SEPARATOR);
    }

    /**
     * Prints the feedback reports menu.
     * Displays options for viewing and managing feedback including:
     * - Overall feedback summary
     * - Doctor-specific feedback
     * - Recent feedback
     * - Report generation
     * - Navigation back to main menu
     */
    public void printFeedbackMenu() {
        System.out.println("\nFeedback Reports");
        System.out.println(SEPARATOR);
        System.out.println("1. View Overall Feedback Summary");
        System.out.println("2. View Doctor-wise Feedback");
        System.out.println("3. View Recent Feedback");
        System.out.println("4. Generate Feedback Report");
        System.out.println("5. Back to Main Menu");
        System.out.println(SEPARATOR);
    }
}