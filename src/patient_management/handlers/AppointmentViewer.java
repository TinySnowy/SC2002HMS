package patient_management.handlers;

import appointment_management.*;
import patient_management.interfaces.IAppointmentViewer;
import pharmacy_management.appointments.AppointmentOutcomeService;
import pharmacy_management.appointments.AppointmentOutcome;
import user_management.Patient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AppointmentViewer implements IAppointmentViewer {
    private final Patient patient;
    private final AppointmentList appointmentList;
    private final AppointmentOutcomeService appointmentOutcomeService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AppointmentViewer(Patient patient, AppointmentList appointmentList, 
                           AppointmentOutcomeService appointmentOutcomeService) {
        this.patient = patient;
        this.appointmentList = appointmentList;
        this.appointmentOutcomeService = appointmentOutcomeService;
    }

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