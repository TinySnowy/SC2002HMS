package appointment_management;

import user_management.Patient;
import user_management.Doctor;
import java.time.LocalDateTime;

/**
 * Controller class for managing appointments in the hospital management system.
 * Handles all appointment-related operations including:
 * - Creation of new appointments
 * - Status management (confirmation/cancellation)
 * - Appointment listing and retrieval
 * - Appointment updates and modifications
 * Serves as the primary interface for appointment operations.
 */
public class AppointmentController {
    /** List manager for storing and retrieving appointments */
    private AppointmentList appointmentList;

    /**
     * Constructs a new AppointmentController.
     * Initializes the appointment list for managing appointments.
     * Provides centralized control for appointment operations.
     */
    public AppointmentController() {
        this.appointmentList = new AppointmentList();
    }

    /**
     * Creates a new appointment in the system.
     * Generates a new appointment with provided details and adds it to the list.
     * 
     * @param appointmentId Unique identifier for the appointment
     * @param patient Patient scheduled for the appointment
     * @param doctor Doctor conducting the appointment
     * @param date Scheduled date and time
     * @return Newly created appointment object
     */
    public Appointment createAppointment(String appointmentId, Patient patient, Doctor doctor, LocalDateTime date) {
        Appointment appointment = new Appointment(appointmentId, patient, doctor, date);
        appointmentList.addAppointment(appointment);
        System.out.println("Appointment created: " + appointment);
        return appointment;
    }

    /**
     * Confirms a pending appointment.
     * Updates the appointment status to "Confirmed".
     * 
     * @param appointmentId ID of the appointment to confirm
     * Prints success/failure message based on operation outcome
     */
    public void confirmAppointment(String appointmentId) {
        Appointment appointment = appointmentList.getAppointmentById(appointmentId);
        if (appointment != null) {
            appointment.confirm();
            System.out.println("Appointment confirmed: " + appointment);
        } else {
            System.out.println("Appointment not found.");
        }
    }

    /**
     * Cancels an existing appointment.
     * Updates the appointment status to "Declined".
     * 
     * @param appointmentId ID of the appointment to cancel
     * Prints success/failure message based on operation outcome
     */
    public void cancelAppointment(String appointmentId) {
        Appointment appointment = appointmentList.getAppointmentById(appointmentId);
        if (appointment != null) {
            appointment.decline();
            System.out.println("Appointment cancelled: " + appointment);
        } else {
            System.out.println("Appointment not found.");
        }
    }

    /**
     * Lists all appointments in the system.
     * Prints all appointments stored in the appointment list.
     */
    public void listAppointments() {
        System.out.println("All Appointments:");
        for (Appointment appointment : appointmentList.getAllAppointments()) {
            System.out.println(appointment);
        }
    }
}
