package user_management;

public class Patient extends User {
    private String contactInfo;

    public Patient(String id, String password, String name, String contactInfo) {
        super(id, password, "Patient", name);
        this.contactInfo = contactInfo;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    @Override
    public void displayMenu() {
        System.out.println("Patient Menu");
    }
}
