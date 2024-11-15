package appointment_management;

import user_management.Patient;
import user_management.Doctor;
import pharmacy_management.Prescription;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Appointment {
    private String appointmentId;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime appointmentDate;
    private String status; // Track the appointment status (e.g., Scheduled, Confirmed, Cancelled)
    private String serviceType; // Type of service provided (for outcome records)
    private String consultationNotes; // Doctor's consultation notes
    private List<Prescription> prescriptions; // List of prescriptions for this appointment

    public Appointment(String appointmentId, Patient patient, Doctor doctor, LocalDateTime appointmentDate) {
        this.appointmentId = appointmentId;
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDate = appointmentDate;
        this.status = "Pending"; // Default status when created
        this.prescriptions = new ArrayList<>();
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

    public void setStatus(String status) {
        this.status = status; // Allow setting the status directly
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getConsultationNotes() {
        return consultationNotes;
    }

    // Method to confirm the appointment
    public void confirm() {
        this.status = "Confirmed";
    }

    // Method to decline the appointment
    public void decline() {
        this.status = "Cancelled";
    }

    // Method to set appointment outcome
    public void setOutcome(String serviceType, String consultationNotes, List<Prescription> prescriptions) {
        this.serviceType = serviceType;
        this.consultationNotes = consultationNotes;
        this.status = "Completed";
        this.prescriptions = prescriptions; // Store the list of prescriptions with the appointment outcome
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
                System.out.println("  - " + prescription.getMedicationName() + " (Status: " + prescription.getStatus() + ")");
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
