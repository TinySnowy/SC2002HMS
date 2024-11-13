package admin_management;

import appointment_management.Appointment;
import appointment_management.AppointmentList;

public class AppointmentViewer {
    private AppointmentList appointmentList;

    public AppointmentViewer(AppointmentList appointmentList) {
        this.appointmentList = appointmentList;
    }

    public void viewAllAppointments() {
        System.out.println("Admin View - All Appointments:");
        for (Appointment appointment : appointmentList.getAllAppointments()) {
            System.out.println(appointment);
        }
    }
}
