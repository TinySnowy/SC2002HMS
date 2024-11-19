package admin_management.handlers.appointment;

import appointment_management.Appointment;
import appointment_management.AppointmentList;
import java.util.List;

/**
 * Handles the display of appointment information in various formats.
 * This class is responsible for presenting appointment data to administrators.
 */
public class AppointmentDisplayHandler {
    private final AppointmentList appointmentList;

    /**
     * Constructs an AppointmentDisplayHandler with the specified appointment list.
     * @param appointmentList The list of appointments to manage
     */
    public AppointmentDisplayHandler(AppointmentList appointmentList) {
        this.appointmentList = appointmentList;
    }

    /**
     * Displays all appointments in the system regardless of their status.
     */
    public void viewAllAppointments() {
        List<Appointment> appointments = appointmentList.getAllAppointments();
        displayAppointments(appointments, "All");
    }

    /**
     * Displays only the appointments that have been marked as completed.
     */
    public void viewCompletedAppointments() {
        List<Appointment> completedAppointments = appointmentList.getAppointmentsByStatus("Completed");
        displayAppointments(completedAppointments, "Completed");
    }

    /**
     * Helper method to display a list of appointments with a specified type header.
     * @param appointments List of appointments to display
     * @param type The type of appointments being displayed (e.g., "All", "Completed")
     */
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

    /**
     * Displays the basic details of a single appointment.
     * If the appointment is completed, also shows its outcome.
     * @param appointment The appointment to display
     */
    private void displayAppointmentDetails(Appointment appointment) {
        System.out.println(appointment);
        if ("Completed".equals(appointment.getStatus())) {
            appointment.displayOutcome();
        }
        System.out.println("----------------------------------------");
    }

    /**
     * Displays detailed information about a specific appointment.
     * Includes patient and doctor details, appointment time, and outcome if completed.
     * @param appointmentId The unique identifier of the appointment to display
     */
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