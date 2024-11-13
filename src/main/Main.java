package main;

import login_system.LoginPage;
import login_system.RoleSelector;
import appointment_management.Appointment;
import appointment_management.AppointmentList;
import user_management.User;
import user_management.Doctor;
import user_management.Patient;
import user_management.UserController;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        // Initialize UserController and AppointmentList
        UserController userController = new UserController();
        AppointmentList appointmentList = new AppointmentList();

        // Create a test doctor and add to userController
        Doctor testDoctor = new Doctor("D001", "password", "John Smith", "Cardiology");
        userController.addUser(testDoctor);

        // Create a test patient and add to userController
        Patient testPatient = new Patient("P001", "password", "Alice Brown", "alice.brown@example.com");
        userController.addUser(testPatient);

        // Add a few test appointments to the appointment list
        appointmentList.addAppointment(new Appointment("A001", testPatient, testDoctor, LocalDateTime.of(2024, 12, 1, 10, 30)));
        appointmentList.addAppointment(new Appointment("A002", testPatient, testDoctor, LocalDateTime.of(2024, 12, 2, 11, 00)));

        // Create RoleSelector and LoginPage with dependencies
        RoleSelector roleSelector = new RoleSelector(appointmentList); // Pass appointmentList here
        LoginPage loginPage = new LoginPage(userController, roleSelector); // Pass roleSelector to LoginPage

        // Simulate login
        User loggedInUser = loginPage.login();

        if (loggedInUser == null) {
            System.out.println("Login failed. Exiting.");
        }
    }
}
