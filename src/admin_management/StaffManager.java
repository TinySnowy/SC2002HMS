package admin_management;

import user_management.*;
import utils.CSVReaderUtil;
import utils.CSVWriterUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class StaffManager {
    private final List<User> staff;
    private final UserController userController;
    private static final String STAFF_FILE_PATH = "SC2002HMS/data/Staff_List.csv";

    public StaffManager(UserController userController) {
        this.staff = new ArrayList<>();
        this.userController = userController;
        loadStaffFromCSV();
    }

    private void loadStaffFromCSV() {
        List<String[]> records = CSVReaderUtil.readCSV(STAFF_FILE_PATH);
        boolean isFirstRow = true; // Skip the header row

        for (String[] record : records) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            try {
                if (record.length < 5) {
                    System.err.println("Skipping malformed row in Staff_List.csv: " + String.join(",", record));
                    continue;
                }

                String id = record[0];
                String name = record[1];
                String role = record[2];
                String rawPassword = record[3];
                boolean isFirstLogin = Boolean.parseBoolean(record[4]);

                User user = createStaffMember(id, name, role, rawPassword, isFirstLogin);
                if (user != null) {
                    addStaff(user);
                }
            } catch (Exception e) {
                System.err.println("Error parsing staff row: " + e.getMessage());
            }
        }
    }

    private User createStaffMember(String id, String name, String role, String rawPassword, boolean isFirstLogin) {
        switch (role) {
            case "Doctor":
                return new Doctor(id, name, rawPassword, "General Medicine", isFirstLogin);
            case "Pharmacist":
                return new Pharmacist(id, name, rawPassword, "License not set", isFirstLogin);
            case "Administrator":
                return new Administrator(id, name, rawPassword, isFirstLogin);
            default:
                System.err.println("Invalid role in staff CSV: " + role + ". Skipping entry.");
                return null;
        }
    }

    public void addStaff(User user) {
        if (!(user instanceof Patient)) { // Ensure we don't add patients as staff
            staff.add(user);
            userController.addUser(user); // Add to the main user database
            System.out.println("Staff member added: " + user.getId() + ", Role: " + user.getRole());
            saveStaffToCSV();
        }
    }

    public void removeStaff(String userId) {
        boolean removed = staff.removeIf(user -> user.getId().equals(userId));
        if (removed) {
            System.out.println("Staff member removed: " + userId);
            saveStaffToCSV();
        } else {
            System.out.println("Staff member not found: " + userId);
        }
    }

    public void listAllStaff() {
        if (staff.isEmpty()) {
            System.out.println("No staff members found.");
            return;
        }

        System.out.println("\nCurrent Staff Members:");
        System.out.println("----------------------------------------");
        System.out.printf("%-10s %-20s %-15s%n", "ID", "Name", "Role");
        System.out.println("----------------------------------------");

        staff.forEach(user -> System.out.printf("%-10s %-20s %-15s%n",
                user.getId(),
                user.getName(),
                user.getRole()));
        System.out.println("----------------------------------------");
    }

    public void filterStaff(String criteria) {
        Scanner scanner = new Scanner(System.in);
        List<User> filteredStaff;

        switch (criteria.toLowerCase()) {
            case "role":
                System.out.print("Enter role to filter by (Doctor, Pharmacist, Administrator): ");
                String role = scanner.nextLine();
                filteredStaff = staff.stream()
                        .filter(user -> user.getRole().equalsIgnoreCase(role))
                        .collect(Collectors.toList());

                if (filteredStaff.isEmpty()) {
                    System.out.println("No staff members found with role: " + role);
                    return;
                }

                System.out.println("\nFiltered Staff Members (Role: " + role + ")");
                System.out.println("----------------------------------------");
                System.out.printf("%-10s %-20s %-15s%n", "ID", "Name", "Role");
                System.out.println("----------------------------------------");

                filteredStaff.forEach(user -> System.out.printf("%-10s %-20s %-15s%n",
                        user.getId(),
                        user.getName(),
                        user.getRole()));
                System.out.println("----------------------------------------");
                break;

            default:
                System.out.println("Invalid criteria. Please enter 'role'.");
        }
    }

    private void saveStaffToCSV() {
        CSVWriterUtil.writeCSV(STAFF_FILE_PATH, writer -> {
            writer.write("ID,Name,Role,PasswordHash,FirstLogin\n");
            for (User user : staff) {
                writer.write(String.format("%s,%s,%s,%s,%b\n",
                        user.getId(),
                        user.getName(),
                        user.getRole(),
                        user.getPasswordHash(),
                        user.isFirstLogin()));
            }
        });
    }
}