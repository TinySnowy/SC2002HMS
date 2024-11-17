package admin_management;

import appointment_management.Appointment;
import java.time.LocalDateTime;
import java.util.List;

public interface IAppointmentManagement {
  List<Appointment> getAllAppointments();

  List<Appointment> getAppointmentsByStatus(String status);

  List<Appointment> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end);

  void displayAppointmentDetails(Appointment appointment);

  void displayAppointmentList(List<Appointment> appointments);
}