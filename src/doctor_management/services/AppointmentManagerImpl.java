package doctor_management.services;

import doctor_management.interfaces.IAppointmentManager;
import appointment_management.Appointment;
import appointment_management.AppointmentList;
import pharmacy_management.appointments.AppointmentOutcome;
import pharmacy_management.appointments.IAppointmentOutcomeService;
import pharmacy_management.prescriptions.Prescription;
import utils.CSVWriterUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
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
        System.out.println("AppointmentManagerImpl initialized");
    }

    @Override
    public List<Appointment> viewPendingAppointments(String doctorId) {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            System.out.println("Invalid doctor ID provided");
            return new ArrayList<>();
        }

        System.out.println("Fetching pending appointments for doctor: " + doctorId);
        
        List<Appointment> allAppointments = appointmentList.getAppointmentsByDoctor(doctorId);
        System.out.println("Total appointments found for doctor: " + allAppointments.size());
        
        LocalDateTime now = LocalDateTime.now();

        List<Appointment> pendingAppointments = allAppointments.stream()
            .filter(appointment -> {
                if (appointment == null || appointment.getDoctor() == null) {
                    System.out.println("Found null appointment or doctor");
                    return false;
                }

                if (appointment.getAppointmentDate() == null) {
                    System.out.println("Appointment " + appointment.getAppointmentId() + " has null date");
                    return false;
                }

                boolean isPending = "Pending".equalsIgnoreCase(appointment.getStatus());
                boolean isFuture = appointment.getAppointmentDate().isAfter(now);

                System.out.println("Appointment " + appointment.getAppointmentId() + 
                                 " [Status: " + appointment.getStatus() + 
                                 ", Date: " + appointment.getAppointmentDate() + "]");

                return isPending && isFuture;
            })
            .sorted((a1, a2) -> a1.getAppointmentDate().compareTo(a2.getAppointmentDate()))
            .collect(Collectors.toList());

        System.out.println("Found " + pendingAppointments.size() + " pending appointments");
        return pendingAppointments;
    }

    @Override
    public List<Appointment> getUpcomingAppointments(String doctorId) {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            System.out.println("Invalid doctor ID provided");
            return new ArrayList<>();
        }

       
        LocalDateTime now = LocalDateTime.now();

        List<Appointment> doctorAppointments = appointmentList.getUpcomingAppointments(doctorId);
       

        List<Appointment> upcomingAppointments = doctorAppointments.stream()
            .filter(appointment -> {
                if (appointment == null) {
                    System.out.println("Found null appointment entry");
                    return false;
                }
                
                if (appointment.getAppointmentDate() == null) {
                    System.out.println("Appointment " + appointment.getAppointmentId() + " has null date");
                    return false;
                }

                boolean isConfirmed = "Confirmed".equalsIgnoreCase(appointment.getStatus());
                boolean isFuture = appointment.getAppointmentDate().isAfter(now);
                
                
                
                return isConfirmed && isFuture;
            })
            .sorted((a1, a2) -> a1.getAppointmentDate().compareTo(a2.getAppointmentDate()))
            .collect(Collectors.toList());

        System.out.println("Found " + upcomingAppointments.size() + " upcoming confirmed appointments");
        return upcomingAppointments;
    }

    @Override
    public void acceptAppointment(String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid appointment ID");
        }

        System.out.println("Attempting to accept appointment: " + appointmentId);
        Appointment appointment = appointmentList.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found: " + appointmentId);
        }

        if (!"Pending".equalsIgnoreCase(appointment.getStatus())) {
            throw new IllegalStateException("Cannot accept appointment with status: " + appointment.getStatus());
        }

        appointment.confirm();
        appointmentList.saveAppointmentsToCSV("SC2002HMS/data/Appointments.csv");
        System.out.println("Appointment accepted successfully. New status: " + appointment.getStatus());
    }

    @Override
    public void declineAppointment(String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid appointment ID");
        }

        System.out.println("Attempting to decline appointment: " + appointmentId);
        Appointment appointment = appointmentList.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found: " + appointmentId);
        }

        if (!"Pending".equalsIgnoreCase(appointment.getStatus())) {
            throw new IllegalStateException("Cannot decline appointment with status: " + appointment.getStatus());
        }

        appointment.decline();
        appointmentList.saveAppointmentsToCSV("SC2002HMS/data/Appointments.csv");
        System.out.println("Appointment declined successfully. New status: " + appointment.getStatus());
    }

    @Override
public AppointmentOutcome recordAppointmentOutcome(String appointmentId,
        String serviceType,
        String consultationNotes,
        List<Prescription> prescriptions) {
    if (appointmentId == null || appointmentId.trim().isEmpty()) {
        throw new IllegalArgumentException("Invalid appointment ID");
    }

    System.out.println("Recording outcome for appointment: " + appointmentId);
    Appointment appointment = appointmentList.getAppointmentById(appointmentId);
    
    if (appointment == null) {
        throw new IllegalArgumentException("Appointment not found");
    }

    if (!"Confirmed".equalsIgnoreCase(appointment.getStatus())) {
        throw new IllegalStateException("Cannot record outcome for appointment with status: " + 
                                      appointment.getStatus());
    }

    // Set the outcome first
    appointment.setOutcome(serviceType, consultationNotes, prescriptions);
    appointment.setStatus("Completed");
    
    // Create the appointment outcome
    AppointmentOutcome outcome = new AppointmentOutcome(
        appointmentId,
        appointment.getPatient().getId(),
        appointment.getDoctor().getId(),
        appointment.getAppointmentDate(),
        serviceType,
        consultationNotes);

    // Add prescriptions to the outcome
    if (prescriptions != null) {
        prescriptions.forEach(p -> {
            if (p != null) {
                outcome.addPrescription(p);
            }
        });
    }

    // Save prescriptions only once using appointmentList
    if (prescriptions != null && !prescriptions.isEmpty()) {
        appointmentList.savePrescriptionsToCSV("SC2002HMS/data/Prescriptions.csv", prescriptions);
    }

    // Save the updated appointment
    appointmentList.saveAppointmentsToCSV("SC2002HMS/data/Appointments.csv");

    // Save the outcome
    if (!outcomeService.saveAppointmentOutcome(outcome)) {
        throw new RuntimeException("Failed to save appointment outcome");
    }

    System.out.println("Appointment outcome recorded successfully");
    return outcome;
}

    @Override
    public List<AppointmentOutcome> getCompletedAppointmentOutcomes(String doctorId) {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            System.out.println("Invalid doctor ID provided");
            return new ArrayList<>();
        }

        System.out.println("Fetching completed appointment outcomes for doctor: " + doctorId);
        
        List<AppointmentOutcome> outcomes = appointmentList.getAppointmentsByDoctor(doctorId).stream()
            .filter(a -> a != null && 
                        a.getDoctor() != null && 
                        "Completed".equalsIgnoreCase(a.getStatus()))
            .map(a -> {
                AppointmentOutcome outcome = outcomeService.getOutcomeByAppointmentId(a.getAppointmentId());
                if (outcome == null) {
                    System.out.println("No outcome found for completed appointment: " + a.getAppointmentId());
                }
                return outcome;
            })
            .filter(o -> o != null)
            .collect(Collectors.toList());

        System.out.println("Found " + outcomes.size() + " completed appointment outcomes");
        return outcomes;
    }
}