package login_system;

import doctor_management.DoctorDashboard;
import admin_management.AdminDashboard;
import admin_management.menu.AdminMainMenu;
import patient_management.PatientDashboard;
import pharmacy_management.*;
import pharmacy_management.appointments.AppointmentOutcomeService;
import pharmacy_management.appointments.IAppointmentOutcomeService;
import pharmacy_management.inventory.IReplenishmentService;
import pharmacy_management.inventory.InventoryManager;
import appointment_management.feedback.FeedbackService;
import user_management.*;
import appointment_management.AppointmentList;

/**
 * Manages role-based navigation and dashboard selection in the HMS.
 * Handles:
 * - Role-specific dashboard routing
 * - User session initialization
 * - Dashboard component setup
 * - Error handling for navigation
 * Serves as the central routing mechanism after successful login.
 */
public class RoleSelector {
    /** Controller for user management operations */
    private final UserController userController;
    
    /** Manager for system appointments */
    private final AppointmentList appointmentList;
    
    /** Service for inventory replenishment */
    private final IReplenishmentService replenishmentService;
    
    /** Service for managing feedback */
    private final FeedbackService feedbackService;

    /**
     * Constructs a new RoleSelector with required dependencies.
     * Initializes components needed for all role dashboards.
     * 
     * @param userController Controller for user operations
     * @param appointmentList List of system appointments
     * @param replenishmentService Service for inventory management
     * @throws IllegalArgumentException if any dependency is null
     */
    public RoleSelector(UserController userController,
            AppointmentList appointmentList,
            IReplenishmentService replenishmentService) {
        this.userController = userController;
        this.appointmentList = appointmentList;
        this.replenishmentService = replenishmentService;
        this.feedbackService = new FeedbackService();
    }

    /**
     * Routes user to appropriate dashboard based on role.
     * Handles:
     * - Dashboard initialization
     * - Component setup
     * - Error recovery
     * Displays welcome message and role information.
     * 
     * @param user Authenticated user to route
     * @throws IllegalArgumentException if user is null
     * @throws IllegalStateException if role is invalid
     */
    public void navigateToRoleDashboard(User user) {
        try {
            // Display welcome message and role
            System.out.println("\nWelcome, " + user.getName() + "!");
            System.out.println("Role: " + user.getRole());
            System.out.println("----------------------------------------");

            // Route to appropriate dashboard based on role
            switch (user.getRole()) {
                case "Administrator":
                    // Initialize admin components and display dashboard
                    InventoryManager adminInventoryManager = new InventoryManager(replenishmentService, user.getId());
                    AdminMainMenu adminMenu = new AdminMainMenu(
                            userController,
                            adminInventoryManager,
                            replenishmentService,
                            appointmentList);
                    adminMenu.display();
                    break;

                case "Doctor":
                    // Initialize doctor components and display dashboard
                    System.out.println("Navigating to Doctor Dashboard...");
                    IAppointmentOutcomeService outcomeService = new AppointmentOutcomeService(userController);
                    DoctorDashboard doctorDashboard = new DoctorDashboard(
                            (Doctor) user,
                            appointmentList,
                            outcomeService);
                    doctorDashboard.showDashboard();
                    break;

                case "Pharmacist":
                    // Initialize pharmacist components and display dashboard
                    System.out.println("Navigating to Pharmacist Dashboard...");
                    Pharmacist pharmacist = (Pharmacist) user;
                    InventoryManager pharmacistInventoryManager = new InventoryManager(replenishmentService,
                            pharmacist.getId());
                    PharmacistDashboard pharmacistDashboard = new PharmacistDashboard(
                            pharmacist,
                            pharmacistInventoryManager,
                            replenishmentService,
                            userController);
                    pharmacistDashboard.showDashboard();
                    break;

                case "Patient":
                    // Initialize patient components and display dashboard
                    System.out.println("Navigating to Patient Dashboard...");
                    PatientDashboard patientDashboard = new PatientDashboard(
                            (Patient) user,
                            appointmentList,
                            userController);
                    patientDashboard.showDashboard();
                    break;

                default:
                    // Handle unknown roles
                    System.out.println("Unknown role. Unable to navigate.");
            }
        } catch (Exception e) {
            // Handle navigation errors and return to login
            System.err.println("Error during navigation: " + e.getMessage());
            System.out.println("Returning to login...");
        }
    }
}