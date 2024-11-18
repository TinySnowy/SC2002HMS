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
import java.util.stream.Collectors;

public class DoctorManagementFacade {
    private final IMedicalRecordManager medicalRecordManager;
    private final IAppointmentManager appointmentManager;
    private final IScheduleManager scheduleManager;
    private final AppointmentList appointmentList;

    public DoctorManagementFacade(MedicalRecordController recordController,
            AppointmentList appointmentList,
            IAppointmentOutcomeService outcomeService) {
        this.medicalRecordManager = new MedicalRecordManagerImpl(recordController);
        this.appointmentManager = new AppointmentManagerImpl(appointmentList, outcomeService);
        this.scheduleManager = new ScheduleManagerImpl();
        this.appointmentList = appointmentList;
    }

    // Medical Record Management
    public void viewPatientRecord(String doctorId, String patientId) {
        if (medicalRecordManager.hasAccessToRecord(doctorId, patientId)) {
            medicalRecordManager.viewMedicalRecord(patientId);
        } else {
            System.out.println("Patient record not found. Please check the Patient ID.");
        }
    }

    public void updatePatientRecord(String doctorId, String patientId,
            String diagnosis, String prescription) {
        if (medicalRecordManager.hasAccessToRecord(doctorId, patientId)) {
            medicalRecordManager.updateMedicalRecord(patientId, diagnosis, prescription);
        } else {
            System.out.println("Patient record not found. Please check the Patient ID.");
        }
    }

    // Appointment Management
    public List<Appointment> viewPendingAppointments(String doctorId) {
        return appointmentManager.viewPendingAppointments(doctorId);
    }

    public void acceptAppointment(String doctorId, String appointmentId) {
        appointmentManager.acceptAppointment(appointmentId);
    }

    public void declineAppointment(String doctorId, String appointmentId) {
        appointmentManager.declineAppointment(appointmentId);
    }

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

    public List<AppointmentOutcome> viewCompletedAppointments(String doctorId) {
        return appointmentManager.getCompletedAppointmentOutcomes(doctorId);
    }

    public List<Appointment> getUpcomingAppointments(String doctorId) {
        return appointmentList.getAppointmentsByDoctor(doctorId).stream()
                .filter(apt -> apt.getAppointmentDate().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    // Schedule Management with new methods
    public void setDoctorAvailability(String doctorId, List<ScheduleEntry> scheduleEntries) {
        scheduleManager.setAvailability(doctorId, scheduleEntries);
    }

    public List<ScheduleEntry> getDoctorAvailability(String doctorId) {
        return scheduleManager.getAvailability(doctorId);
    }

    public boolean isDoctorAvailable(String doctorId, LocalDateTime dateTime) {
        return scheduleManager.isAvailable(doctorId, dateTime);
    }

    public boolean validateTimeFormat(String time) {
        return scheduleManager.validateTimeSlot(time);
    }

    public boolean validateDateFormat(String date) {
        return scheduleManager.validateDate(date);
    }
}