package user_management;

import utils.CSVReaderUtil;
import utils.CSVWriterUtil;
import utils.PasswordUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class UserController {
    private static UserController instance;
    private final Map<String, User> userDatabase;
    private static final String STAFF_FILE = "SC2002HMS/data/Staff_List.csv";
    private static final String PATIENT_FILE = "SC2002HMS/data/Patient_List.csv";

    public UserController() {
        userDatabase = new HashMap<>();
        loadPatientData();
        loadStaffData();
    }

    public static UserController getInstance() {
        if (instance == null) {
            instance = new UserController();
        }
        return instance;
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
                if (record.length < 4) {
                    System.err.println("Invalid patient record: " + String.join(",", record));
                    continue;
                }

                String id = record[0];
                String name = record[1];
                String contactInfo = record[2];
                String email = record[3];
                String password = record.length > 4 ? record[4] : PasswordUtil.hashPassword("password");
                boolean isFirstLogin = record.length > 5 ? Boolean.parseBoolean(record[5]) : true;

                Patient patient = new Patient(id, name, password, contactInfo, email, isFirstLogin);
                addUser(patient);
            } catch (Exception e) {
                System.err.println("Error loading patient data: " + e.getMessage());
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
                if (record.length < 6) {
                    System.err.println("Invalid staff record: " + String.join(",", record));
                    continue;
                }

                String id = record[0];
                String name = record[1];
                String role = record[2];
                String specialtyOrLicense = record[3];
                String gender = record[4];
                int age = Integer.parseInt(record[5]);
                String passwordHash = PasswordUtil.hashPassword("password");

                User user = createStaffUser(id, name, role, specialtyOrLicense, gender, age, passwordHash, true);
                if (user != null) {
                    addUser(user);
                }
            } catch (Exception e) {
                System.err.println("Error loading staff data: " + e.getMessage());
            }
        }
    }

    private User createStaffUser(String id, String name, String role, String specialtyOrLicense,
            String gender, int age, String passwordHash, boolean isFirstLogin) {
        switch (role.toLowerCase()) {
            case "doctor":
                return new Doctor(id, name, passwordHash, specialtyOrLicense, gender, age, isFirstLogin);
            case "pharmacist":
                return new Pharmacist(id, name, passwordHash, specialtyOrLicense, gender, age, isFirstLogin);
            case "administrator":
                return new Administrator(id, name, passwordHash, gender, age, isFirstLogin);
            default:
                System.err.println("Invalid role: " + role);
                return null;
        }
    }

    public void addUser(User user) {
        if (userDatabase.containsKey(user.getId())) {
            throw new IllegalArgumentException("User ID already exists: " + user.getId());
        }
        userDatabase.put(user.getId(), user);
    }

    public void removeUser(String userId) {
        if (!userDatabase.containsKey(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        userDatabase.remove(userId);
    }

    public void updateUser(User user) {
        if (!userDatabase.containsKey(user.getId())) {
            throw new IllegalArgumentException("User not found: " + user.getId());
        }
        userDatabase.put(user.getId(), user);
    }

    public User getUserById(String id) {
        return userDatabase.get(id);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userDatabase.values());
    }

    public List<User> getUsersByRole(String role) {
        return userDatabase.values().stream()
                .filter(user -> user.getRole().equalsIgnoreCase(role))
                .collect(java.util.stream.Collectors.toList());
    }

    public void persistPatientData() {
        CSVWriterUtil.writeCSV(PATIENT_FILE, writer -> {
            writer.write("ID,Name,Contact Info,Email,PasswordHash,FirstLogin\n");
            for (User user : userDatabase.values()) {
                if (user instanceof Patient) {
                    Patient patient = (Patient) user;
                    writer.write(String.format("%s,%s,%s,%s,%s,%b\n",
                            patient.getId(),
                            patient.getName(),
                            patient.getContactInfo(),
                            patient.getEmail(),
                            patient.getPasswordHash(),
                            patient.isFirstLogin()));
                }
            }
        });
    }

    private String createStaffRecord(User user) {
        try {
            if (user instanceof Doctor) {
                Doctor doctor = (Doctor) user;
                return String.format("%s,%s,Doctor,%s,%s,%d\n",
                        doctor.getId(),
                        doctor.getName(),
                        doctor.getSpecialty(),
                        doctor.getGender(),
                        doctor.getAge());
            } else if (user instanceof Pharmacist) {
                Pharmacist pharmacist = (Pharmacist) user;
                return String.format("%s,%s,Pharmacist,%s,%s,%d\n",
                        pharmacist.getId(),
                        pharmacist.getName(),
                        pharmacist.getLicenseNumber(),
                        pharmacist.getGender(),
                        pharmacist.getAge());
            } else if (user instanceof Administrator) {
                Administrator admin = (Administrator) user;
                return String.format("%s,%s,Administrator,,%s,%d\n",
                        admin.getId(),
                        admin.getName(),
                        admin.getGender(),
                        admin.getAge());
            }
        } catch (Exception e) {
            System.err.println("Error creating staff record for " + user.getId() + ": " + e.getMessage());
        }
        return null;
    }

    public void persistStaffData() {
        try {
            CSVWriterUtil.writeCSV(STAFF_FILE, writer -> {
                // Write header
                writer.write("ID,Name,Role,Specialty/License,Gender,Age\n");

                // Write staff records
                for (User user : userDatabase.values()) {
                    if (!(user instanceof Patient)) {
                        String record = createStaffRecord(user);
                        if (record != null) {
                            writer.write(record);
                        }
                    }
                }
            });
            System.out.println("Data successfully written to " + STAFF_FILE);
        } catch (Exception e) {
            System.err.println("Error persisting staff data: " + e.getMessage());
        }
    }

    public void persistAllData() {
        persistPatientData();
        persistStaffData();
    }

    // Helper method to create a new staff member
    public User createNewStaff(String id, String name, String role, String password,
            String specialtyOrLicense, String gender, int age) {
        String hashedPassword = PasswordUtil.hashPassword(password);
        return createStaffUser(id, name, role, specialtyOrLicense, gender, age, hashedPassword, true);
    }
}
