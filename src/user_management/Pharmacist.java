package user_management;

public class Pharmacist extends User {
    private String licenseNumber;

    public Pharmacist(String id, String password, String licenseNumber, String name) {
        super(id, password, "Pharmacist", name);
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    @Override
    public void displayMenu() {
        System.out.println("Pharmacist Menu");
    }
}
