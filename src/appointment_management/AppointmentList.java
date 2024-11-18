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
import pharmacy_management.prescriptions.Prescription;

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

    public void loadAppointmentsFromCSV(String filePath, UserController userController) {
        List<String[]> records = CSVReaderUtil.readCSV(filePath);
        appointments.clear();

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
    }

    private void createAppointmentFromRecord(String[] record, UserController userController) {
        if (record.length < 5) {
            System.err.println("Invalid appointment record format");
            return;
        }

        try {
            String appointmentId = record[0];
            String patientId = record[1];
            String doctorId = record[2];
            LocalDateTime appointmentDate = LocalDateTime.parse(record[3], DATE_FORMATTER);
            String status = record[4];
            String serviceType = record.length > 5 ? record[5] : "";
            String consultationNotes = record.length > 6 ? record[6] : "";

            User patientUser = userController.getUserById(patientId);
            User doctorUser = userController.getUserById(doctorId);

            if (!(patientUser instanceof Patient) || !(doctorUser instanceof Doctor)) {
                System.err.println("Invalid patient or doctor ID in appointment record");
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
        } catch (Exception e) {
            System.err.println("Error creating appointment from record: " + e.getMessage());
        }
    }

    public void saveAppointmentsToCSV(String filePath) {
        CSVWriterUtil.writeCSV(filePath, writer -> {
            try {
                writer.write("AppointmentID,PatientID,DoctorID,Date,Status,ServiceType,ConsultationNotes\n");

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
        value = value.replace("\"", "\"\"");
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = "\"" + value + "\"";
        }
        return value;
    }

    public List<Appointment> getAppointmentsForPatient(String patientId) {
        
        List<Appointment> patientAppointments = appointments.stream()
            .filter(app -> {
                System.out.println("Checking appointment: " + app.getAppointmentId() 
                    + " Patient ID: " + app.getPatient().getId());
                return app.getPatient().getId().equals(patientId);
            })
            .collect(java.util.stream.Collectors.toList());
        return patientAppointments;
    }

    public List<Appointment> getUpcomingAppointmentsForPatient(String patientId) {
        return appointments.stream()
            .filter(a -> a.getPatient() != null && 
                        a.getPatient().getId().equals(patientId) &&
                        "Confirmed".equalsIgnoreCase(a.getStatus()) &&
                        a.getAppointmentDate().isAfter(LocalDateTime.now()))
            .collect(java.util.stream.Collectors.toList());
    }

    public List<Appointment> getAppointmentsForDoctor(String doctorId) {
        return appointments.stream()
                .filter(app -> app.getDoctor().getId().equals(doctorId))
                .collect(java.util.stream.Collectors.toList());
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
}