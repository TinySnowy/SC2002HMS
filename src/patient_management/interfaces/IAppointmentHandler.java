package patient_management.interfaces;

import appointment_management.Appointment;
import user_management.Doctor;
import java.time.LocalDateTime;

/**
 * Interface defining the contract for appointment management operations.
 * Specifies required functionality for:
 * - Appointment scheduling
 * - Appointment rescheduling
 * - Appointment cancellation
 * - Doctor availability checking
 * Provides standardized methods for appointment-related operations.
 */
public interface IAppointmentHandler {
    /**
     * Schedules a new appointment.
     * Implementation should:
     * - Display available doctors
     * - Allow doctor selection
     * - Show available time slots
     * - Create appointment record
     * - Handle scheduling conflicts
     * - Validate time slots
     */
    void scheduleAppointment();

    /**
     * Reschedules an existing appointment.
     * Implementation should:
     * - Verify appointment exists
     * - Check new time slot availability
     * - Update appointment record
     * - Handle scheduling conflicts
     * - Notify relevant parties
     * 
     * @param appointment Appointment to be rescheduled
     */
    void rescheduleAppointment(Appointment appointment);

    /**
     * Cancels an existing appointment.
     * Implementation should:
     * - Verify appointment exists
     * - Update appointment status
     * - Free up time slot
     * - Handle cancellation policies
     * - Notify relevant parties
     * 
     * @param appointment Appointment to be cancelled
     */
    void cancelAppointment(Appointment appointment);

    /**
     * Displays list of available doctors.
     * Implementation should:
     * - Show active doctors
     * - Display doctor specialties
     * - Show availability status
     * - Format output clearly
     * - Handle empty doctor list
     */
    void displayAvailableDoctors();
}