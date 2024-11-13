package admin_management;

import user_management.*;
import utils.CSVReaderUtil;
import java.util.ArrayList;
import java.util.List;

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
            String gender = record[3];
            int age = Integer.parseInt(record[4]);

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
                    user = new Patient(id, "password", name, "contact@example.com");
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
}
