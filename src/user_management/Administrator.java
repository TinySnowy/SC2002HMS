package user_management;

public class Administrator extends User {

    public Administrator(String id, String password) {
        super(id, password, "Administrator");
    }

    @Override
    public void displayMenu() {
        System.out.println("Administrator Menu");
        // Admin-specific options can go here
    }
}
