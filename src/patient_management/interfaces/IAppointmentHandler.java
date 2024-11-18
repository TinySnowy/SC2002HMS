package patient_management.interfaces;

import appointment_management.Appointment;
import user_management.Doctor;
import java.time.LocalDateTime;

public interface IAppointmentHandler {
    void scheduleAppointment();
    void rescheduleAppointment(Appointment appointment);
    void cancelAppointment(Appointment appointment);
    void displayAvailableDoctors();
}