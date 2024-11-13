package user_management;

import utils.CSVReaderUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserController {
    private Map<String, User> userDatabase;

    public UserController() {
        userDatabase = new HashMap<>();
        loadPatientData();
    }

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
            String dateOfBirth = record[2];
            String gender = record[3];
            String bloodType = record[4];
            String contactInfo = record[5];

            // Creating a Patient object with default password "password"
            Patient patient = new Patient(id, "password", name, contactInfo);
            addUser(patient);

            // Debug statement to confirm loading
            System.out.println("Loaded patient: ID=" + patient.getId() + ", Name=" + patient.getName() + ", Password=default");
        }
    }

    public void addUser(User user) {
        userDatabase.put(user.getId(), user);
    }

    public User getUserById(String id) {
        return userDatabase.get(id);
    }
}
