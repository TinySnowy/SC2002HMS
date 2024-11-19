package doctor_management.interfaces;

import appointment_management.Appointment;
import pharmacy_management.appointments.AppointmentOutcome;
import pharmacy_management.prescriptions.Prescription;
import java.util.List;

public interface IAppointmentManager {
    /**
     * View all pending appointments for a doctor
     * @param doctorId The ID of the doctor
     * @return List of pending appointments
     */
    List<Appointment> viewPendingAppointments(String doctorId);

    /**
     * Get upcoming confirmed appointments for a doctor
     * @param doctorId The ID of the doctor
     * @return List of upcoming confirmed appointments
     */
    List<Appointment> getUpcomingAppointments(String doctorId);

    /**
     * Accept a pending appointment
     * @param appointmentId The ID of the appointment to accept
     * @throws IllegalArgumentException if appointment ID is invalid
     * @throws IllegalStateException if appointment cannot be accepted
     */
    void acceptAppointment(String appointmentId);

    /**
     * Decline a pending appointment
     * @param appointmentId The ID of the appointment to decline
     * @throws IllegalArgumentException if appointment ID is invalid
     * @throws IllegalStateException if appointment cannot be declined
     */
    void declineAppointment(String appointmentId);

    /**
     * Record the outcome of a completed appointment
     * @param appointmentId The ID of the appointment
     * @param serviceType The type of service provided
     * @param consultationNotes Notes from the consultation
     * @param prescriptions List of prescriptions given
     * @return The created appointment outcome
     * @throws IllegalArgumentException if parameters are invalid
     * @throws IllegalStateException if outcome cannot be recorded
     */
    AppointmentOutcome recordAppointmentOutcome(String appointmentId,
            String serviceType,
            String consultationNotes,
            List<Prescription> prescriptions);

    /**
     * Get all completed appointment outcomes for a doctor
     * @param doctorId The ID of the doctor
     * @return List of completed appointment outcomes
     */
    List<AppointmentOutcome> getCompletedAppointmentOutcomes(String doctorId);
}