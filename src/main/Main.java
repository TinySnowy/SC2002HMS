package main;

import user_management.*;

import pharmacy_management.inventory.IInventoryService;
import pharmacy_management.inventory.IReplenishmentService;
import pharmacy_management.inventory.InventoryManager;
import pharmacy_management.inventory.InventoryService;
import pharmacy_management.inventory.ReplenishmentService;
import appointment_management.*;
import login_system.*;

public class Main {
    private static final String APPOINTMENT_FILE = "SC2002HMS/data/Appointments.csv";
    private static final String MEDICINE_FILE = "SC2002HMS/data/Medicine_List.csv";

    public static void main(String[] args) {
        try {
            // Initialize core services
            UserController userController = UserController.getInstance();
            AppointmentList appointmentList = new AppointmentList();
            IInventoryService inventoryService = new InventoryService();
            IReplenishmentService replenishmentService = new ReplenishmentService(inventoryService);

            // Create components for initialization
            InventoryManager tempInventoryManager = new InventoryManager(replenishmentService, "SYSTEM");
            LoginPage loginPage = new LoginPage(userController);
            RoleSelector roleSelector = new RoleSelector(userController, appointmentList, replenishmentService);

            // Initialize system
            initializeSystem(userController, appointmentList, tempInventoryManager);

            // Run main application loop
            runApplicationLoop(loginPage, roleSelector);

        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeSystem(UserController userController,
            AppointmentList appointmentList,
            InventoryManager inventoryManager) {
        System.out.println("Initializing Hospital Management System...");
        try {
            userController.persistAllData();
            appointmentList.loadAppointmentsFromCSV(APPOINTMENT_FILE);
            inventoryManager.loadInventoryFromCSV(MEDICINE_FILE);
            System.out.println("System initialization completed successfully.");
        } catch (Exception e) {
            System.err.println("Error during system initialization: " + e.getMessage());
            throw e;
        }
    }

    private static void runApplicationLoop(LoginPage loginPage, RoleSelector roleSelector) {
        System.out.println("\nWelcome to the Hospital Management System!");

        while (true) {
            try {
                User user = loginPage.start();
                if (user == null) {
                    System.out.println("Exiting system. Goodbye!");
                    break;
                }
                roleSelector.navigateToRoleDashboard(user);
            } catch (Exception e) {
                System.err.println("Error during session: " + e.getMessage());
                System.out.println("Returning to login...");
            }
        }
    }
}