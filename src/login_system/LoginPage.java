package login_system;

import user_management.UserController;
import user_management.User;

import java.util.Scanner;

public class LoginPage {
    private UserController userController;
    private RoleSelector roleSelector;
    private Scanner scanner;

    public LoginPage(UserController userController, RoleSelector roleSelector) {
        this.userController = userController;
        this.roleSelector = roleSelector;
        this.scanner = new Scanner(System.in);
    }

    public User login() {
        System.out.print("Enter User ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        User user = userController.getUserById(id);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Login successful! Welcome " + user.getRole());
            roleSelector.navigateToRoleDashboard(user); // Use roleSelector for navigation
            return user;
        } else {
            System.out.println("Invalid ID or Password. Please try again.");
            return null;
        }
    }
}
