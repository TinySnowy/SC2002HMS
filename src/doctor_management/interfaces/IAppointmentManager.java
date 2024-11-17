package doctor_management.interfaces;

import appointment_management.Appointment;
import pharmacy_management.appointments.AppointmentOutcome;
import pharmacy_management.prescriptions.Prescription;

import java.util.List;

public interface IAppointmentManager {
  List<Appointment> viewPendingAppointments(String doctorId);

  void acceptAppointment(String appointmentId);

  void declineAppointment(String appointmentId);

  List<Appointment> getUpcomingAppointments(String doctorId);

  AppointmentOutcome recordAppointmentOutcome(String appointmentId, String serviceType,
      String consultationNotes, List<Prescription> prescriptions);

  List<AppointmentOutcome> getCompletedAppointmentOutcomes(String doctorId);
}
