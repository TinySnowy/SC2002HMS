package user_management;

import utils.CSVReaderUtil;
import utils.CSVWriterUtil;
import utils.PasswordUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserController {
    private final Map<String, User> userDatabase;
    private static final String STAFF_FILE = "SC2002HMS/data/Staff_List.csv";
    private static final String PATIENT_FILE = "SC2002HMS/data/Patient_List.csv";

    public UserController() {
        userDatabase = new HashMap<>();
        loadPatientData();
        loadStaffData();
    }

    private void loadPatientData() {
        List<String[]> records = CSVReaderUtil.readCSV(PATIENT_FILE);
        boolean isFirstRow = true;

        for (String[] record : records) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            try {
                String id = record[0];
                String name = record[1];
                String contactInfo = record[2];
                String email = record[3];

                // Set default password as "password"
                String defaultPassword = PasswordUtil.hashPassword("password");
                Patient patient = new Patient(id, name, defaultPassword, contactInfo, email, true);
                addUser(patient);
            } catch (Exception e) {
                System.err.println("Error parsing patient row: " + String.join(",", record));
            }
        }
    }

    private void loadStaffData() {
        List<String[]> records = CSVReaderUtil.readCSV(STAFF_FILE);
        boolean isFirstRow = true;

        for (String[] record : records) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            try {
                String id = record[0];
                String specialty_license = record[1];
                String role = record[2];
                String name = record[3];

                // Set default password as "password"
                String defaultPassword = PasswordUtil.hashPassword("password");

                User user = null;
                switch (role) {
                    case "Doctor":
                        user = new Doctor(id, name, defaultPassword, specialty_license, true);
                        break;
                    case "Pharmacist":
                        user = new Pharmacist(id, name, defaultPassword, specialty_license, true);
                        break;
                }

                if (user != null) {
                    addUser(user);
                }
            } catch (Exception e) {
                System.err.println("Error parsing staff row: " + String.join(",", record));
            }
        }
    }

    public void addUser(User user) {
        if (userDatabase.containsKey(user.getId())) {
            System.err.println("Duplicate user ID detected: " + user.getId());
        } else {
            userDatabase.put(user.getId(), user);
        }
    }

    public User getUserById(String id) {
        return userDatabase.get(id);
    }

    public void persistPatientData() {
        CSVWriterUtil.writeCSV(PATIENT_FILE, writer -> {
            writer.write("ID,Name,Contact Info,Email\n");
            for (User user : userDatabase.values()) {
                if (user instanceof Patient) {
                    Patient patient = (Patient) user;
                    writer.write(String.format("%s,%s,%s,%s\n",
                            patient.getId(),
                            patient.getName(),
                            patient.getContactInfo(),
                            patient.getEmail()));
                }
            }
        });
    }

    public void persistStaffData() {
        CSVWriterUtil.writeCSV(STAFF_FILE, writer -> {
            writer.write("ID,Name,Role,Specialty/License\n");
            for (User user : userDatabase.values()) {
                if (user instanceof Doctor) {
                    Doctor doctor = (Doctor) user;
                    writer.write(String.format("%s,%s,Doctor,%s\n",
                            doctor.getId(),
                            doctor.getSpecialty(),
                            doctor.getName()));
                } else if (user instanceof Pharmacist) {
                    Pharmacist pharmacist = (Pharmacist) user;
                    writer.write(String.format("%s,%s,Pharmacist,%s\n",
                            pharmacist.getId(),
                            pharmacist.getName(),
                            pharmacist.getLicenseNumber()));
                }
            }
        });
    }

    public void persistAllData() {
        persistPatientData();
        persistStaffData();
    }
}