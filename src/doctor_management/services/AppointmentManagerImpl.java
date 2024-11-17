package doctor_management.services;

import doctor_management.interfaces.IAppointmentManager;
import appointment_management.Appointment;
import appointment_management.AppointmentList;
import pharmacy_management.appointments.AppointmentOutcome;
import pharmacy_management.appointments.IAppointmentOutcomeService;
import pharmacy_management.prescriptions.Prescription;

import java.util.List;
import java.util.stream.Collectors;

public class AppointmentManagerImpl implements IAppointmentManager {
  private final AppointmentList appointmentList;
  private final IAppointmentOutcomeService outcomeService;

  public AppointmentManagerImpl(AppointmentList appointmentList,
      IAppointmentOutcomeService outcomeService) {
    this.appointmentList = appointmentList;
    this.outcomeService = outcomeService;
  }

  @Override
  public List<Appointment> viewPendingAppointments(String doctorId) {
    return appointmentList.getAllAppointments().stream()
        .filter(a -> a.getDoctor().getId().equals(doctorId) &&
            "Confirmed".equals(a.getStatus()))
        .collect(Collectors.toList());
  }

  @Override
  public void acceptAppointment(String appointmentId) {
    Appointment appointment = appointmentList.getAppointmentById(appointmentId);
    if (appointment != null) {
      appointment.confirm();
    }
  }

  @Override
  public void declineAppointment(String appointmentId) {
    Appointment appointment = appointmentList.getAppointmentById(appointmentId);
    if (appointment != null) {
      appointment.decline();
    }
  }

  @Override
  public List<Appointment> getUpcomingAppointments(String doctorId) {
    return appointmentList.getAllAppointments().stream()
        .filter(a -> a.getDoctor().getId().equals(doctorId) &&
            "Confirmed".equals(a.getStatus()))
        .collect(Collectors.toList());
  }

  @Override
  public AppointmentOutcome recordAppointmentOutcome(String appointmentId,
      String serviceType,
      String consultationNotes,
      List<Prescription> prescriptions) {
    Appointment appointment = appointmentList.getAppointmentById(appointmentId);
    if (appointment == null) {
      System.out.println("Appointment not found.");
      return null;
    }
    // Update appointment details
    appointment.setOutcome(serviceType, consultationNotes, prescriptions);
    appointmentList.saveAppointmentsToCSV("SC2002HMS/data/Appointments.csv");

    // Save prescriptions to CSV
    appointmentList.savePrescriptionsToCSV("SC2002HMS/data/Prescriptions.csv", prescriptions);

    // Create and save appointment outcome
    AppointmentOutcome outcome = new AppointmentOutcome(
        appointmentId,
        appointment.getPatient().getId(),
        appointment.getDoctor().getId(),
        appointment.getAppointmentDate(),
        serviceType,
        consultationNotes);

    // Add prescriptions to the outcome
    for (Prescription prescription : prescriptions) {
      outcome.addPrescription(prescription);
    }

    return outcome;
  }

  @Override
  public List<AppointmentOutcome> getCompletedAppointmentOutcomes(String doctorId) {
    return appointmentList.getAllAppointments().stream()
        .filter(a -> a.getDoctor().getId().equals(doctorId) &&
            "Completed".equals(a.getStatus()))
        .map(a -> outcomeService.getOutcomeByAppointmentId(a.getAppointmentId()))
        .filter(outcome -> outcome != null)
        .collect(Collectors.toList());
  }
}
