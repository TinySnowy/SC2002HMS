package patient_management;

import user_management.Patient;
import appointment_management.Appointment;
import appointment_management.AppointmentList;

public class AppointmentOutcomeViewer {
    private Patient patient;
    private AppointmentList appointmentList;

    public AppointmentOutcomeViewer(Patient patient) {
        this.patient = patient;
        this.appointmentList = new AppointmentList();
    }

    public void displayOutcomes() {
        System.out.println("\nAppointment Outcomes for " + patient.getName() + ":");
        for (Appointment appointment : appointmentList.getAllAppointments()) {
            System.out.println(appointment + ", Outcome: " + getAppointmentOutcome(appointment));
        }
    }

    private String getAppointmentOutcome(Appointment appointment) {
        // Placeholder for actual outcome; this could be enhanced
        return "Outcome details for Appointment ID " + appointment.getAppointmentId();
    }
}
