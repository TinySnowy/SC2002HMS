package admin_management;

import appointment_management.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentManagementService implements IAppointmentManagement {
  private final AppointmentList appointmentList;

  public AppointmentManagementService(AppointmentList appointmentList) {
    this.appointmentList = appointmentList;
  }

  @Override
  public List<Appointment> getAllAppointments() {
    return appointmentList.getAllAppointments();
  }

  @Override
  public List<Appointment> getAppointmentsByStatus(String status) {
    return appointmentList.getAppointmentsByStatus(status);
  }

  @Override
  public List<Appointment> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
    return appointmentList.getAllAppointments().stream()
        .filter(apt -> !apt.getAppointmentDate().isBefore(start) &&
            !apt.getAppointmentDate().isAfter(end))
        .collect(Collectors.toList());
  }

  @Override
  public void displayAppointmentDetails(Appointment appointment) {
    if (appointment == null) {
      System.out.println("No appointment details available.");
      return;
    }

    System.out.println("\nAppointment Details:");
    System.out.println("----------------------------------------");
    System.out.println("Appointment ID: " + appointment.getAppointmentId());
    System.out.println("Patient: " + appointment.getPatient().getName() +
        " (ID: " + appointment.getPatient().getId() + ")");
    System.out.println("Doctor: " + appointment.getDoctor().getName() +
        " (ID: " + appointment.getDoctor().getId() + ")");
    System.out.println("Date/Time: " + appointment.getAppointmentDate());
    System.out.println("Status: " + appointment.getStatus());

    if (appointment.getStatus().equals("Completed")) {
      System.out.println("\nOutcome Details:");
      System.out.println("Service Type: " + appointment.getServiceType());
      System.out.println("Consultation Notes: " + appointment.getConsultationNotes());
    }

    System.out.println("----------------------------------------");
  }

  @Override
  public void displayAppointmentList(List<Appointment> appointments) {
    if (appointments == null || appointments.isEmpty()) {
      System.out.println("No appointments found.");
      return;
    }

    System.out.println("\nAppointments List:");
    System.out.println("----------------------------------------");
    for (Appointment apt : appointments) {
      System.out.printf("Appointment ID: %-10s%n", apt.getAppointmentId());
      System.out.printf("Patient: %-20s (ID: %s)%n",
          apt.getPatient().getName(), apt.getPatient().getId());
      System.out.printf("Doctor: %-20s (ID: %s)%n",
          apt.getDoctor().getName(), apt.getDoctor().getId());
      System.out.printf("Date/Time: %s%n", apt.getAppointmentDate());
      System.out.printf("Status: %s%n", apt.getStatus());
      System.out.println("----------------------------------------");
    }
  }

  // Helper methods for appointment management
  public void filterAppointmentsByDoctor(String doctorId) {
    List<Appointment> filteredAppointments = appointmentList.getAppointmentsForDoctor(doctorId);
    displayAppointmentList(filteredAppointments);
  }

  public void filterAppointmentsByPatient(String patientId) {
    List<Appointment> filteredAppointments = appointmentList.getAppointmentsForPatient(patientId);
    displayAppointmentList(filteredAppointments);
  }

  public Appointment getAppointmentById(String appointmentId) {
    return appointmentList.getAppointmentById(appointmentId);
  }
}