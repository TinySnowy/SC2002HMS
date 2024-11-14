package user_management;

public class Patient extends User {
    private String contactInfo;
    private String email;

    public Patient(String id, String password, String name, String contactInfo, String email) {
        super(id, password, "Patient", name);
        this.contactInfo = contactInfo;
        this.email = email;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public String getEmail() {
        return email;
    }

    // Updated method to set both email and contact information
    public void setContactInfo(String email, String contactInfo) {
        this.email = email;
        this.contactInfo = contactInfo;
    }

    @Override
    public void displayMenu() {
        System.out.println("Patient Menu");
    }
}
