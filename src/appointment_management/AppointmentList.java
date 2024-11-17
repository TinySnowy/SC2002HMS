package appointment_management;

import user_management.UserController;
import user_management.Patient;
import user_management.Doctor;
import user_management.User;
import utils.CSVReaderUtil;
import utils.CSVWriterUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AppointmentList {
    private List<Appointment> appointments;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String APPOINTMENT_FILE_PATH = "SC2002HMS/data/Appointments.csv";

    public AppointmentList() {
        this.appointments = new ArrayList<>();
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        saveAppointmentsToCSV(APPOINTMENT_FILE_PATH);
    }

    public Appointment getAppointmentById(String appointmentId) {
        return appointments.stream()
                .filter(app -> app.getAppointmentId().equals(appointmentId))
                .findFirst()
                .orElse(null);
    }

    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments);
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

        boolean isFirstRow = true;
        for (String[] record : records) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            try {
                createAppointmentFromRecord(record, userController);
            } catch (Exception e) {
                System.err.println("Error loading appointment: " + e.getMessage());
            }
        }

        System.out.println("Appointments loaded successfully from " + filePath);
    }

    private void createAppointmentFromRecord(String[] record, UserController userController) {
        if (record.length < 5) {
            System.err.println("Invalid appointment record format");
            return;
        }

        String appointmentId = record[0];
        String patientId = record[1];
        String doctorId = record[2];
        LocalDateTime appointmentDate = LocalDateTime.parse(record[3], DATE_FORMATTER);
        String status = record[4];
        String serviceType = record.length > 5 ? record[5] : "";
        String consultationNotes = record.length > 6 ? record[6] : "";

        // Get patient and doctor from UserController
        User patientUser = userController.getUserById(patientId);
        User doctorUser = userController.getUserById(doctorId);

        if (!(patientUser instanceof Patient) || !(doctorUser instanceof Doctor)) {
            System.err.println("Invalid patient or doctor ID in appointment record");
            return;
        }

        Patient patient = (Patient) patientUser;
        Doctor doctor = (Doctor) doctorUser;

        // Create and configure appointment
        Appointment appointment = new Appointment(appointmentId, patient, doctor, appointmentDate);
        appointment.setStatus(status);

        if (!serviceType.isEmpty() || !consultationNotes.isEmpty()) {
            appointment.setOutcome(serviceType, consultationNotes, new ArrayList<>());
        }

        addAppointment(appointment);
    }

    public void saveAppointmentsToCSV(String filePath) {
        CSVWriterUtil.writeCSV(filePath, writer -> {
            try {
                // Write header
                writer.write("AppointmentID,PatientID,DoctorID,Date,Status,ServiceType,ConsultationNotes\n");

                // Write appointment data with proper escaping for CSV
                for (Appointment appointment : appointments) {
                    String serviceType = appointment.getServiceType() != null ? escapeCSV(appointment.getServiceType())
                            : "";
                    String notes = appointment.getConsultationNotes() != null
                            ? escapeCSV(appointment.getConsultationNotes())
                            : "";

                    writer.write(String.format("%s,%s,%s,%s,%s,%s,%s\n",
                            appointment.getAppointmentId(),
                            appointment.getPatient().getId(),
                            appointment.getDoctor().getId(),
                            appointment.getAppointmentDate().format(DATE_FORMATTER),
                            appointment.getStatus(),
                            serviceType,
                            notes));
                }
            } catch (Exception e) {
                System.err.println("Error saving appointments: " + e.getMessage());
            }
        });
    }

    private String escapeCSV(String value) {
        if (value == null)
            return "";
        // Escape special characters and wrap in quotes if needed
        value = value.replace("\"", "\"\"");
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = "\"" + value + "\"";
        }
        return value;
    }

    // Get appointments for a specific patient
    public List<Appointment> getAppointmentsForPatient(String patientId) {
        return appointments.stream()
                .filter(app -> app.getPatient().getId().equals(patientId))
                .collect(java.util.stream.Collectors.toList());
    }

    // Get appointments for a specific doctor
    public List<Appointment> getAppointmentsForDoctor(String doctorId) {
        return appointments.stream()
                .filter(app -> app.getDoctor().getId().equals(doctorId))
                .collect(java.util.stream.Collectors.toList());
    }
}