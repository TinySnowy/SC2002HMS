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
        System.out.println("Fetching pending appointments for doctor: " + doctorId); // Debug print
        List<Appointment> appointments = appointmentList.getAllAppointments().stream()
            .filter(a -> {
                // Debug prints
                System.out.println("Checking appointment: " + a.getAppointmentId());
                System.out.println("Doctor ID: " + (a.getDoctor() != null ? a.getDoctor().getId() : "null"));
                System.out.println("Status: " + a.getStatus());
                
                return a.getDoctor() != null && 
                       a.getDoctor().getId().equals(doctorId) &&
                       "Pending".equalsIgnoreCase(a.getStatus());
            })
            .collect(Collectors.toList());

        System.out.println("Found " + appointments.size() + " pending appointments"); // Debug print
        return appointments;
    }

    @Override
    public void acceptAppointment(String appointmentId) {
        Appointment appointment = appointmentList.getAppointmentById(appointmentId);
        if (appointment != null) {
            appointment.setStatus("Confirmed");
            appointmentList.saveAppointmentsToCSV("SC2002HMS/data/Appointments.csv");
            System.out.println("Appointment " + appointmentId + " confirmed successfully");
        } else {
            System.out.println("Appointment not found: " + appointmentId);
        }
    }

    @Override
    public void declineAppointment(String appointmentId) {
        Appointment appointment = appointmentList.getAppointmentById(appointmentId);
        if (appointment != null) {
            appointment.setStatus("Cancelled");
            appointmentList.saveAppointmentsToCSV("SC2002HMS/data/Appointments.csv");
            System.out.println("Appointment " + appointmentId + " cancelled successfully");
        } else {
            System.out.println("Appointment not found: " + appointmentId);
        }
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

        // Update appointment status
        appointment.setStatus("Completed");
        appointmentList.saveAppointmentsToCSV("SC2002HMS/data/Appointments.csv");

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

        // Save the outcome
        outcomeService.saveAppointmentOutcome(outcome);

        return outcome;
    }

    @Override
    public List<AppointmentOutcome> getCompletedAppointmentOutcomes(String doctorId) {
        return appointmentList.getAllAppointments().stream()
            .filter(a -> a.getDoctor() != null && 
                        a.getDoctor().getId().equals(doctorId) &&
                        "Completed".equalsIgnoreCase(a.getStatus()))
            .map(a -> outcomeService.getOutcomeByAppointmentId(a.getAppointmentId()))
            .filter(outcome -> outcome != null)
            .collect(Collectors.toList());
    }
}