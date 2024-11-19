package admin_management.utils;

public class MenuPrinter {
    private static final String SEPARATOR = "----------------------------------------";

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

    public void printInventoryMenu() {
        System.out.println("\nInventory Management");
        System.out.println(SEPARATOR);
        System.out.println("1. View Current Inventory");
        System.out.println("2. View Low Stock Alerts");
        System.out.println("3. Manage Replenishment Requests");
        System.out.println("4. Back to Main Menu");
        System.out.println(SEPARATOR);
    }

    public void printAppointmentMenu() {
        System.out.println("\nAppointment Management");
        System.out.println(SEPARATOR);
        System.out.println("1. View All Appointments");
        System.out.println("2. View Completed Appointments");
        System.out.println("3. Back to Main Menu");
        System.out.println(SEPARATOR);
    }

    public void printReplenishmentMenu() {
        System.out.println("\nReplenishment Request Management");
        System.out.println(SEPARATOR);
        System.out.println("1. View Pending Requests");
        System.out.println("2. Approve Request");
        System.out.println("3. Reject Request");
        System.out.println("4. Back");
        System.out.println(SEPARATOR);
    }

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