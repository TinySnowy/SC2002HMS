package admin_management.handlers.appointment;

import appointment_management.Appointment;
import appointment_management.AppointmentList;
import java.util.List;

public class AppointmentDisplayHandler {
    private final AppointmentList appointmentList;

    public AppointmentDisplayHandler(AppointmentList appointmentList) {
        this.appointmentList = appointmentList;
    }

    public void viewAllAppointments() {
        List<Appointment> appointments = appointmentList.getAllAppointments();
        displayAppointments(appointments, "All");
    }

    public void viewCompletedAppointments() {
        List<Appointment> completedAppointments = appointmentList.getAppointmentsByStatus("Completed");
        displayAppointments(completedAppointments, "Completed");
    }

    private void displayAppointments(List<Appointment> appointments, String type) {
        if (appointments.isEmpty()) {
            System.out.println("No " + type.toLowerCase() + " appointments found.");
            return;
        }

        System.out.println("\n" + type + " Appointments:");
        System.out.println("----------------------------------------");
        for (Appointment appointment : appointments) {
            displayAppointmentDetails(appointment);
        }
    }

    private void displayAppointmentDetails(Appointment appointment) {
        System.out.println(appointment);
        if ("Completed".equals(appointment.getStatus())) {
            appointment.displayOutcome();
        }
        System.out.println("----------------------------------------");
    }

    public void displayDetailedAppointmentInfo(String appointmentId) {
        Appointment appointment = appointmentList.getAppointmentById(appointmentId);
        if (appointment == null) {
            System.out.println("Appointment not found.");
            return;
        }

        System.out.println("\nDetailed Appointment Information:");
        System.out.println("----------------------------------------");
        System.out.println("Appointment ID: " + appointment.getAppointmentId());
        System.out.println("Patient: " + appointment.getPatient().getName() + 
                         " (ID: " + appointment.getPatient().getId() + ")");
        System.out.println("Doctor: " + appointment.getDoctor().getName() + 
                         " (ID: " + appointment.getDoctor().getId() + ")");
        System.out.println("Date/Time: " + appointment.getAppointmentDate());
        System.out.println("Status: " + appointment.getStatus());

        if ("Completed".equals(appointment.getStatus())) {
            System.out.println("\nOutcome Information:");
            System.out.println("Service Type: " + appointment.getServiceType());
            System.out.println("Consultation Notes: " + appointment.getConsultationNotes());
            appointment.displayOutcome();
        }
        System.out.println("----------------------------------------");
    }
}