package appointment_management;

import user_management.Patient;
import user_management.Doctor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import pharmacy_management.prescriptions.Prescription;

/**
 * Represents a medical appointment in the hospital management system.
 * Manages all appointment-related information including:
 * - Patient and doctor details
 * - Appointment scheduling
 * - Status tracking
 * - Consultation outcomes
 * - Prescription management
 */
public class Appointment {
    /** Unique identifier for the appointment */
    private String appointmentId;
    
    /** Patient associated with the appointment */
    private Patient patient;
    
    /** Doctor conducting the appointment */
    private Doctor doctor;
    
    /** Scheduled date and time of the appointment */
    private LocalDateTime appointmentDate;
    
    /** Current status of the appointment (Pending/Confirmed/Cancelled/Completed) */
    private String status;
    
    /** Type of medical service provided */
    private String serviceType;
    
    /** Doctor's notes from the consultation */
    private String consultationNotes;
    
    /** List of prescriptions issued during the appointment */
    private List<Prescription> prescriptions;

    /**
     * Constructs a new appointment with essential details.
     * Initializes with pending status and empty prescription list.
     * 
     * @param appointmentId Unique identifier for the appointment
     * @param patient Patient scheduled for the appointment
     * @param doctor Doctor conducting the appointment
     * @param appointmentDate Scheduled date and time
     */
    public Appointment(String appointmentId, Patient patient, Doctor doctor, LocalDateTime appointmentDate) {
        this.appointmentId = appointmentId;
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDate = appointmentDate;
        this.status = "Pending"; // Default status when created
        this.prescriptions = new ArrayList<>();
    }

    /**
     * Retrieves the appointment's unique identifier
     * @return Appointment ID
     */
    public String getAppointmentId() { return appointmentId; }

    /**
     * Retrieves the patient associated with the appointment
     * @return Patient object
     */
    public Patient getPatient() { return patient; }

    /**
     * Retrieves the doctor conducting the appointment
     * @return Doctor object
     */
    public Doctor getDoctor() { return doctor; }

    /**
     * Retrieves the scheduled appointment date and time
     * @return LocalDateTime of appointment
     */
    public LocalDateTime getAppointmentDate() { return appointmentDate; }

    /**
     * Retrieves the current appointment status
     * @return Status string (Pending/Confirmed/Cancelled/Completed)
     */
    public String getStatus() { return status; }

    /**
     * Updates the appointment status
     * @param status New status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Updates the appointment date and time
     * @param appointmentDate New date and time
     */
    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    /**
     * Retrieves the type of medical service provided
     * @return Service type description
     */
    public String getServiceType() { return serviceType; }

    /**
     * Retrieves the consultation notes
     * @return Doctor's notes from the consultation
     */
    public String getConsultationNotes() { return consultationNotes; }

    /**
     * Confirms the appointment.
     * Updates status to "Confirmed"
     */
    public void confirm() {
        this.status = "Confirmed";
    }

    /**
     * Declines/Cancels the appointment.
     * Updates status to "Cancelled"
     */
    public void decline() {
        this.status = "Cancelled";
    }

    // Method to set appointment outcome
    public void setOutcome(String serviceType, String consultationNotes, List<Prescription> prescriptions) {
        this.serviceType = serviceType;
        this.consultationNotes = consultationNotes;
        this.status = "Completed";
        this.prescriptions = new ArrayList<>(prescriptions); // Create a new list to avoid reference issues
    }

    // Method to add a prescription to the appointment
    public void addPrescription(Prescription prescription) {
        this.prescriptions.add(prescription);
    }

    // Display detailed outcome, including prescriptions
    public void displayOutcome() {
        if ("Completed".equals(status)) {
            System.out.println("Service Type: " + serviceType);
            System.out.println("Consultation Notes: " + consultationNotes);
            System.out.println("Prescriptions:");
            for (Prescription prescription : prescriptions) {
                System.out.println(
                        "  - " + prescription.getMedicationName() + " (Status: " + prescription.getStatus() + ")");
            }
        } else {
            System.out.println("No outcome recorded for this appointment.");
        }
    }

    @Override
    public String toString() {
        return "Appointment ID: " + appointmentId +
                ", Patient: " + (patient != null ? patient.getName() : "None") +
                ", Doctor: " + (doctor != null ? doctor.getName() : "None") +
                ", Date: " + appointmentDate +
                ", Status: " + status;
    }
}
