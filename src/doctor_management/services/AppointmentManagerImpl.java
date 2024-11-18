package doctor_management.services;

import doctor_management.interfaces.IAppointmentManager;
import appointment_management.Appointment;
import appointment_management.AppointmentList;
import pharmacy_management.appointments.AppointmentOutcome;
import pharmacy_management.appointments.IAppointmentOutcomeService;
import pharmacy_management.prescriptions.Prescription;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentManagerImpl implements IAppointmentManager {
    private final AppointmentList appointmentList;
    private final IAppointmentOutcomeService outcomeService;

    public AppointmentManagerImpl(AppointmentList appointmentList,
            IAppointmentOutcomeService outcomeService) {
        if (appointmentList == null || outcomeService == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        this.appointmentList = appointmentList;
        this.outcomeService = outcomeService;
        System.out.println("AppointmentManagerImpl initialized"); // Debug log
    }

    @Override
    public List<Appointment> viewPendingAppointments(String doctorId) {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            System.out.println("Invalid doctor ID provided");
            return List.of();
        }

        System.out.println("Fetching pending appointments for doctor: " + doctorId);
        
        List<Appointment> allAppointments = appointmentList.getAllAppointments();
        System.out.println("Total appointments found: " + allAppointments.size());

        List<Appointment> pendingAppointments = allAppointments.stream()
            .filter(a -> {
                if (a == null) {
                    System.out.println("Found null appointment entry");
                    return false;
                }
                
                if (a.getDoctor() == null) {
                    System.out.println("Appointment " + a.getAppointmentId() + " has no doctor assigned");
                    return false;
                }

                boolean isDoctor = a.getDoctor().getId().equals(doctorId);
                boolean isPending = "Pending".equalsIgnoreCase(a.getStatus());
                boolean isFuture = a.getAppointmentDate().isAfter(LocalDateTime.now());

                System.out.println("Checking appointment: " + a.getAppointmentId() + 
                                 " [Doctor Match: " + isDoctor + 
                                 ", Status: " + a.getStatus() + 
                                 ", Is Future: " + isFuture + "]");

                return isDoctor && isPending && isFuture;
            })
            .collect(Collectors.toList());

        System.out.println("Found " + pendingAppointments.size() + " pending appointments");
        pendingAppointments.forEach(a -> 
            System.out.println("Pending appointment: " + a.getAppointmentId() + 
                             " at " + a.getAppointmentDate()));
        return pendingAppointments;
    }

    @Override
    public void acceptAppointment(String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            System.out.println("Invalid appointment ID provided");
            return;
        }

        System.out.println("Attempting to accept appointment: " + appointmentId);
        Appointment appointment = appointmentList.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            System.out.println("Appointment not found: " + appointmentId);
            return;
        }

        if (!"Pending".equalsIgnoreCase(appointment.getStatus())) {
            System.out.println("Cannot accept appointment with status: " + appointment.getStatus());
            return;
        }

        System.out.println("Current status: " + appointment.getStatus());
        appointment.confirm();
        appointmentList.saveAppointmentsToCSV("SC2002HMS/data/Appointments.csv");
        System.out.println("Appointment accepted successfully. New status: " + appointment.getStatus());
    }

    @Override
    public void declineAppointment(String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            System.out.println("Invalid appointment ID provided");
            return;
        }

        System.out.println("Attempting to decline appointment: " + appointmentId);
        Appointment appointment = appointmentList.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            System.out.println("Appointment not found: " + appointmentId);
            return;
        }

        if (!"Pending".equalsIgnoreCase(appointment.getStatus())) {
            System.out.println("Cannot decline appointment with status: " + appointment.getStatus());
            return;
        }

        System.out.println("Current status: " + appointment.getStatus());
        appointment.decline();
        appointmentList.saveAppointmentsToCSV("SC2002HMS/data/Appointments.csv");
        System.out.println("Appointment declined successfully. New status: " + appointment.getStatus());
    }

    @Override
    public List<Appointment> getUpcomingAppointments(String doctorId) {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            System.out.println("Invalid doctor ID provided");
            return List.of();
        }

        System.out.println("Fetching upcoming appointments for doctor: " + doctorId);
        LocalDateTime now = LocalDateTime.now();
        
        List<Appointment> appointments = appointmentList.getAllAppointments().stream()
            .filter(a -> {
                if (a == null || a.getDoctor() == null) {
                    return false;
                }
                
                boolean isDoctor = a.getDoctor().getId().equals(doctorId);
                boolean isConfirmed = "Confirmed".equalsIgnoreCase(a.getStatus());
                boolean isFuture = a.getAppointmentDate().isAfter(now);
                
                System.out.println("Checking appointment: " + a.getAppointmentId() + 
                                 " [IsDoctor: " + isDoctor + 
                                 ", Status: " + a.getStatus() + 
                                 ", IsFuture: " + isFuture + "]");
                
                return isDoctor && isConfirmed && isFuture;
            })
            .sorted((a1, a2) -> a1.getAppointmentDate().compareTo(a2.getAppointmentDate()))
            .collect(Collectors.toList());

        System.out.println("Found " + appointments.size() + " upcoming appointments");
        return appointments;
    }

    @Override
    public AppointmentOutcome recordAppointmentOutcome(String appointmentId,
            String serviceType,
            String consultationNotes,
            List<Prescription> prescriptions) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            System.out.println("Invalid appointment ID provided");
            return null;
        }

        System.out.println("Recording outcome for appointment: " + appointmentId);
        Appointment appointment = appointmentList.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            System.out.println("Appointment not found");
            return null;
        }

        if (!"Confirmed".equalsIgnoreCase(appointment.getStatus())) {
            System.out.println("Cannot record outcome for appointment with status: " + 
                             appointment.getStatus());
            return null;
        }

        try {
            // Update appointment
            appointment.setOutcome(serviceType, consultationNotes, prescriptions);
            appointmentList.saveAppointmentsToCSV("SC2002HMS/data/Appointments.csv");

            // Create outcome
            AppointmentOutcome outcome = new AppointmentOutcome(
                appointmentId,
                appointment.getPatient().getId(),
                appointment.getDoctor().getId(),
                appointment.getAppointmentDate(),
                serviceType,
                consultationNotes);

            // Add prescriptions
            if (prescriptions != null) {
                prescriptions.forEach(p -> {
                    if (p != null) {
                        outcome.addPrescription(p);
                    }
                });
            }

            // Save outcome
            if (outcomeService.saveAppointmentOutcome(outcome)) {
                System.out.println("Appointment outcome recorded successfully");
                return outcome;
            } else {
                System.out.println("Failed to save appointment outcome");
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error recording outcome: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<AppointmentOutcome> getCompletedAppointmentOutcomes(String doctorId) {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            System.out.println("Invalid doctor ID provided");
            return List.of();
        }

        System.out.println("Fetching completed appointment outcomes for doctor: " + doctorId);
        
        List<AppointmentOutcome> outcomes = appointmentList.getAllAppointments().stream()
            .filter(a -> a != null && 
                        a.getDoctor() != null && 
                        a.getDoctor().getId().equals(doctorId) &&
                        "Completed".equalsIgnoreCase(a.getStatus()))
            .map(a -> {
                AppointmentOutcome outcome = outcomeService.getOutcomeByAppointmentId(a.getAppointmentId());
                if (outcome == null) {
                    System.out.println("No outcome found for completed appointment: " + 
                                     a.getAppointmentId());
                }
                return outcome;
            })
            .filter(o -> o != null)
            .collect(Collectors.toList());

        System.out.println("Found " + outcomes.size() + " completed appointment outcomes");
        return outcomes;
    }
}