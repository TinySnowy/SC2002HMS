package doctor_management;

import doctor_management.interfaces.*;
import doctor_management.services.*;
import patient_management.controllers.MedicalRecordController;
import appointment_management.AppointmentList;
import appointment_management.Appointment;
import pharmacy_management.appointments.AppointmentOutcome;
import pharmacy_management.appointments.IAppointmentOutcomeService;
import pharmacy_management.prescriptions.Prescription;
import doctor_management.services.ScheduleManagerImpl.ScheduleEntry;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Facade pattern implementation for doctor management operations.
 * Provides a simplified interface for:
 * - Medical record management
 * - Appointment handling
 * - Schedule management
 * Acts as a unified entry point for all doctor-related operations.
 * Coordinates between different subsystems while hiding implementation complexity.
 */
public class DoctorManagementFacade {
    /** Manager for medical record operations */
    private final IMedicalRecordManager medicalRecordManager;
    
    /** Manager for appointment operations */
    private final IAppointmentManager appointmentManager;
    
    /** Manager for schedule operations */
    private final IScheduleManager scheduleManager;
    
    /** List of system appointments */
    private final AppointmentList appointmentList;

    /**
     * Constructs the facade with necessary service dependencies.
     * Initializes all required managers and loads appointment data.
     * 
     * @param recordController Controller for medical records
     * @param appointmentList System appointment list
     * @param outcomeService Service for appointment outcomes
     */
    public DoctorManagementFacade(MedicalRecordController recordController,
            AppointmentList appointmentList,
            IAppointmentOutcomeService outcomeService) {
        this.appointmentList = appointmentList;
        // Load appointments first
        this.appointmentList.loadAppointmentsFromCSV("SC2002HMS/data/Appointments.csv");
        
        this.medicalRecordManager = new MedicalRecordManagerImpl(recordController);
        this.appointmentManager = new AppointmentManagerImpl(appointmentList, outcomeService);
        this.scheduleManager = new ScheduleManagerImpl();
        
        System.out.println("DoctorManagementFacade initialized");
        System.out.println("Total appointments loaded: " + appointmentList.getAllAppointments().size());
    }

    /**
     * Views a patient's medical record.
     * Validates doctor's access rights before displaying.
     * 
     * @param doctorId ID of requesting doctor
     * @param patientId ID of patient whose record to view
     */
    public void viewPatientRecord(String doctorId, String patientId) {
        if (medicalRecordManager.hasAccessToRecord(doctorId, patientId)) {
            medicalRecordManager.viewMedicalRecord(patientId);
        } else {
            System.out.println("Patient record not found. Please check the Patient ID.");
        }
    }

    /**
     * Updates a patient's medical record.
     * Validates doctor's access rights before allowing update.
     * 
     * @param doctorId ID of doctor making update
     * @param patientId ID of patient whose record to update
     * @param diagnosis New diagnosis information
     * @param prescription New prescription information
     */
    public void updatePatientRecord(String doctorId, String patientId,
            String diagnosis, String prescription) {
        if (medicalRecordManager.hasAccessToRecord(doctorId, patientId)) {
            medicalRecordManager.updateMedicalRecord(patientId, diagnosis, prescription, doctorId);
        } else {
            System.out.println("Patient record not found. Please check the Patient ID.");
        }
    }

    /**
     * Sets doctor's availability schedule.
     * Updates or creates new schedule entries.
     * 
     * @param doctorId ID of the doctor
     * @param scheduleEntries List of schedule entries
     */
    public void setDoctorAvailability(String doctorId, List<ScheduleEntry> scheduleEntries) {
        scheduleManager.setAvailability(doctorId, scheduleEntries);
    }

    /**
     * Retrieves doctor's availability schedule.
     * 
     * @param doctorId ID of the doctor
     * @return List of schedule entries
     */
    public List<ScheduleEntry> getDoctorAvailability(String doctorId) {
        return scheduleManager.getAvailability(doctorId);
    }

    /**
     * Checks if doctor is available at specific time.
     * 
     * @param doctorId ID of the doctor
     * @param dateTime Date and time to check
     * @return true if available, false otherwise
     */
    public boolean isDoctorAvailable(String doctorId, LocalDateTime dateTime) {
        return scheduleManager.isAvailable(doctorId, dateTime);
    }

    /**
     * Validates time format for scheduling.
     * 
     * @param time Time string to validate
     * @return true if valid format, false otherwise
     */
    public boolean validateTimeFormat(String time) {
        return scheduleManager.validateTimeSlot(time);
    }

    /**
     * Validates date format for scheduling.
     * 
     * @param date Date string to validate
     * @return true if valid format, false otherwise
     */
    public boolean validateDateFormat(String date) {
        return scheduleManager.validateDate(date);
    }

    /**
     * Retrieves pending appointments for doctor.
     * 
     * @param doctorId ID of the doctor
     * @return List of pending appointments
     */
    public List<Appointment> viewPendingAppointments(String doctorId) {
        return appointmentManager.viewPendingAppointments(doctorId);
    }

    /**
     * Accepts a pending appointment.
     * 
     * @param doctorId ID of accepting doctor
     * @param appointmentId ID of appointment to accept
     */
    public void acceptAppointment(String doctorId, String appointmentId) {
        appointmentManager.acceptAppointment(appointmentId);
    }

    /**
     * Declines a pending appointment.
     * 
     * @param doctorId ID of declining doctor
     * @param appointmentId ID of appointment to decline
     */
    public void declineAppointment(String doctorId, String appointmentId) {
        appointmentManager.declineAppointment(appointmentId);
    }

    /**
     * Records outcome of completed appointment.
     * 
     * @param doctorId ID of doctor recording outcome
     * @param appointmentId ID of completed appointment
     * @param serviceType Type of service provided
     * @param consultationNotes Notes from consultation
     * @param prescriptions List of prescriptions
     * @return Outcome of the completed appointment
     */
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

    /**
     * Retrieves completed appointments for doctor.
     * 
     * @param doctorId ID of the doctor
     * @return List of completed appointment outcomes
     */
    public List<AppointmentOutcome> viewCompletedAppointments(String doctorId) {
        return appointmentManager.getCompletedAppointmentOutcomes(doctorId);
    }

    /**
     * Retrieves upcoming appointments for doctor.
     * 
     * @param doctorId ID of the doctor
     * @return List of upcoming appointments
     */
    public List<Appointment> getUpcomingAppointments(String doctorId) {
        return appointmentManager.getUpcomingAppointments(doctorId);
    }
}