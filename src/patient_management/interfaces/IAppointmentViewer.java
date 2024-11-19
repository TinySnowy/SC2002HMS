package patient_management.interfaces;

import appointment_management.Appointment;

/**
 * Interface defining the contract for appointment viewing operations.
 * Specifies required functionality for:
 * - Viewing all appointments
 * - Displaying appointment details
 * - Viewing appointment outcomes
 * - Formatting appointment information
 * Provides standardized methods for appointment display operations.
 */
public interface IAppointmentViewer {
    /**
     * Displays all appointments for the current user.
     * Implementation should:
     * - Show upcoming appointments
     * - Show past appointments
     * - Display appointment status
     * - Sort by date/time
     * - Format output clearly
     * - Handle empty appointment list
     */
    void viewAllAppointments();

    /**
     * Displays detailed information for a specific appointment.
     * Implementation should:
     * - Show appointment ID
     * - Show date and time
     * - Display doctor information
     * - Show appointment status
     * - Display location/department
     * - Show any special instructions
     * - Handle null appointments
     * 
     * @param appointment Appointment to display details for
     */
    void displayAppointmentDetails(Appointment appointment);

    /**
     * Displays outcomes for completed appointments.
     * Implementation should:
     * - Show consultation notes
     * - Display prescriptions
     * - Show treatment details
     * - Display follow-up instructions
     * - Format outcome information
     * - Handle missing outcomes
     * - Sort by date
     */
    void viewAppointmentOutcomes();
}
