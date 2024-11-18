package patient_management.interfaces;

import appointment_management.Appointment;
import java.time.LocalDateTime;
import user_management.Doctor;

public interface IAppointmentHandler {
    void scheduleAppointment();
    void rescheduleAppointment(Appointment appointment);
    void cancelAppointment(Appointment appointment);
    boolean isDoctorAvailable(Doctor doctor, LocalDateTime appointmentDateTime);
    void displayAvailableDoctors();
}
