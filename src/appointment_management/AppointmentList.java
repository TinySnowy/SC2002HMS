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
import java.util.stream.Collectors;
import pharmacy_management.prescriptions.Prescription;

public class AppointmentList {
    private List<Appointment> appointments;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String APPOINTMENT_FILE_PATH = "SC2002HMS/data/Appointments.csv";
    private final UserController userController;

    public AppointmentList() {
        this.appointments = new ArrayList<>();
        this.userController = UserController.getInstance();
        loadAppointmentsFromCSV(APPOINTMENT_FILE_PATH);
        System.out.println("Loaded " + appointments.size() + " appointments"); // Debug
    }

    public void addAppointment(Appointment appointment) {
        if (appointment == null || appointment.getPatient() == null || appointment.getDoctor() == null) {
            System.err.println("Cannot add invalid appointment");
            return;
        }
        System.out.println("Adding appointment: " + appointment.getAppointmentId()); // Debug
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
        return appointments.stream()
                .filter(app -> app.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    public void loadAppointmentsFromCSV(String filePath) {
        List<String[]> records = CSVReaderUtil.readCSV(filePath);
        appointments.clear();
        System.out.println("Loading appointments from: " + filePath); // Debug

        boolean isFirstRow = true;
        for (String[] record : records) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            try {
                createAppointmentFromRecord(record);
            } catch (Exception e) {
                System.err.println("Error loading appointment: " + e.getMessage());
            }
        }
    }

    private void createAppointmentFromRecord(String[] record) {
        if (record.length < 5) {
            System.err.println("Invalid appointment record format");
            return;
        }

        try {
            String appointmentId = record[0].trim();
            String patientId = record[1].trim();
            String doctorId = record[2].trim();
            LocalDateTime appointmentDate = LocalDateTime.parse(record[3].trim(), DATE_FORMATTER);
            String status = record[4].trim();
            String serviceType = record.length > 5 ? record[5].trim() : "";
            String consultationNotes = record.length > 6 ? record[6].trim() : "";

            User patientUser = userController.getUserById(patientId);
            User doctorUser = userController.getUserById(doctorId);

            if (!(patientUser instanceof Patient)) {
                System.err.println("Invalid patient ID: " + patientId);
                return;
            }
            if (!(doctorUser instanceof Doctor)) {
                System.err.println("Invalid doctor ID: " + doctorId);
                return;
            }

            Patient patient = (Patient) patientUser;
            Doctor doctor = (Doctor) doctorUser;

            Appointment appointment = new Appointment(appointmentId, patient, doctor, appointmentDate);
            appointment.setStatus(status);
            
            if (!serviceType.isEmpty() || !consultationNotes.isEmpty()) {
                appointment.setOutcome(serviceType, consultationNotes, new ArrayList<>());
            }
            
            appointments.add(appointment);
            System.out.println("Loaded appointment: " + appointmentId + " for patient: " + 
                             patientId + " with doctor: " + doctorId); // Debug
        } catch (Exception e) {
            System.err.println("Error creating appointment from record: " + e.getMessage());
        }
    }

    public void saveAppointmentsToCSV(String filePath) {
        System.out.println("Saving " + appointments.size() + " appointments to: " + filePath); // Debug
        
        CSVWriterUtil.writeCSV(filePath, writer -> {
            try {
                writer.write("AppointmentID,PatientID,DoctorID,Date,Status,ServiceType,ConsultationNotes\n");

                for (Appointment appointment : appointments) {
                    if (appointment.getPatient() == null || appointment.getDoctor() == null) {
                        System.err.println("Skipping invalid appointment: " + appointment.getAppointmentId());
                        continue;
                    }

                    String serviceType = appointment.getServiceType() != null ? 
                        escapeCSV(appointment.getServiceType()) : "";
                    String notes = appointment.getConsultationNotes() != null ? 
                        escapeCSV(appointment.getConsultationNotes()) : "";

                    String line = String.format("%s,%s,%s,%s,%s,%s,%s",
                            appointment.getAppointmentId(),
                            appointment.getPatient().getId(),
                            appointment.getDoctor().getId(),
                            appointment.getAppointmentDate().format(DATE_FORMATTER),
                            appointment.getStatus(),
                            serviceType,
                            notes);
                            
                    writer.write(line + "\n");
                    System.out.println("Saved appointment: " + appointment.getAppointmentId()); // Debug
                }
            } catch (Exception e) {
                System.err.println("Error saving appointments: " + e.getMessage());
            }
        });
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        value = value.replace("\"", "\"\"");
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = "\"" + value + "\"";
        }
        return value;
    }

    public List<Appointment> getAppointmentsForPatient(String patientId) {
        System.out.println("Getting appointments for patient: " + patientId); // Debug
        List<Appointment> patientAppointments = appointments.stream()
            .filter(app -> app.getPatient() != null && app.getPatient().getId().equals(patientId))
            .peek(app -> System.out.println("Found appointment: " + app.getAppointmentId() + 
                        " Status: " + app.getStatus())) // Debug
            .collect(Collectors.toList());
            
        System.out.println("Found " + patientAppointments.size() + " appointments"); // Debug
        return patientAppointments;
    }

    public List<Appointment> getUpcomingAppointmentsForPatient(String patientId) {
        return appointments.stream()
            .filter(a -> a.getPatient() != null && 
                        a.getPatient().getId().equals(patientId) &&
                        "Confirmed".equalsIgnoreCase(a.getStatus()) &&
                        a.getAppointmentDate().isAfter(LocalDateTime.now()))
            .collect(Collectors.toList());
    }

    public List<Appointment> getAppointmentsForDoctor(String doctorId) {
        System.out.println("Getting appointments for doctor: " + doctorId); // Debug
        return appointments.stream()
                .filter(app -> app.getDoctor() != null && app.getDoctor().getId().equals(doctorId))
                .peek(app -> System.out.println("Found appointment: " + app.getAppointmentId() + 
                            " Status: " + app.getStatus())) // Debug
                .collect(Collectors.toList());
    }

    public void savePrescriptionsToCSV(String filePath, List<Prescription> prescriptions) {
        CSVWriterUtil.writeCSV(filePath, writer -> {
            try {
                writer.write("PrescriptionID,MedicationName,Dosage,Quantity,Status\n");
                for (Prescription prescription : prescriptions) {
                    writer.write(String.format("%s,%s,%s,%d,%s\n",
                            prescription.getPrescriptionId(),
                            escapeCSV(prescription.getMedicationName()),
                            escapeCSV(prescription.getDosage()),
                            prescription.getQuantity(),
                            escapeCSV(prescription.getStatus())));
                }
            } catch (Exception e) {
                System.err.println("Error saving prescriptions: " + e.getMessage());
            }
        });
    }

    public List<Appointment> getAppointmentsByDoctor(String doctorId) {
        return appointments.stream()
                .filter(apt -> apt.getDoctor() != null && apt.getDoctor().getId().equals(doctorId))
                .filter(apt -> !apt.getStatus().equalsIgnoreCase("Confirmed") && 
                             !apt.getStatus().equalsIgnoreCase("Booked"))
                .collect(Collectors.toList());
    }

    public List<Appointment> getAvailableAppointments() {
        return appointments.stream()
                .filter(apt -> !apt.getStatus().equalsIgnoreCase("Pending") &&
                             !apt.getStatus().equalsIgnoreCase("Confirmed") &&
                             apt.getStatus().equalsIgnoreCase("Available"))
                .collect(Collectors.toList());
    }

    public List<Appointment> getUpcomingAppointments(String doctorId) {
        return appointments.stream()
            .filter(apt -> apt.getDoctor() != null && apt.getDoctor().getId().equals(doctorId))
            .filter(apt -> apt.getStatus().equalsIgnoreCase("Confirmed"))
            .filter(apt -> apt.getAppointmentDate().isAfter(LocalDateTime.now()))
            .collect(Collectors.toList());
    }
}