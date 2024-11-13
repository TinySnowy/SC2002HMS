package appointment_management;

import java.util.ArrayList;
import java.util.List;

public class AppointmentList {
    private List<Appointment> appointments;

    public AppointmentList() {
        this.appointments = new ArrayList<>();
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    public Appointment getAppointmentById(String appointmentId) {
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentId().equals(appointmentId)) {
                return appointment;
            }
        }
        return null;
    }

    public List<Appointment> getAllAppointments() {
        return appointments;
    }
}
