package main;

import user_management.*;
import admin_management.*;
import doctor_management.*;
import pharmacy_management.*;
import patient_management.*;
import appointment_management.*;
import login_system.LoginPage;
import java.util.Scanner;

public class Main {
    private static final String APPOINTMENT_FILE = "SC2002HMS/data/Appointments.csv";
    private static final String MEDICINE_FILE = "SC2002HMS/data/Medicine_List.csv";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            UserController userController = new UserController();
            AppointmentList appointmentList = new AppointmentList();
            IInventoryService inventoryService = new InventoryService();
            IReplenishmentService replenishmentService = new ReplenishmentService(inventoryService);

            // Create temporary InventoryManager for initialization
            InventoryManager tempInventoryManager = new InventoryManager(replenishmentService, "SYSTEM");
            MedicalRecordController medicalRecordController = new MedicalRecordController();
            LoginPage loginPage = new LoginPage(userController);

            initializeSystem(userController, appointmentList, tempInventoryManager);
            runApplicationLoop(loginPage, userController, appointmentList, replenishmentService,
                    medicalRecordController);

        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeSystem(UserController userController, AppointmentList appointmentList,
            InventoryManager inventoryManager) {
        System.out.println("Initializing Hospital Management System...");
        try {
            userController.persistAllData();
            appointmentList.loadAppointmentsFromCSV(APPOINTMENT_FILE, userController);
            inventoryManager.loadInventoryFromCSV(MEDICINE_FILE);
            System.out.println("System initialization completed successfully.");
        } catch (Exception e) {
            System.err.println("Error during system initialization: " + e.getMessage());
            throw e;
        }
    }

    private static void runApplicationLoop(LoginPage loginPage, UserController userController,
            AppointmentList appointmentList, IReplenishmentService replenishmentService,
            MedicalRecordController medicalRecordController) {
        System.out.println("\nWelcome to the Hospital Management System!");

        while (true) {
            try {
                User user = loginPage.start();
                if (user == null) {
                    System.out.println("Exiting system. Goodbye!");
                    break;
                }
                handleUserSession(user, appointmentList, replenishmentService,
                        medicalRecordController, userController);
            } catch (Exception e) {
                System.err.println("Error during session: " + e.getMessage());
                System.out.println("Returning to login...");
            }
        }
    }

    private static void handleUserSession(User user, AppointmentList appointmentList,
            IReplenishmentService replenishmentService, MedicalRecordController medicalRecordController,
            UserController userController) {
        System.out.println("\nWelcome, " + user.getName() + "!");
        System.out.println("Role: " + user.getRole());
        System.out.println("----------------------------------------");

        try {
            switch (user.getRole()) {
                case "Administrator":
                    // Create InventoryManager with admin ID
                    InventoryManager adminInventoryManager = new InventoryManager(replenishmentService, user.getId());
                    AdminDashboard adminDashboard = new AdminDashboard(userController, adminInventoryManager,
                            appointmentList);
                    adminDashboard.showMenu();
                    break;
                case "Doctor":
                    DoctorDashboard doctorDashboard = new DoctorDashboard((Doctor) user, appointmentList);
                    doctorDashboard.showDashboard();
                    break;
                case "Pharmacist":
                    Pharmacist pharmacist = (Pharmacist) user;
                    // Create InventoryManager with pharmacist ID
                    InventoryManager pharmacistInventoryManager = new InventoryManager(replenishmentService,
                            pharmacist.getId());
                    PharmacistDashboard pharmacistDashboard = new PharmacistDashboard(pharmacist,
                            pharmacistInventoryManager, replenishmentService);
                    pharmacistDashboard.showDashboard();
                    break;
                case "Patient":
                    PatientDashboard patientDashboard = new PatientDashboard((Patient) user, appointmentList);
                    patientDashboard.showDashboard();
                    break;
                default:
                    System.out.println("Invalid role detected. Logging out...");
            }
        } catch (Exception e) {
            System.err.println("Error in user session: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("----------------------------------------");
            System.out.println("Session ended. Logging out...");
        }
    }
}