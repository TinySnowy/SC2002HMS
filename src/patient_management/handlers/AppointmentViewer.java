package patient_management.handlers;

import appointment_management.*;
import patient_management.interfaces.IAppointmentViewer;
import pharmacy_management.appointments.AppointmentOutcomeService;
import pharmacy_management.appointments.AppointmentOutcome;
import user_management.Patient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Handles viewing and display of appointments in the HMS.
 * Manages:
 * - Appointment list display
 * - Appointment detail viewing
 * - Appointment outcome viewing
 * - Past/upcoming appointment filtering
 * Provides formatted display of appointment information for patients.
 */
public class AppointmentViewer implements IAppointmentViewer {
    /** Currently active patient */
    private final Patient patient;
    
    /** System-wide appointment list */
    private final AppointmentList appointmentList;
    
    /** Service for managing appointment outcomes */
    private final AppointmentOutcomeService appointmentOutcomeService;
    
    /** Formatter for date-time display */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Constructs a new AppointmentViewer with required dependencies.
     * 
     * @param patient Active patient
     * @param appointmentList System appointment list
     * @param appointmentOutcomeService Service for appointment outcomes
     */
    public AppointmentViewer(Patient patient, AppointmentList appointmentList, 
                           AppointmentOutcomeService appointmentOutcomeService) {
        this.patient = patient;
        this.appointmentList = appointmentList;
        this.appointmentOutcomeService = appointmentOutcomeService;
    }

    /**
     * Displays all appointments for the current patient.
     * Shows:
     * - Upcoming appointments
     * - Past appointments
     * - Appointment status
     * Filters and sorts appointments by date.
     */
    @Override
    public void viewAllAppointments() {
        List<Appointment> patientAppointments = appointmentList.getAppointmentsForPatient(patient.getId());
        if (patientAppointments.isEmpty()) {
            System.out.println("\nNo appointments found.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        System.out.println("\nYour Appointments");
        System.out.println("----------------------------------------");

        displayUpcomingAppointments(patientAppointments, now);
        displayPastAppointments(patientAppointments, now);
        
        System.out.println("----------------------------------------");
    }

    /**
     * Displays appointment outcomes for completed appointments.
     * Shows:
     * - Service type
     * - Consultation notes
     * - Prescriptions
     * - Appointment details
     */
    @Override
    public void viewAppointmentOutcomes() {
        System.out.println("\nAppointment Outcomes");
        System.out.println("----------------------------------------");
        List<Appointment> completedAppointments = appointmentList.getAppointmentsForPatient(patient.getId());
        if (completedAppointments.isEmpty()) {
            System.out.println("No appointment outcomes available.");
            return;
        }

        for (Appointment appointment : completedAppointments) {
            AppointmentOutcome outcome = appointmentOutcomeService
                    .getOutcomeByAppointmentId(appointment.getAppointmentId());
            if (outcome != null) {
                displayOutcomeDetails(outcome);
            }
        }
    }

    /**
     * Displays detailed information for a single appointment.
     * Shows:
     * - Appointment ID
     * - Date and time
     * - Status
     * - Doctor information
     * - Outcome details (if completed)
     * 
     * @param appointment Appointment to display
     */
    @Override
    public void displayAppointmentDetails(Appointment appointment) {
        System.out.println("\nAppointment ID: " + appointment.getAppointmentId());
        System.out.println("Date: " + appointment.getAppointmentDate().format(DATE_FORMATTER));
        System.out.println("Status: " + appointment.getStatus());
        
        if (appointment.getDoctor() != null) {
            System.out.println("Doctor: " + appointment.getDoctor().getName());
        } else {
            System.out.println("Doctor: Not yet assigned");
        }

        if (appointment.getStatus().equalsIgnoreCase("Completed")) {
            displayCompletedAppointmentDetails(appointment);
        }
        System.out.println("----------------------------------------");
    }

    /**
     * Displays upcoming appointments.
     * Filters for:
     * - Future dates
     * - Confirmed status
     * 
     * @param appointments List of all appointments
     * @param now Current date-time
     */
    private void displayUpcomingAppointments(List<Appointment> appointments, LocalDateTime now) {
        System.out.println("\nUpcoming Appointments:");
        boolean hasUpcoming = false;
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentDate().isAfter(now) &&
                    appointment.getStatus().equalsIgnoreCase("Confirmed")) {
                displayAppointmentDetails(appointment);
                hasUpcoming = true;
            }
        }
        if (!hasUpcoming) {
            System.out.println("No upcoming appointments.");
        }
    }

    /**
     * Displays past appointments.
     * Filters for:
     * - Past dates
     * - Completed status
     * - Cancelled status
     * 
     * @param appointments List of all appointments
     * @param now Current date-time
     */
    private void displayPastAppointments(List<Appointment> appointments, LocalDateTime now) {
        System.out.println("\nPast Appointments:");
        boolean hasPast = false;
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentDate().isBefore(now) ||
                    appointment.getStatus().equalsIgnoreCase("Completed") ||
                    appointment.getStatus().equalsIgnoreCase("Cancelled")) {
                displayAppointmentDetails(appointment);
                hasPast = true;
            }
        }
        if (!hasPast) {
            System.out.println("No past appointments.");
        }
    }

    /**
     * Displays details for completed appointments.
     * Shows:
     * - Service type
     * - Prescription status
     * 
     * @param appointment Completed appointment
     */
    private void displayCompletedAppointmentDetails(Appointment appointment) {
        AppointmentOutcome outcome = appointmentOutcomeService
                .getOutcomeByAppointmentId(appointment.getAppointmentId());
        if (outcome != null) {
            System.out.println("Service Type: " + outcome.getServiceType());
            if (outcome.getPrescriptions() != null && !outcome.getPrescriptions().isEmpty()) {
                System.out.println("Prescriptions Issued: Yes");
            }
        }
    }

    /**
     * Displays detailed outcome information.
     * Shows:
     * - Appointment details
     * - Service information
     * - Consultation notes
     * - Prescription details
     * 
     * @param outcome Appointment outcome to display
     */
    private void displayOutcomeDetails(AppointmentOutcome outcome) {
        System.out.println("\nAppointment ID: " + outcome.getAppointmentId());
        System.out.println("Date: " + outcome.getAppointmentDate().format(DATE_FORMATTER));
        System.out.println("Service Type: " + outcome.getServiceType());
        System.out.println("Consultation Notes: " + outcome.getConsultationNotes());

        if (!outcome.getPrescriptions().isEmpty()) {
            System.out.println("Prescriptions:");
            outcome.getPrescriptions()
                    .forEach(prescription -> System.out.println("- " + prescription.getMedicationName() +
                            " (" + prescription.getDosage() + ")"));
        }
        System.out.println("----------------------------------------");
    }
}