package user_management;

public class Doctor extends User {
    private String specialty;

    public Doctor(String id, String password, String specialty, String name) {
        super(id, password, "Doctor", name);
        this.specialty = specialty;
    }

    public String getSpecialty() {
        return specialty;
    }

    @Override
    public void displayMenu() {
        System.out.println("Doctor Menu");
    }
}
