package main;

import user_management.*;

import pharmacy_management.inventory.IInventoryService;
import pharmacy_management.inventory.IReplenishmentService;
import pharmacy_management.inventory.InventoryManager;
import pharmacy_management.inventory.InventoryService;
import pharmacy_management.inventory.ReplenishmentService;
import appointment_management.*;
import login_system.*;

/**
 * Main entry point for the Hospital Management System (HMS).
 * Responsible for:
 * - System initialization
 * - Core service setup
 * - Component coordination
 * - Application lifecycle management
 * - Error handling and recovery
 */
public class Main {
    /** File path for appointment data persistence */
    private static final String APPOINTMENT_FILE = "SC2002HMS/data/Appointments.csv";
    
    /** File path for medicine inventory data */
    private static final String MEDICINE_FILE = "SC2002HMS/data/Medicine_List.csv";

    /**
     * Main method - entry point of the application.
     * Initializes system components and starts main application loop.
     * Handles system-wide error recovery.
     * 
     * @param args Command line arguments (not used)
     */
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

    /**
     * Initializes core system components.
     * Handles:
     * - User data persistence
     * - Appointment data loading
     * - Inventory initialization
     * Ensures all required data is loaded before system start.
     * 
     * @param userController Controller for user management
     * @param appointmentList List manager for appointments
     * @param inventoryManager Manager for medicine inventory
     * @throws RuntimeException if initialization fails
     */
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

    /**
     * Manages the main application loop.
     * Handles:
     * - User authentication
     * - Session management
     * - Role-based navigation
     * - Error recovery
     * Continues until user chooses to exit.
     * 
     * @param loginPage Page for user authentication
     * @param roleSelector Navigator for role-based dashboards
     */
    private static void runApplicationLoop(LoginPage loginPage, RoleSelector roleSelector) {
        System.out.println("\nWelcome to the Hospital Management System!");

        while (true) {
            try {
                // Attempt user login
                User user = loginPage.start();
                if (user == null) {
                    System.out.println("Exiting system. Goodbye!");
                    break;
                }
                
                // Navigate to appropriate dashboard
                roleSelector.navigateToRoleDashboard(user);
            } catch (Exception e) {
                // Handle session errors and return to login
                System.err.println("Error during session: " + e.getMessage());
                System.out.println("Returning to login...");
            }
        }
    }
}