package user_management;

/**
 * Model class representing an Administrator user in the HMS.
 * Extends User class with administrator-specific functionality.
 * Responsibilities:
 * - System configuration
 * - Staff management
 * - Inventory oversight
 * - Request approval
 * - System monitoring
 * Provides highest level of system access and control.
 */
public class Administrator extends User {

    /**
     * Constructs a new Administrator user.
     * Initializes with administrator privileges and access rights.
     * 
     * @param id Unique administrator identifier
     * @param name Administrator's full name
     * @param rawPassword Initial password
     * @param gender Administrator's gender
     * @param age Administrator's age
     * @param isFirstLogin First login status flag
     */
    public Administrator(String id, String name, String rawPassword,
            String gender, int age, boolean isFirstLogin) {
        super(id, name, "Administrator", rawPassword, gender, age, isFirstLogin);
    }

    /**
     * Displays administrator-specific menu options.
     * Shows:
     * - Staff management options
     * - Appointment oversight
     * - Inventory control
     * - Request management
     * - System navigation
     * Provides access to administrative functions.
     */
    public void displayMenu() {
        System.out.println("Administrator Menu:");
        System.out.println("1. View and Manage Hospital Staff");
        System.out.println("2. View Appointment Details");
        System.out.println("3. View and Manage Medication Inventory");
        System.out.println("4. Approve Replenishment Requests");
        System.out.println("5. Logout");
    }

    /**
     * Returns user type identifier.
     * Used for access control and permission management.
     * 
     * @return "Administrator" as user type
     */
    @Override
    public String getUserType() {
        return "Administrator";
    }
}
