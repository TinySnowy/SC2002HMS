package main;

import user_management.*;
import admin_management.*;
import doctor_management.*;
import pharmacy_management.*;
import patient_management.*;
import appointment_management.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Initialize controllers and services
        UserController userController = new UserController();
        AppointmentList appointmentList = new AppointmentList();
        InventoryManager inventoryManager = new InventoryManager();
        MedicalRecordController medicalRecordController = new MedicalRecordController();
        MedicalRecordUpdater medicalRecordUpdater = new MedicalRecordUpdater(medicalRecordController);


        // Load data
        userController.persistData(); // Load user data from CSV files
        appointmentList.loadAppointmentsFromCSV("data/Appointments.csv", userController); // Load appointments
        inventoryManager.loadInventoryFromCSV("data/Medicine_List.csv"); // Load inventory

        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to HMS System");

        while (true) {
            System.out.println("\nLogin:");
            System.out.print("Enter User ID: ");
            String userId = scanner.nextLine();

            User user = userController.getUserById(userId);
            if (user == null) {
                System.out.println("Invalid User ID. Try again.");
                continue;
            }

            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            if (!user.authenticate(password)) {
                System.out.println("Invalid Password. Try again.");
                continue;
            }

            // Role-based redirection
            switch (user.getRole()) {
                case "Administrator" -> {
                    AdminDashboard adminDashboard = new AdminDashboard(inventoryManager, appointmentList);
                    adminDashboard.showMenu();
                }
                case "Doctor" -> {
                    DoctorDashboard doctorDashboard = new DoctorDashboard((Doctor) user, appointmentList);
                    doctorDashboard.showDashboard();
                }
                case "Pharmacist" -> {
                    PharmacistDashboard pharmacistDashboard = new PharmacistDashboard((Pharmacist) user, inventoryManager);
                    pharmacistDashboard.showDashboard();
                }
                case "Patient" -> {
                    PatientDashboard patientDashboard = new PatientDashboard((Patient) user, appointmentList);
                    patientDashboard.showDashboard();
                }

                default -> System.out.println("Invalid role. Exiting...");
            }

            System.out.println("Logout successful. Returning to login...");
        }
    }
}
