package user_management;

public class Patient extends User {
    private String name;
    private String contactInfo;

    public Patient(String id, String password, String name, String contactInfo) {
        super(id, password, "Patient");
        this.name = name;
        this.contactInfo = contactInfo;
    }

    public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    @Override
    public void displayMenu() {
        System.out.println("Patient Menu");
        // Patient-specific options can go here
    }
}
