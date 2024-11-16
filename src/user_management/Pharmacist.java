package user_management;

public class Pharmacist extends User {
    private String licenseNumber;

    public Pharmacist(String id, String name, String rawPassword, String licenseNumber, boolean isFirstLogin) {
        super(id, name, "Pharmacist", rawPassword, isFirstLogin);
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void displayMenu() {
        System.out.println("Pharmacist Menu:");
        System.out.println("1. View Appointment Outcome Record");
        System.out.println("2. Update Prescription Status");
        System.out.println("3. View Medication Inventory");
        System.out.println("4. Submit Replenishment Request");
        System.out.println("5. Logout");
    }
}
