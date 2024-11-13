package appointment_management;

import user_management.Patient;
import user_management.Doctor;
import java.time.LocalDateTime;

public class Appointment {
    private String appointmentId;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime appointmentDate;
    private String status; // Track the appointment status (e.g., Scheduled, Confirmed, Cancelled)
    private String serviceType; // Type of service provided (for outcome records)
    private String consultationNotes; // Doctor's consultation notes
    private String prescribedMedications; // Medications prescribed during the appointment

    public Appointment(String appointmentId, Patient patient, Doctor doctor, LocalDateTime appointmentDate) {
        this.appointmentId = appointmentId;
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDate = appointmentDate;
        this.status = "Pending"; // Default status when created
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    // Method to confirm the appointment
    public void confirm() {
        this.status = "Confirmed";
    }

    // Method to decline the appointment
    public void decline() {
        this.status = "Cancelled";
    }

    // Methods for appointment outcome
    public void setOutcome(String serviceType, String consultationNotes, String prescribedMedications) {
        this.serviceType = serviceType;
        this.consultationNotes = consultationNotes;
        this.prescribedMedications = prescribedMedications;
        this.status = "Completed"; // Set status to completed once outcomes are recorded
    }

    @Override
    public String toString() {
        return "Appointment ID: " + appointmentId +
                ", Patient: " + patient.getName() +
                ", Doctor: " + (doctor != null ? doctor.getName() : "None") +
                ", Date: " + appointmentDate +
                ", Status: " + status;
    }

    // Display detailed outcome if available
    public void displayOutcome() {
        if ("Completed".equals(status)) {
            System.out.println("Service Type: " + serviceType);
            System.out.println("Consultation Notes: " + consultationNotes);
            System.out.println("Prescribed Medications: " + prescribedMedications);
        } else {
            System.out.println("No outcome recorded for this appointment.");
        }
    }
}
