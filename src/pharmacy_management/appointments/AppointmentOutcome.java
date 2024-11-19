package pharmacy_management.appointments;

import java.time.LocalDateTime;
import java.util.List;

import pharmacy_management.prescriptions.Prescription;

import java.util.ArrayList;

/**
 * Represents the medical outcome and decisions from a completed appointment.
 * This class serves as a comprehensive record of:
 * - Medical consultation results
 * - Treatment decisions
 * - Prescribed medications
 * - Doctor's observations
 * Essential for maintaining patient medical history and treatment tracking.
 */
public class AppointmentOutcome {
  /** Tracks the specific appointment this outcome is linked to */
  private final String appointmentId;
  /** Links the outcome to a specific patient's medical history */
  private final String patientId;
  /** Records the treating physician for accountability and follow-up */
  private final String doctorId;
  /** Records when the medical consultation occurred */
  private final LocalDateTime appointmentDate;
  /** Categorizes the type of medical service delivered */
  private final String serviceType;
  /** Stores the physician's medical observations and decisions */
  private final String consultationNotes;
  /** Maintains a record of medications prescribed during consultation */
  private final List<Prescription> prescriptions;

  /**
   * Creates a new medical consultation outcome record.
   * Links all essential participants and records initial medical decisions.
   * Initializes an empty prescription list for medication tracking.
   * 
   * @param appointmentId Links to specific appointment
   * @param patientId Links to patient record
   * @param doctorId Links to treating physician
   * @param appointmentDate When consultation occurred
   * @param serviceType Category of medical service
   * @param consultationNotes Physician's observations
   */
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

  /**
   * Retrieves the linked appointment identifier.
   * Used for tracking and record association.
   */
  public String getAppointmentId() {
    return appointmentId;
  }

  /**
   * Retrieves the patient identifier.
   * Used for medical history tracking.
   */
  public String getPatientId() {
    return patientId;
  }

  /**
   * Retrieves the treating physician's identifier.
   * Used for medical responsibility tracking.
   */
  public String getDoctorId() {
    return doctorId;
  }

  /**
   * Retrieves when the consultation occurred.
   * Important for medical timeline tracking.
   */
  public LocalDateTime getAppointmentDate() {
    return appointmentDate;
  }

  /**
   * Retrieves the category of medical service.
   * Used for service classification and billing.
   */
  public String getServiceType() {
    return serviceType;
  }

  /**
   * Retrieves physician's medical observations.
   * Critical for treatment history and follow-up care.
   */
  public String getConsultationNotes() {
    return consultationNotes;
  }

  /**
   * Provides safe access to prescription history.
   * Returns a copy to prevent external modification.
   * Essential for medication tracking and safety.
   */
  public List<Prescription> getPrescriptions() {
    return new ArrayList<>(prescriptions);
  }

  /**
   * Records a new medication prescription.
   * Maintains accurate medication history for the patient.
   * Critical for drug interaction prevention and treatment tracking.
   * 
   * @param prescription New medication order to record
   */
  public void addPrescription(Prescription prescription) {
    prescriptions.add(prescription);
  }
}
