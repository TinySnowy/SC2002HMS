package login_system;

import doctor_management.DoctorDashboard;
import appointment_management.AppointmentList;
import patient_management.PatientDashboard;
import pharmacy_management.InventoryManager;
import pharmacy_management.PharmacistDashboard;
import user_management.Doctor;
import user_management.Patient;
import user_management.Pharmacist;
import user_management.User;

public class RoleSelector {
    private AppointmentList appointmentList; // Shared appointment list
    private InventoryManager inventoryManager; // Inventory manager for pharmacist dashboard

    public RoleSelector(AppointmentList appointmentList, InventoryManager inventoryManager) {
        this.appointmentList = appointmentList;
        this.inventoryManager = inventoryManager;
    }

    public void navigateToRoleDashboard(User user) {
        switch (user.getRole()) {
            case "Doctor": {
                System.out.println("Navigating to Doctor Dashboard...");
                DoctorDashboard doctorDashboard = new DoctorDashboard((Doctor) user, appointmentList);
                doctorDashboard.showDashboard();
            }
            case "Patient": {
                System.out.println("Navigating to Patient Dashboard...");
                PatientDashboard patientDashboard = new PatientDashboard((Patient) user, appointmentList);
                patientDashboard.showDashboard();
            }
            case "Pharmacist": {
                System.out.println("Navigating to Pharmacist Dashboard...");
                PharmacistDashboard pharmacistDashboard = new PharmacistDashboard((Pharmacist) user, inventoryManager);
                pharmacistDashboard.showDashboard();
            }
            case "Administrator":
                System.out.println("Navigating to Admin Dashboard...");
            default:
                System.out.println("Unknown role. Unable to navigate.");
        }
    }
}
