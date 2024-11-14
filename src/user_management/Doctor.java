package user_management;

public class Doctor extends User {
    private String specialty;
    private String availability; // New field for availability

    public Doctor(String id, String password, String name, String specialty) {
        super(id, password, "Doctor", name);
        this.specialty = specialty;
        this.availability = "Not set"; // Default message
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    @Override
    public void displayMenu() {
        System.out.println("Doctor Menu");
    }
}
