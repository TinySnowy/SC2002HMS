package doctor_management;

import doctor_management.interfaces.*;
import doctor_management.services.*;
import appointment_management.AppointmentList;
import appointment_management.Appointment;
import patient_management.MedicalRecordController;
import pharmacy_management.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DoctorManagementFacade {
  private final IMedicalRecordManager medicalRecordManager;
  private final IAppointmentManager appointmentManager;
  private final IScheduleManager scheduleManager;

  public DoctorManagementFacade(MedicalRecordController recordController,
      AppointmentList appointmentList,
      IAppointmentOutcomeService outcomeService) {
    this.medicalRecordManager = new MedicalRecordManagerImpl(recordController);
    this.appointmentManager = new AppointmentManagerImpl(appointmentList, outcomeService);
    this.scheduleManager = new ScheduleManagerImpl();
  }

  // Medical Record Management
  public void viewPatientRecord(String doctorId, String patientId) {
    if (medicalRecordManager.hasAccessToRecord(doctorId, patientId)) {
      medicalRecordManager.viewMedicalRecord(patientId);
    } else {
      System.out.println("Patient record not found. Please check the Patient ID.");
    }
  }

  public void updatePatientRecord(String doctorId, String patientId,
      String diagnosis, String prescription) {
    if (medicalRecordManager.hasAccessToRecord(doctorId, patientId)) {
      medicalRecordManager.updateMedicalRecord(patientId, diagnosis, prescription);
    } else {
      System.out.println("Patient record not found. Please check the Patient ID.");
    }
  }

  // Appointment Management
  public List<Appointment> viewPendingAppointments(String doctorId) {
    return appointmentManager.viewPendingAppointments(doctorId);
  }

  public void acceptAppointment(String doctorId, String appointmentId) {
    appointmentManager.acceptAppointment(appointmentId);
  }

  public void declineAppointment(String doctorId, String appointmentId) {
    appointmentManager.declineAppointment(appointmentId);
  }

  public AppointmentOutcome recordAppointmentOutcome(String doctorId,
      String appointmentId,
      String serviceType,
      String consultationNotes,
      List<Prescription> prescriptions) {
    return appointmentManager.recordAppointmentOutcome(appointmentId,
        serviceType,
        consultationNotes,
        prescriptions);
  }

  public List<AppointmentOutcome> viewCompletedAppointments(String doctorId) {
    return appointmentManager.getCompletedAppointmentOutcomes(doctorId);
  }

  // Schedule Management
  public void setDoctorAvailability(String doctorId, Map<String, String> weeklySchedule) {
    scheduleManager.setAvailability(doctorId, weeklySchedule);
  }

  public Map<String, String> getDoctorAvailability(String doctorId) {
    return scheduleManager.getAvailability(doctorId);
  }

  public boolean isDoctorAvailable(String doctorId, LocalDateTime dateTime) {
    return scheduleManager.isAvailable(doctorId, dateTime);
  }
}
