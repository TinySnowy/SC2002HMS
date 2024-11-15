package user_management;

public class Main {
    public static void main(String[] args) {
        UserController userController = new UserController();

        // Add a new patient
        Patient patient1 = new Patient("P001", "password", "John Doe", "1234567890", "john.doe@example.com");
        Patient patient2 = new Patient("P002", "password", "Jane Smith", "9876543210", "jane.smith@example.com");
        Patient patient3 = new Patient("P003", "password", "Emily Davis", "5432167890", "emily.davis@example.com");
        Patient patient4 = new Patient("P004", "password", "Michael Brown", "9873216540", "michael.brown@example.com");
        Patient patient5 = new Patient("P005", "password", "Sarah Wilson", "1122334455", "sarah.wilson@example.com");

        userController.addUser(patient1);
        userController.addUser(patient2);
        userController.addUser(patient3);
        userController.addUser(patient4);
        userController.addUser(patient5);


        // Add a new doctor
        Doctor doctor1 = new Doctor("D001", "password", "Orthopedics", "Dr. Alice Green");
        Doctor doctor2 = new Doctor("D002", "password", "Pediatrics", "Dr. Bob White");
        Pharmacist pharmacist1 = new Pharmacist("P001", "password", "License12345", "Pharmacist Anna Taylor");
        Pharmacist pharmacist2 = new Pharmacist("P002", "password", "License67890", "Pharmacist Ryan Martin");
        Administrator admin1 = new Administrator("A001", "password", "Admin Kevin Lee");
        Administrator admin2 = new Administrator("A002", "password", "Admin Laura King");

        userController.addUser(doctor1);
        userController.addUser(doctor2);
        userController.addUser(pharmacist1);
        userController.addUser(pharmacist2);
        userController.addUser(admin1);
        userController.addUser(admin2);

        // Persist data
        userController.persistData();

        // Reload data
        UserController reloadedController = new UserController();
        System.out.println("Reloaded Users:");
        for (String userId : reloadedController.getUserDatabase().keySet()) {
            User user = reloadedController.getUserById(userId);
            System.out.println(user.getId() + ": " + user.getName() + " (" + user.getRole() + ")");
        }
    }
}
