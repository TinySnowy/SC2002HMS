package login_system;

import user_management.User;
import user_management.UserController;
import java.util.Scanner;
import java.util.regex.Pattern;

public class LoginPage {
    private final UserController userController;
    private final LoginCredentials loginCredentials;
    private final Scanner scanner;
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 20;
    private static final String DEFAULT_PASSWORD = "password";
    
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,20}$");

    public LoginPage(UserController userController) {
        if (userController == null) {
            throw new IllegalArgumentException("UserController cannot be null");
        }
        this.userController = userController;
        this.loginCredentials = new LoginCredentials();
        this.scanner = new Scanner(System.in);
    }

    public User start() {
        int globalAttempts = 0;
        final int MAX_GLOBAL_ATTEMPTS = 5;

        while (globalAttempts < MAX_GLOBAL_ATTEMPTS) {
            displayWelcomeBanner();
            
            System.out.print("Enter User ID (or 'exit' to quit): ");
            String id = scanner.nextLine();

            if (id == null || id.trim().equalsIgnoreCase("exit")) {
                System.out.println("Thank you for using HMS. Goodbye!");
                return null;
            }

            id = id.trim();
            if (id.isEmpty()) {
                System.out.println("User ID cannot be empty. Please try again.");
                globalAttempts++;
                continue;
            }

            User user = userController.getUserById(id);
            if (user == null) {
                System.out.println("Invalid User ID. Please try again.");
                globalAttempts++;
                continue;
            }

            User authenticatedUser = handleLogin(user, id);
            if (authenticatedUser != null) {
                return authenticatedUser;
            }

            globalAttempts++;
            if (globalAttempts < MAX_GLOBAL_ATTEMPTS) {
                System.out.println("\nLogin failed. " + 
                    (MAX_GLOBAL_ATTEMPTS - globalAttempts) + " total attempts remaining.");
            } else {
                System.out.println("\nMaximum login attempts exceeded. Please contact system administrator.");
            }
        }

        return null;
    }

    private User handleLogin(User user, String id) {
        int attempts = 0;
        while (attempts < MAX_LOGIN_ATTEMPTS) {
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            // Check for default password first
            if (password.equals(DEFAULT_PASSWORD) && loginCredentials.isFirstLogin(id)) {
                if (handleFirstTimeLogin(user, id)) {
                    System.out.println("\nLogin successful. Welcome, " + user.getName() + "!");
                    return user;
                }
                return null;
            }

            // Then check for regular authentication
            if (loginCredentials.authenticateUser(id, password)) {
                if (loginCredentials.isFirstLogin(id)) {
                    if (handleFirstTimeLogin(user, id)) {
                        System.out.println("\nLogin successful. Welcome, " + user.getName() + "!");
                        return user;
                    }
                    return null;
                }
                System.out.println("\nLogin successful. Welcome back, " + user.getName() + "!");
                return user;
            }

            attempts++;
            if (attempts < MAX_LOGIN_ATTEMPTS) {
                System.out.println("Invalid password. Please try again. " +
                        (MAX_LOGIN_ATTEMPTS - attempts) + " attempts remaining.");
            } else {
                System.out.println("Maximum login attempts exceeded for this user ID.");
            }
        }
        return null;
    }

    private boolean handleFirstTimeLogin(User user, String id) {
        System.out.println("\nFirst time login detected. You must change your password.");
        System.out.println("Password requirements:");
        System.out.println("- Between " + MIN_PASSWORD_LENGTH + " and " + MAX_PASSWORD_LENGTH + " characters");
        System.out.println("- At least one uppercase letter");
        System.out.println("- At least one lowercase letter");
        System.out.println("- At least one number");
        System.out.println("- No whitespace allowed");

        int attempts = 0;
        final int MAX_PASSWORD_CHANGE_ATTEMPTS = 3;

        while (attempts < MAX_PASSWORD_CHANGE_ATTEMPTS) {
            System.out.print("\nEnter new password: ");
            String newPassword = scanner.nextLine();

            if (!isValidPassword(newPassword)) {
                attempts++;
                if (attempts < MAX_PASSWORD_CHANGE_ATTEMPTS) {
                    System.out.println("Invalid password format. Please try again. " + 
                        (MAX_PASSWORD_CHANGE_ATTEMPTS - attempts) + " attempts remaining.");
                } else {
                    System.out.println("Maximum password change attempts exceeded. " + 
                        "Please try logging in again later.");
                    return false;
                }
                continue;
            }

            System.out.print("Confirm new password: ");
            String confirmPassword = scanner.nextLine();

            if (!newPassword.equals(confirmPassword)) {
                System.out.println("Passwords do not match. Please try again.");
                attempts++;
                continue;
            }

            try {
                loginCredentials.updatePassword(id, newPassword);
                user.setPassword(newPassword);
                user.setFirstLogin(false);
                userController.persistAllData();

                System.out.println("\nPassword changed successfully!");
                return true;
            } catch (Exception e) {
                System.out.println("Error updating password.");
                return false;
            }
        }
        return false;
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            System.out.println("Password must be between " + MIN_PASSWORD_LENGTH + 
                             " and " + MAX_PASSWORD_LENGTH + " characters.");
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            System.out.println("Password must contain at least one uppercase letter, " + 
                             "one lowercase letter, one number, and no whitespace.");
            return false;
        }

        return true;
    }

    private void displayWelcomeBanner() {
        System.out.println("\n=========================================");
        System.out.println("    Hospital Management System Login");
        System.out.println("=========================================");
    }

    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}