package login_system;

import user_management.User;
import user_management.UserController;
import java.util.Scanner;

public class LoginPage {
    private final UserController userController;
    private final LoginCredentials loginCredentials;
    private final Scanner scanner;
    private static final int MAX_LOGIN_ATTEMPTS = 3;

    public LoginPage(UserController userController) {
        this.userController = userController;
        this.loginCredentials = new LoginCredentials();
        this.scanner = new Scanner(System.in);
    }

    public User start() {
        while (true) {
            System.out.println("\n=== Hospital Management System Login ===");
            System.out.print("Enter User ID (or 'exit' to quit): ");
            String id = scanner.nextLine().trim();

            if (id.equalsIgnoreCase("exit")) {
                return null;
            }

            User user = userController.getUserById(id);
            if (user == null) {
                System.out.println("Invalid User ID. Please try again.");
                continue;
            }

            int attempts = 0;
            while (attempts < MAX_LOGIN_ATTEMPTS) {
                System.out.print("Enter Password: ");
                String password = scanner.nextLine();

                if (loginCredentials.authenticateUser(id, password)) {
                    if (loginCredentials.isFirstLogin(id)) {
                        if (handleFirstTimeLogin(user, id)) {
                            return user;
                        }
                        break; // Break and return to main login loop if password change was cancelled
                    }
                    return user;
                } else if (password.equals("password") && loginCredentials.isFirstLogin(id)) {
                    // Allow default password only for first-time login
                    if (handleFirstTimeLogin(user, id)) {
                        return user;
                    }
                    break;
                } else {
                    attempts++;
                    if (attempts < MAX_LOGIN_ATTEMPTS) {
                        System.out.println("Invalid password. Please try again. " +
                                (MAX_LOGIN_ATTEMPTS - attempts) + " attempts remaining.");
                    } else {
                        System.out.println("Maximum login attempts exceeded. Please try again later.");
                    }
                }
            }
        }
    }

    private boolean handleFirstTimeLogin(User user, String id) {
        System.out.println("First time login detected. You must change your password.");
        while (true) {
            System.out.print("Enter new password (minimum 6 characters): ");
            String newPassword = scanner.nextLine();

            if (newPassword.length() < 6) {
                System.out.println("Password must be at least 6 characters long.");
                continue;
            }

            System.out.print("Confirm new password: ");
            String confirmPassword = scanner.nextLine();

            if (!newPassword.equals(confirmPassword)) {
                System.out.println("Passwords do not match. Please try again.");
                continue;
            }

            // Update password in both systems
            loginCredentials.updatePassword(id, newPassword);
            user.setPassword(newPassword);
            user.setFirstLogin(false);
            userController.persistAllData();

            System.out.println("Password changed successfully!");
            return true;
        }
    }
}