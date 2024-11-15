package user_management;

import utils.CSVReaderUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserController {
    private Map<String, User> userDatabase;

    public UserController() {
        userDatabase = new HashMap<>();
        loadPatientData();
        loadStaffData();
    }

    // Getter for userDatabase
    public Map<String, User> getUserDatabase() {
        return userDatabase;
    }

    // Load Patient Data from Patient_List.csv
    private void loadPatientData() {
        List<String[]> records = CSVReaderUtil.readCSV("data/Patient_List.csv");

        boolean isFirstRow = true; // Skip header row
        for (String[] record : records) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            String id = record[0];
            String name = record[1];
            String contactInfo = record[2];
            String email = record[3];

            Patient patient = new Patient(id, "password", name, contactInfo, email);
            addUser(patient);

            System.out.println("Loaded patient: ID=" + patient.getId() + ", Name=" + patient.getName());
        }
    }

    // Load Staff Data from Staff_List.csv
    private void loadStaffData() {
        List<String[]> records = CSVReaderUtil.readCSV("data/Staff_List.csv");

        boolean isFirstRow = true; // Skip header row
        for (String[] record : records) {
            try {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }

                // Validate row length
                if (record.length < 4) {
                    System.err.println("Skipping malformed row: " + String.join(",", record));
                    continue;
                }

                String id = record[0];
                String name = record[1];
                String role = record[2];
                String field1 = record[3]; // Specialty/License
                String field2 = (record.length > 4) ? record[4] : ""; // Optional additional field

                User user = switch (role) {
                    case "Doctor" -> new Doctor(id, "password", field1, name);
                    case "Pharmacist" -> new Pharmacist(id, "password", field1, name);
                    case "Administrator" -> new Administrator(id, "password", name);
                    default -> {
                        System.err.println("Unknown or misplaced role in staff data: " + role);
                        yield null;
                    }
                };

                if (user != null) {
                    addUser(user);
                    System.out.println("Loaded staff: ID=" + id + ", Name=" + name + ", Role=" + role);
                }
            } catch (Exception e) {
                System.err.println("Error processing row: " + String.join(",", record) + " - " + e.getMessage());
            }
        }
    }


    // Add user
    public void addUser(User user) {
        userDatabase.put(user.getId(), user);
    }

    // Retrieve user by ID
    public User getUserById(String id) {
        return userDatabase.get(id);
    }

    // Persist Data to Separate Files
    public void persistData() {
        persistPatientData();
        persistStaffData();
    }

    private void persistPatientData() {
        try (FileWriter writer = new FileWriter("data/Patient_List.csv")) {
            // Write header
            writer.write("ID,Name,Contact Info,Email\n");

            for (User user : userDatabase.values()) {
                if (user instanceof Patient patient) {
                    writer.write(patient.getId() + "," +
                            patient.getName() + "," +
                            patient.getContactInfo() + "," +
                            patient.getEmail() + "\n");
                }
            }

            System.out.println("Patient data persisted successfully!");
        } catch (IOException e) {
            System.err.println("Error persisting patient data: " + e.getMessage());
        }
    }

    private void persistStaffData() {
        try (FileWriter writer = new FileWriter("data/Staff_List.csv")) {
            // Write header
            writer.write("ID,Name,Role,Specialty/License\n");

            for (User user : userDatabase.values()) {
                if (user instanceof Doctor doctor) {
                    writer.write(doctor.getId() + "," +
                            doctor.getName() + "," +
                            "Doctor," +
                            doctor.getSpecialty() + "\n");
                } else if (user instanceof Pharmacist pharmacist) {
                    writer.write(pharmacist.getId() + "," +
                            pharmacist.getName() + "," +
                            "Pharmacist," +
                            pharmacist.getLicenseNumber() + "\n");
                } else if (user instanceof Administrator admin) {
                    writer.write(admin.getId() + "," +
                            admin.getName() + "," +
                            "Administrator,\n");
                }
            }

            System.out.println("Staff data persisted successfully!");
        } catch (IOException e) {
            System.err.println("Error persisting staff data: " + e.getMessage());
        }
    }
}
