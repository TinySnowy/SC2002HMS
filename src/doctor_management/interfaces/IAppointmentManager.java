package doctor_management.interfaces;

import appointment_management.Appointment;
import pharmacy_management.appointments.AppointmentOutcome;
import pharmacy_management.prescriptions.Prescription;
import java.util.List;

/**
 * Interface defining the contract for appointment management operations.
 * Provides a standardized set of methods for managing medical appointments
 * from a doctor's perspective. Includes functionality for:
 * - Viewing pending and upcoming appointments
 * - Managing appointment status
 * - Recording appointment outcomes
 * - Tracking completed appointments
 */
public interface IAppointmentManager {
    /**
     * Retrieves all pending appointments for a specific doctor.
     * Pending appointments are those awaiting doctor confirmation.
     * Used to manage incoming appointment requests.
     * 
     * @param doctorId The unique identifier of the doctor
     * @return List of pending appointments for the specified doctor
     * @throws IllegalArgumentException if doctorId is invalid
     */
    List<Appointment> viewPendingAppointments(String doctorId);

    /**
     * Retrieves upcoming confirmed appointments for a specific doctor.
     * Includes only appointments that:
     * - Have been confirmed
     * - Are scheduled for future dates
     * - Are assigned to the specified doctor
     * 
     * @param doctorId The unique identifier of the doctor
     * @return List of upcoming confirmed appointments
     * @throws IllegalArgumentException if doctorId is invalid
     */
    List<Appointment> getUpcomingAppointments(String doctorId);

    /**
     * Accepts a pending appointment request.
     * Changes appointment status from 'Pending' to 'Confirmed'.
     * Should validate appointment state before confirmation.
     * 
     * @param appointmentId The unique identifier of the appointment to accept
     * @throws IllegalArgumentException if appointment ID is invalid
     * @throws IllegalStateException if appointment cannot be accepted
     *         (e.g., already confirmed, cancelled, or completed)
     */
    void acceptAppointment(String appointmentId);

    /**
     * Declines a pending appointment request.
     * Changes appointment status to 'Declined'.
     * Should validate appointment state before declining.
     * 
     * @param appointmentId The unique identifier of the appointment to decline
     * @throws IllegalArgumentException if appointment ID is invalid
     * @throws IllegalStateException if appointment cannot be declined
     *         (e.g., already confirmed, cancelled, or completed)
     */
    void declineAppointment(String appointmentId);

    /**
     * Records the outcome of a completed appointment.
     * Captures essential information about the consultation including:
     * - Service type provided
     * - Doctor's notes
     * - Prescribed medications
     * Updates appointment status to 'Completed'.
     * 
     * @param appointmentId The unique identifier of the appointment
     * @param serviceType The type of medical service provided
     * @param consultationNotes Detailed notes from the consultation
     * @param prescriptions List of medications prescribed
     * @return The created appointment outcome record
     * @throws IllegalArgumentException if any parameters are invalid
     * @throws IllegalStateException if outcome cannot be recorded
     *         (e.g., appointment not confirmed or already completed)
     */
    AppointmentOutcome recordAppointmentOutcome(String appointmentId,
            String serviceType,
            String consultationNotes,
            List<Prescription> prescriptions);

    /**
     * Retrieves all completed appointment outcomes for a specific doctor.
     * Provides historical record of past consultations and treatments.
     * Used for medical history tracking and reporting.
     * 
     * @param doctorId The unique identifier of the doctor
     * @return List of completed appointment outcomes
     * @throws IllegalArgumentException if doctorId is invalid
     */
    List<AppointmentOutcome> getCompletedAppointmentOutcomes(String doctorId);
}