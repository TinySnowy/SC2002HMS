package user_management;

public class Doctor extends User {
    private String specialty;

    public Doctor(String id, String password, String specialty) {
        super(id, password, "Doctor");
        this.specialty = specialty;
    }

    public String getSpecialty() {
        return specialty;
    }

    @Override
    public void displayMenu() {
        System.out.println("Doctor Menu");
        // Doctor-specific options can go here
    }
}
