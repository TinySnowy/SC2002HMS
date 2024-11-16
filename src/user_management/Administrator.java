package user_management;

public class Administrator extends User {

    public Administrator(String id, String name, String rawPassword, boolean isFirstLogin) {
        super(id, name, "Administrator", rawPassword, isFirstLogin);
    }

    public void displayMenu() {
        System.out.println("Administrator Menu:");
        System.out.println("1. View and Manage Hospital Staff");
        System.out.println("2. View Appointment Details");
        System.out.println("3. View and Manage Medication Inventory");
        System.out.println("4. Approve Replenishment Requests");
        System.out.println("5. Logout");
    }
}
