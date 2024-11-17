package login_system;

import doctor_management.DoctorDashboard;
import admin_management.AdminDashboard;
import patient_management.PatientDashboard;
import pharmacy_management.*;
import pharmacy_management.appointments.AppointmentOutcomeService;
import pharmacy_management.appointments.IAppointmentOutcomeService;
import pharmacy_management.inventory.IReplenishmentService;
import pharmacy_management.inventory.InventoryManager;
import user_management.*;
import appointment_management.AppointmentList;

public class RoleSelector {
    private final UserController userController;
    private final AppointmentList appointmentList;
    private final IReplenishmentService replenishmentService;

    public RoleSelector(UserController userController,
            AppointmentList appointmentList,
            IReplenishmentService replenishmentService) {
        this.userController = userController;
        this.appointmentList = appointmentList;
        this.replenishmentService = replenishmentService;
    }

    public void navigateToRoleDashboard(User user) {
        try {
            System.out.println("\nWelcome, " + user.getName() + "!");
            System.out.println("Role: " + user.getRole());
            System.out.println("----------------------------------------");

            switch (user.getRole()) {
                case "Administrator":
                    InventoryManager adminInventoryManager = new InventoryManager(replenishmentService, user.getId());
                    AdminDashboard adminDashboard = new AdminDashboard(
                            userController,
                            adminInventoryManager,
                            replenishmentService,
                            appointmentList);
                    adminDashboard.showDashboard();
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
                    // Create InventoryManager with pharmacist ID
                    InventoryManager pharmacistInventoryManager = new InventoryManager(replenishmentService,
                            pharmacist.getId());
                    PharmacistDashboard pharmacistDashboard = new PharmacistDashboard(
                            pharmacist,
                            pharmacistInventoryManager,
                            replenishmentService,
                            userController); // Pass UserController here
                    pharmacistDashboard.showDashboard();
                    break;

                case "Patient":
                    System.out.println("Navigating to Patient Dashboard...");
                    PatientDashboard patientDashboard = new PatientDashboard(
                            (Patient) user,
                            appointmentList);
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