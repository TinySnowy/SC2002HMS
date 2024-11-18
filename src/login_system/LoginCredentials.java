package login_system;

import utils.CSVReaderUtil;
import utils.CSVWriterUtil;
import utils.PasswordUtil;
import user_management.UserController;

import java.util.*;

public class LoginCredentials {
    private Map<String, LoginInfo> loginDatabase;
    private static final String LOGIN_FILE = "SC2002HMS/data/Login.csv";
    private static boolean isInitializing = false;
    private final UserController userController;

    private static class LoginInfo {
        String id;
        String role; 
        String passwordHash;
        boolean isFirstLogin;

        LoginInfo(String id, String role, String passwordHash, boolean isFirstLogin) {
            this.id = id;
            this.role = role;
            this.passwordHash = passwordHash;
            this.isFirstLogin = isFirstLogin;
        }
    }

    public LoginCredentials() {
        this.loginDatabase = new HashMap<>();
        this.userController = UserController.getInstance();
        loadLoginData();
        System.out.println("LoginCredentials initialized with " + loginDatabase.size() + " users");
    }

    private void loadLoginData() {
        if (isInitializing) return;
        
        List<String[]> records = CSVReaderUtil.readCSV(LOGIN_FILE);
        
        System.out.println("Loading login data from: " + LOGIN_FILE);
        
        if (records.isEmpty()) {
            System.out.println("No existing login data found. Initializing default data...");
            isInitializing = true;
            initializeLoginData();
            isInitializing = false;
            return;
        }

        loginDatabase.clear();
        boolean isFirstRow = true;
        
        for (String[] record : records) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            if (record.length >= 4) {
                String id = record[0].trim();
                String role = record[1].trim();
                String passwordHash = record[2].trim();
                boolean isFirstLogin = Boolean.parseBoolean(record[3].trim());
                
                // Verify user exists in UserController
                if (userController.getUserById(id) != null) {
                    loginDatabase.put(id, new LoginInfo(id, role, passwordHash, isFirstLogin));
                    System.out.println("Loaded login info for user: " + id);
                } else {
                    System.out.println("Warning: User " + id + " found in login data but not in user database");
                }
            }
        }
    }

    private void initializeLoginData() {
        System.out.println("Initializing default login data...");
        String defaultPasswordHash = PasswordUtil.hashPassword("password");
        loginDatabase.clear();

        // Get all users from UserController
        List<user_management.User> allUsers = userController.getAllUsers();
        for (user_management.User user : allUsers) {
            loginDatabase.put(user.getId(), 
                new LoginInfo(
                    user.getId(),
                    user.getUserType(),
                    defaultPasswordHash,
                    true
                )
            );
            System.out.println("Created login entry for: " + user.getId());
        }

        // Save to file
        saveLoginData();
        System.out.println("Default login data initialized for " + loginDatabase.size() + " users");
    }

    public boolean authenticateUser(String id, String password) {
        if (id == null || password == null) {
            System.out.println("Authentication failed: null id or password");
            return false;
        }

        LoginInfo info = loginDatabase.get(id);
        if (info == null) {
            System.out.println("Authentication failed: no login info found for ID: " + id);
            return false;
        }

        boolean isValid = PasswordUtil.verifyPassword(password, info.passwordHash);
        System.out.println("Authentication attempt for " + id + ": " + (isValid ? "successful" : "failed"));
        return isValid;
    }

    public String getUserRole(String id) {
        LoginInfo info = loginDatabase.get(id);
        if (info == null) {
            System.out.println("No role found for user: " + id);
            return null;
        }
        return info.role;
    }

    public boolean isFirstLogin(String id) {
        LoginInfo info = loginDatabase.get(id);
        return info != null && info.isFirstLogin;
    }

    public void updatePassword(String id, String newPassword) {
        if (id == null || newPassword == null || newPassword.trim().isEmpty()) {
            System.out.println("Invalid id or password for update");
            return;
        }

        LoginInfo info = loginDatabase.get(id);
        if (info != null) {
            System.out.println("Updating password for user: " + id);
            info.passwordHash = PasswordUtil.hashPassword(newPassword);
            info.isFirstLogin = false;
            saveLoginData();
            System.out.println("Password updated successfully");
        } else {
            System.out.println("No login info found for ID: " + id);
        }
    }

    private void saveLoginData() {
        System.out.println("Saving login data...");
        CSVWriterUtil.writeCSV(LOGIN_FILE, writer -> {
            try {
                writer.write("ID,Role,PasswordHash,FirstLogin\n");
                for (LoginInfo info : loginDatabase.values()) {
                    writer.write(String.format("%s,%s,%s,%b\n",
                        info.id, info.role, info.passwordHash, info.isFirstLogin));
                }
                System.out.println("Login data saved successfully");
            } catch (Exception e) {
                System.err.println("Error saving login data: " + e.getMessage());
            }
        });
    }

    public void addNewUser(String id, String role) {
        if (id == null || role == null) {
            System.out.println("Cannot add user: null id or role");
            return;
        }

        if (!loginDatabase.containsKey(id)) {
            String defaultPasswordHash = PasswordUtil.hashPassword("password");
            loginDatabase.put(id, new LoginInfo(id, role, defaultPasswordHash, true));
            saveLoginData();
            System.out.println("Added new user to login database: " + id);
        } else {
            System.out.println("User already exists in login database: " + id);
        }
    }
}