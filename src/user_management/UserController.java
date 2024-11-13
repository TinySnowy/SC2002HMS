package user_management;

import java.util.HashMap;
import java.util.Map;

public class UserController {
    private Map<String, User> userDatabase;

    public UserController() {
        userDatabase = new HashMap<>();
    }

    public void addUser(User user) {
        userDatabase.put(user.getId(), user);
    }

    public User getUserById(String id) {
        return userDatabase.get(id);
    }

    // Mock data method to populate users
    public void initializeUsers() {
        addUser(new Patient("P001", "pass123", "Alice", "alice@example.com"));
        addUser(new Doctor("D001", "docpass", "Cardiology"));
        addUser(new Pharmacist("PH001", "pharmpass", "L12345"));
        addUser(new Administrator("A001", "adminpass"));
    }
}
