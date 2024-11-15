package appointment_management;

import user_management.UserController;
import user_management.Patient;
import user_management.Doctor;
import utils.CSVReaderUtil;
import utils.CSVWriterUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AppointmentList {
    private List<Appointment> appointments;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AppointmentList() {
        this.appointments = new ArrayList<>();
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    public Appointment getAppointmentById(String appointmentId) {
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentId().equals(appointmentId)) {
                return appointment;
            }
        }
        return null;
    }

    public List<Appointment> getAllAppointments() {
        return appointments;
    }

    public List<Appointment> getAppointmentsByStatus(String status) {
        List<Appointment> filteredAppointments = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getStatus().equalsIgnoreCase(status)) {
                filteredAppointments.add(appointment);
            }
        }
        return filteredAppointments;
    }

    // Load appointments from CSV
    public void loadAppointmentsFromCSV(String filePath, UserController userController) {
        List<String[]> records = CSVReaderUtil.readCSV(filePath);

        appointments.clear(); // Clear existing data to avoid duplication
        boolean isFirstRow = true; // Skip header row
        for (String[] record : records) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            String appointmentId = record[0];
            String patientId = record[1];
            String doctorId = record[2];
            LocalDateTime appointmentDate = LocalDateTime.parse(record[3], DATE_FORMATTER);
            String status = record[4];
            String serviceType = record.length > 5 ? record[5] : "";
            String consultationNotes = record.length > 6 ? record[6] : "";

            // Retrieve patient and doctor objects using UserController
            Patient patient = userController.getPatientById(patientId);
            Doctor doctor = userController.getDoctorById(doctorId);

            // Create and configure appointment
            Appointment appointment = new Appointment(appointmentId, patient, doctor, appointmentDate);
            if (!serviceType.isEmpty() || !consultationNotes.isEmpty()) {
                appointment.setOutcome(serviceType, consultationNotes, new ArrayList<>());
            }
            appointment.setAppointmentDate(appointmentDate);
            appointment.setStatus(status); // Set status directly from CSV

            addAppointment(appointment);
        }

        System.out.println("Appointments loaded successfully from " + filePath);
    }

    // Save appointments to CSV
    public void saveAppointmentsToCSV(String filePath) {
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"AppointmentID", "PatientID", "DoctorID", "Date", "Status", "ServiceType", "ConsultationNotes"}); // Header

        for (Appointment appointment : appointments) {
            data.add(new String[]{
                    appointment.getAppointmentId(),
                    appointment.getPatient() != null ? appointment.getPatient().getId() : "Unknown",
                    appointment.getDoctor() != null ? appointment.getDoctor().getId() : "Unknown",
                    appointment.getAppointmentDate().format(DATE_FORMATTER),
                    appointment.getStatus(),
                    appointment.getServiceType() != null ? appointment.getServiceType() : "",
                    appointment.getConsultationNotes() != null ? appointment.getConsultationNotes() : ""
            });
        }

        CSVWriterUtil.writeCSV(filePath, data);
        System.out.println("Appointments saved successfully to " + filePath);
    }
}
