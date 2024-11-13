package appointment_management;

import user_management.Patient;
import user_management.Doctor;
import java.time.LocalDateTime;

public class AppointmentController {
    private AppointmentList appointmentList;

    public AppointmentController() {
        this.appointmentList = new AppointmentList();
    }

    public Appointment createAppointment(String appointmentId, Patient patient, Doctor doctor, LocalDateTime date) {
        Appointment appointment = new Appointment(appointmentId, patient, doctor, date); // Pass LocalDateTime
        appointmentList.addAppointment(appointment);
        System.out.println("Appointment created: " + appointment);
        return appointment;
    }

    public void confirmAppointment(String appointmentId) {
        Appointment appointment = appointmentList.getAppointmentById(appointmentId);
        if (appointment != null) {
            appointment.confirm();
            System.out.println("Appointment confirmed: " + appointment);
        } else {
            System.out.println("Appointment not found.");
        }
    }

    public void cancelAppointment(String appointmentId) {
        Appointment appointment = appointmentList.getAppointmentById(appointmentId);
        if (appointment != null) {
            appointment.decline();
            System.out.println("Appointment cancelled: " + appointment);
        } else {
            System.out.println("Appointment not found.");
        }
    }

    public void listAppointments() {
        System.out.println("All Appointments:");
        for (Appointment appointment : appointmentList.getAllAppointments()) {
            System.out.println(appointment);
        }
    }
}
