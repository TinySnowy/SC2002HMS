package patient_management.interfaces;

import appointment_management.Appointment;

public interface IAppointmentViewer {
    void viewAllAppointments();
    void displayAppointmentDetails(Appointment appointment);
    void viewAppointmentOutcomes();
}
