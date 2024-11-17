package pharmacy_management.appointments;

import java.time.LocalDateTime;
import java.util.List;

import pharmacy_management.prescriptions.Prescription;

import java.util.ArrayList;

public class AppointmentOutcome {
  private final String appointmentId;
  private final String patientId;
  private final String doctorId;
  private final LocalDateTime appointmentDate;
  private final String serviceType;
  private final String consultationNotes;
  private final List<Prescription> prescriptions;

  public AppointmentOutcome(String appointmentId, String patientId, String doctorId,
      LocalDateTime appointmentDate, String serviceType, String consultationNotes) {
    this.appointmentId = appointmentId;
    this.patientId = patientId;
    this.doctorId = doctorId;
    this.appointmentDate = appointmentDate;
    this.serviceType = serviceType;
    this.consultationNotes = consultationNotes;
    this.prescriptions = new ArrayList<>();
  }

  public String getAppointmentId() {
    return appointmentId;
  }

  public String getPatientId() {
    return patientId;
  }

  public String getDoctorId() {
    return doctorId;
  }

  public LocalDateTime getAppointmentDate() {
    return appointmentDate;
  }

  public String getServiceType() {
    return serviceType;
  }

  public String getConsultationNotes() {
    return consultationNotes;
  }

  public List<Prescription> getPrescriptions() {
    return new ArrayList<>(prescriptions);
  }

  public void addPrescription(Prescription prescription) {
    prescriptions.add(prescription);
  }
}
