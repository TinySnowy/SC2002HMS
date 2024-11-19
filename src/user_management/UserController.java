package user_management;

import utils.CSVReaderUtil;
import utils.CSVWriterUtil;
import utils.PasswordUtil;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Central controller managing all user operations in the HMS.
 * Handles:
 * - User data persistence
 * - Staff and patient management
 * - Authentication services
 * - Data validation
 * - CSV operations
 * Provides singleton access to user management functionality.
 */
public class UserController {
    /** Singleton instance of the controller */
    private static UserController instance;
    
    /** Central storage for all system users */
    private final Map<String, User> userDatabase;
    
    /** File path for staff data storage */
    private static final String STAFF_FILE = "SC2002HMS/data/Staff_List.csv";
    
    /** File path for patient data storage */
    private static final String PATIENT_FILE = "SC2002HMS/data/Patient_List.csv";
    
    /** Date format for patient records */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Private constructor for singleton pattern.
     * Initializes database and loads existing user data.
     */
    private UserController() {
        userDatabase = new HashMap<>();
        loadPatientData();
        loadStaffData();
    }

    /**
     * Provides singleton access to controller.
     * Creates instance if not exists.
     * 
     * @return Singleton controller instance
     */
    public static UserController getInstance() {
        if (instance == null) {
            instance = new UserController();
        }
        return instance;
    }

    /**
     * Loads patient data from CSV storage.
     * Processes:
     * - Personal information
     * - Medical details
     * - Authentication data
     * - Login status
     * Handles file reading and parsing errors.
     */
    private void loadPatientData() {
        List<String[]> records = CSVReaderUtil.readCSV(PATIENT_FILE);
        boolean isFirstRow = true;

        for (String[] record : records) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            try {
                if (record.length < 8) {
                    System.err.println("Invalid patient record: " + String.join(",", record));
                    continue;
                }

                String id = record[0].trim();
                String name = record[1].trim();
                String password = record[2].trim();
                LocalDate dob = LocalDate.parse(record[3].trim(), DATE_FORMATTER);
                String gender = record[4].trim();
                String bloodGroup = record[5].trim();
                String contactInfo = record[6].trim();
                String email = record[7].trim();
                boolean isFirstLogin = record.length > 8 ? Boolean.parseBoolean(record[8].trim()) : true;

                Patient patient = new Patient(id, name, password, dob, gender, bloodGroup, 
                                           contactInfo, email);
                patient.setFirstLogin(isFirstLogin);
                addUser(patient);
                
                System.out.println("Loaded patient: " + id);
            } catch (Exception e) {
                System.err.println("Error loading patient data: " + e.getMessage());
            }
        }
        System.out.println("Total patients loaded: " + 
                          userDatabase.values().stream().filter(u -> u instanceof Patient).count());
    }

    /**
     * Loads staff data from CSV storage.
     * Processes:
     * - Staff credentials
     * - Role assignments
     * - Department details
     * - Professional information
     * Handles file reading and parsing errors.
     */
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

                String id = record[0].trim();
                String name = record[1].trim();
                String role = record[2].trim();
                String specialtyOrLicense = record[3].trim();
                String gender = record[4].trim();
                int age = Integer.parseInt(record[5].trim());
                String passwordHash = PasswordUtil.hashPassword("password");

                User user = createStaffUser(id, name, role, specialtyOrLicense, gender, age, 
                                          passwordHash, true);
                if (user != null) {
                    addUser(user);
                    System.out.println("Loaded staff member: " + id);
                }
            } catch (Exception e) {
                System.err.println("Error loading staff data: " + e.getMessage());
            }
        }
        System.out.println("Total staff loaded: " + 
                          userDatabase.values().stream().filter(u -> !(u instanceof Patient)).count());
    }

    /**
     * Creates appropriate staff user based on role.
     * Supports:
     * - Doctors
     * - Pharmacists
     * - Administrators
     * 
     * @param id Staff identifier
     * @param name Staff name
     * @param role System role
     * @param specialtyOrLicense Professional credentials
     * @param gender Staff gender
     * @param age Staff age
     * @param passwordHash Authentication hash
     * @param isFirstLogin Login status
     * @return Created staff user object
     */
    private User createStaffUser(String id, String name, String role, String specialtyOrLicense,
            String gender, int age, String passwordHash, boolean isFirstLogin) {
        return switch (role.toLowerCase()) {
            case "doctor" -> new Doctor(id, name, passwordHash, specialtyOrLicense, gender, age, isFirstLogin);
            case "pharmacist" -> new Pharmacist(id, name, passwordHash, specialtyOrLicense, gender, age, isFirstLogin);
            case "administrator" -> new Administrator(id, name, passwordHash, gender, age, isFirstLogin);
            default -> {
                System.err.println("Invalid role: " + role);
                yield null;
            }
        };
    }

    /**
     * Adds new user to system.
     * Validates:
     * - Unique ID
     * - Required fields
     * - Data consistency
     * 
     * @param user New user to add
     * @throws IllegalArgumentException if validation fails
     */
    public void addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (userDatabase.containsKey(user.getId())) {
            throw new IllegalArgumentException("User ID already exists: " + user.getId());
        }
        userDatabase.put(user.getId(), user);
    }

    /**
     * Removes user from system.
     * Updates:
     * - User database
     * - Persistent storage
     * 
     * @param userId ID of user to remove
     * @throws IllegalArgumentException if user not found
     */
    public void removeUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (!userDatabase.containsKey(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        userDatabase.remove(userId);
        persistAllData(); // Save changes immediately
    }

    /**
     * Updates existing user information.
     * Maintains:
     * - Data consistency
     * - Storage synchronization
     * 
     * @param user Updated user data
     * @throws IllegalArgumentException if user not found
     */
    public void updateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (!userDatabase.containsKey(user.getId())) {
            throw new IllegalArgumentException("User not found: " + user.getId());
        }
        userDatabase.put(user.getId(), user);
        persistAllData(); // Save changes immediately
    }

    /**
     * Retrieves user by ID.
     * 
     * @param id User identifier
     * @return User if found, null otherwise
     */
    public User getUserById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return userDatabase.get(id);
    }

    /**
     * Retrieves all system users.
     * 
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(userDatabase.values());
    }

    /**
     * Retrieves all doctors in system.
     * 
     * @return List of all doctors
     */
    public List<Doctor> getAllDoctors() {
        return userDatabase.values().stream()
                .filter(user -> user instanceof Doctor)
                .map(user -> (Doctor) user)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves users by role.
     * 
     * @param role Target role
     * @return List of matching users
     */
    public List<User> getUsersByRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return userDatabase.values().stream()
                .filter(user -> user.getUserType().equalsIgnoreCase(role))
                .collect(Collectors.toList());
    }

    /**
     * Searches users by name.
     * 
     * @param name Search term
     * @return List of matching users
     */
    public List<User> searchUsersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String searchTerm = name.toLowerCase().trim();
        return userDatabase.values().stream()
                .filter(user -> user.getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Searches doctors by specialty.
     * 
     * @param specialty Medical specialty
     * @return List of matching doctors
     */
    public List<Doctor> searchDoctorsBySpecialty(String specialty) {
        if (specialty == null || specialty.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String searchTerm = specialty.toLowerCase().trim();
        return getAllDoctors().stream()
                .filter(doctor -> doctor.getSpecialty().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Saves patient data to CSV.
     * Handles:
     * - Data formatting
     * - File writing
     * - Error recovery
     */
    public void persistPatientData() {
        CSVWriterUtil.writeCSV(PATIENT_FILE, writer -> {
            writer.write("ID,Name,Password,DateOfBirth,Gender,BloodGroup,ContactInfo,Email,FirstLogin\n");
            for (User user : userDatabase.values()) {
                if (user instanceof Patient patient) {
                    String record = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%b\n",
                            patient.getId(),
                            patient.getName(),
                            patient.getPasswordHash(),
                            patient.getDateOfBirth().format(DATE_FORMATTER),
                            patient.getGender(),
                            patient.getBloodGroup(),
                            patient.getContactInfo(),
                            patient.getEmail(),
                            patient.isFirstLogin());
                    writer.write(record);
                }
            }
        });
    }

    /**
     * Creates staff record for CSV storage.
     * 
     * @param user Staff member
     * @return Formatted CSV record
     */
    private String createStaffRecord(User user) {
        try {
            if (user instanceof Doctor doctor) {
                return String.format("%s,%s,Doctor,%s,%s,%d\n",
                        doctor.getId(),
                        doctor.getName(),
                        doctor.getSpecialty(),
                        doctor.getGender(),
                        doctor.getAge());
            } else if (user instanceof Pharmacist pharmacist) {
                return String.format("%s,%s,Pharmacist,%s,%s,%d\n",
                        pharmacist.getId(),
                        pharmacist.getName(),
                        pharmacist.getLicenseNumber(),
                        pharmacist.getGender(),
                        pharmacist.getAge());
            } else if (user instanceof Administrator admin) {
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

    /**
     * Saves staff data to CSV.
     * Handles:
     * - Data formatting
     * - File writing
     * - Error recovery
     */
    public void persistStaffData() {
        try {
            CSVWriterUtil.writeCSV(STAFF_FILE, writer -> {
                writer.write("ID,Name,Role,Specialty/License,Gender,Age\n");
                for (User user : userDatabase.values()) {
                    if (!(user instanceof Patient)) {
                        String record = createStaffRecord(user);
                        if (record != null) {
                            writer.write(record);
                        }
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("Error persisting staff data: " + e.getMessage());
        }
    }

    /**
     * Saves all user data to storage.
     * Updates both staff and patient records.
     */
    public void persistAllData() {
        try {
            persistPatientData();
            persistStaffData();
            System.out.println("Data updated successfully");
        } catch (Exception e) {
            System.err.println("Error updating data: " + e.getMessage());
        }
    }

    /**
     * Creates new staff user in system.
     * 
     * @param id Staff identifier
     * @param name Staff name
     * @param role System role
     * @param password Initial password
     * @param specialtyOrLicense Professional credentials
     * @param gender Staff gender
     * @param age Staff age
     * @return Created staff user
     */
    public User createNewStaff(String id, String name, String role, String password,
            String specialtyOrLicense, String gender, int age) {
        if (id == null || name == null || role == null || password == null) {
            throw new IllegalArgumentException("Required fields cannot be null");
        }
        String hashedPassword = PasswordUtil.hashPassword(password);
        User newUser = createStaffUser(id, name, role, specialtyOrLicense, gender, age, 
                                     hashedPassword, true);
        if (newUser != null) {
            addUser(newUser);
            persistStaffData();
        }
        return newUser;
    }

    /**
     * Creates new patient in system.
     * 
     * @param id Patient identifier
     * @param name Patient name
     * @param password Initial password
     * @param dob Date of birth
     * @param gender Patient gender
     * @param bloodGroup Patient blood group
     * @param contactInfo Patient contact information
     * @param email Patient email
     * @return Created patient
     */
    public Patient createNewPatient(String id, String name, String password, LocalDate dob,
            String gender, String bloodGroup, String contactInfo, String email) {
        if (id == null || name == null || password == null || dob == null) {
            throw new IllegalArgumentException("Required fields cannot be null");
        }
        String hashedPassword = PasswordUtil.hashPassword(password);
        Patient newPatient = new Patient(id, name, hashedPassword, dob, gender, bloodGroup, 
                                       contactInfo, email);
        addUser(newPatient);
        persistPatientData();
        return newPatient;
    }
}