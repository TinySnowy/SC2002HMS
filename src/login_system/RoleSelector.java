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

public class RoleSelector {
    private final UserController userController;
    private final AppointmentList appointmentList;
    private final IReplenishmentService replenishmentService;
    private final FeedbackService feedbackService;

    public RoleSelector(UserController userController,
            AppointmentList appointmentList,
            IReplenishmentService replenishmentService) {
        this.userController = userController;
        this.appointmentList = appointmentList;
        this.replenishmentService = replenishmentService;
        this.feedbackService = new FeedbackService();
    }

    public void navigateToRoleDashboard(User user) {
        try {
            System.out.println("\nWelcome, " + user.getName() + "!");
            System.out.println("Role: " + user.getRole());
            System.out.println("----------------------------------------");

            switch (user.getRole()) {
                case "Administrator":
                    InventoryManager adminInventoryManager = new InventoryManager(replenishmentService, user.getId());
                    AdminMainMenu adminMenu = new AdminMainMenu(
                            userController,
                            adminInventoryManager,
                            replenishmentService,
                            appointmentList);
                    adminMenu.display();
                    break;

                case "Doctor":
                    System.out.println("Navigating to Doctor Dashboard...");
                    IAppointmentOutcomeService outcomeService = new AppointmentOutcomeService(userController);
                    DoctorDashboard doctorDashboard = new DoctorDashboard(
                            (Doctor) user,
                            appointmentList,
                            outcomeService);
                    doctorDashboard.showDashboard();
                    break;

                case "Pharmacist":
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
                    System.out.println("Navigating to Patient Dashboard...");
                    PatientDashboard patientDashboard = new PatientDashboard(
                            (Patient) user,
                            appointmentList,
                            userController);
                    patientDashboard.showDashboard();
                    break;

                default:
                    System.out.println("Unknown role. Unable to navigate.");
            }
        } catch (Exception e) {
            System.err.println("Error during navigation: " + e.getMessage());
            System.out.println("Returning to login...");
        }
    }
}