package admin_management;

import user_management.*;
import java.util.*;

public class StaffManager {
    private final UserController userController;

    public StaffManager(UserController userController) {
        this.userController = userController;
    }

    public List<User> getStaffByRole(String role) {
        return userController.getUsersByRole(role);
    }

    public List<User> getAllStaff() {
        return userController.getAllUsers().stream()
                .filter(user -> !(user instanceof Patient))
                .collect(java.util.stream.Collectors.toList());
    }

    public void addStaff(User user) {
        if (user instanceof Patient) {
            throw new IllegalArgumentException("Cannot add patients as staff members");
        }
        userController.addUser(user);
        userController.persistAllData(); // Persist after adding
        System.out.println("Staff member added successfully.");
    }

    public void removeStaff(String userId) {
        User user = userController.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("Staff member not found: " + userId);
        }
        if (user instanceof Patient) {
            throw new IllegalArgumentException("Cannot remove patients from staff management");
        }
        userController.removeUser(userId);
        userController.persistAllData(); // Persist after removing
        System.out.println("Staff member removed successfully.");
    }

    public void updateStaff(String staffId, User updatedUser) {
        if (!staffId.equals(updatedUser.getId())) {
            throw new IllegalArgumentException("Staff ID cannot be changed");
        }
        if (updatedUser instanceof Patient) {
            throw new IllegalArgumentException("Cannot update to patient type");
        }
        userController.updateUser(updatedUser);
        userController.persistAllData(); // Persist after updating
        System.out.println("Staff member updated successfully.");
    }

    // Helper method to create new staff with auto-generated ID
    public User createStaffWithId(String name, String role, String password, String specialtyOrLicense, String gender,
            int age) {
        String prefix;
        switch (role.toLowerCase()) {
            case "doctor":
                prefix = "D";
                break;
            case "pharmacist":
                prefix = "P";
                break;
            case "administrator":
                prefix = "A";
                break;
            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }

        // Get the next available ID
        int maxId = 0;
        List<User> existingStaff = getAllStaff();
        for (User staff : existingStaff) {
            String id = staff.getId();
            if (id.startsWith(prefix)) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    maxId = Math.max(maxId, num);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        String newId = String.format("%s%03d", prefix, maxId + 1);
        User newStaff = userController.createNewStaff(newId, name, role, password, specialtyOrLicense, gender, age);
        addStaff(newStaff); // This will also persist the data
        return newStaff;
    }

    // Validate staff data
    public void validateStaffData(String name, String gender, int age, String specialtyOrLicense) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (gender == null || (!gender.equalsIgnoreCase("M") && !gender.equalsIgnoreCase("F"))) {
            throw new IllegalArgumentException("Gender must be M or F");
        }
        if (age < 18 || age > 100) {
            throw new IllegalArgumentException("Age must be between 18 and 100");
        }
        if (specialtyOrLicense != null && specialtyOrLicense.trim().isEmpty()) {
            throw new IllegalArgumentException("Specialty/License cannot be empty when required");
        }
    }
}