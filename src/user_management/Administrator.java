package user_management;

public class Administrator extends User {

    public Administrator(String id, String password, String name) {
        super(id, password, "Administrator", name);
    }

    @Override
    public void displayMenu() {
        System.out.println("Administrator Menu");
    }
}
