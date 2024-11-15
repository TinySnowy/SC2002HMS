package admin_management;

import user_management.*;
import utils.CSVReaderUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class StaffManager {
    private List<User> staff;

    public StaffManager() {
        this.staff = new ArrayList<>();
        loadStaffFromCSV();
    }

    private void loadStaffFromCSV() {
        List<String[]> records = CSVReaderUtil.readCSV("data/Staff_List.csv");

        boolean isFirstRow = true; // Skip the header row
        for (String[] record : records) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            String id = record[0];
            String name = record[1];
            String role = record[2];

            User user;
            switch (role) {
                case "Doctor":
                    user = new Doctor(id, "password", "General Medicine", name);
                    break;
                case "Pharmacist":
                    user = new Pharmacist(id, "password", "License123", name);
                    break;
                case "Administrator":
                    user = new Administrator(id, "password", name);
                    break;
                default:
                    System.out.println("Invalid role in staff CSV: " + role + ". Skipping entry.");
                    continue; // Skip invalid entries like patients
            }

            addStaff(user);
        }
    }

    public void addStaff(User user) {
        staff.add(user);
        System.out.println("Staff member added: " + user.getId() + ", Role: " + user.getRole());
    }

    public void removeStaff(String userId) {
        staff.removeIf(user -> user.getId().equals(userId));
        System.out.println("Staff member removed: " + userId);
    }

    public void listAllStaff() {
        System.out.println("Current Staff Members:");
        for (User user : staff) {
            System.out.println("ID: " + user.getId() + ", Name: " + user.getName() + ", Role: " + user.getRole());
        }
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
                break;

            default:
                System.out.println("Invalid criteria. Please enter 'role'.");
                return;
        }

        System.out.println("Filtered Staff Members:");
        for (User user : filteredStaff) {
            System.out.println("ID: " + user.getId() + ", Name: " + user.getName() + ", Role: " + user.getRole());
        }
    }
}
