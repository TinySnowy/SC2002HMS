package user_management;

/**
 * Model class representing a Pharmacist user in the HMS.
 * Extends User class with pharmacy-specific functionality.
 * Responsibilities:
 * - Medication dispensing
 * - Inventory management
 * - Prescription processing
 * - Stock monitoring
 * - Replenishment requests
 * Provides pharmacy staff-specific system access and capabilities.
 */
public class Pharmacist extends User {
    /** Professional license number for pharmacy practice */
    private String licenseNumber;

    /**
     * Constructs a new Pharmacist user.
     * Initializes with pharmacy staff privileges and licensing.
     * 
     * @param id Unique pharmacist identifier
     * @param name Pharmacist's full name
     * @param rawPassword Initial password
     * @param licenseNumber Professional license number
     * @param gender Pharmacist's gender
     * @param age Pharmacist's age
     * @param isFirstLogin First login status flag
     * @throws IllegalArgumentException if license number is invalid
     */
    public Pharmacist(String id, String name, String rawPassword, String licenseNumber,
            String gender, int age, boolean isFirstLogin) {
        super(id, name, "Pharmacist", rawPassword, gender, age, isFirstLogin);
        this.licenseNumber = licenseNumber;
    }

    /**
     * Retrieves pharmacist's license number.
     * Used for professional verification and audit.
     * 
     * @return Professional license number
     */
    public String getLicenseNumber() {
        return licenseNumber;
    }

    /**
     * Displays pharmacist-specific menu options.
     * Shows:
     * - Prescription management
     * - Inventory control
     * - Stock monitoring
     * - Request handling
     * Provides access to pharmacy operations.
     */
    public void displayMenu() {
        System.out.println("Pharmacist Menu:");
        System.out.println("1. View Appointment Outcome Record");
        System.out.println("2. Update Prescription Status");
        System.out.println("3. View Medication Inventory");
        System.out.println("4. Submit Replenishment Request");
        System.out.println("5. Logout");
    }

    /**
     * Returns user type identifier.
     * Used for access control and permission management.
     * 
     * @return "Pharmacist" as user type
     */
    @Override
    public String getUserType() {
        return "Pharmacist";
    }
}
