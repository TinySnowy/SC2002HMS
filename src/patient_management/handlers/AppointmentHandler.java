package patient_management.handlers;

import appointment_management.*;
import patient_management.interfaces.IAppointmentHandler;
import user_management.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class AppointmentHandler implements IAppointmentHandler {
    private final Patient patient;
    private final AppointmentList appointmentList;
    private final UserController userController;
    private final DoctorScheduleManager scheduleManager;
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AppointmentHandler(Patient patient, AppointmentList appointmentList, 
                            UserController userController, DoctorScheduleManager scheduleManager) {
        this.patient = patient;
        this.appointmentList = appointmentList;
        this.userController = userController;
        this.scheduleManager = scheduleManager;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void scheduleAppointment() {
        try {
            displayAvailableDoctors();

            System.out.print("\nEnter the Doctor ID you wish to schedule with (or 'cancel' to abort): ");
            String doctorId = scanner.nextLine().trim();

            if (doctorId.equalsIgnoreCase("cancel")) {
                System.out.println("Appointment scheduling cancelled.");
                return;
            }

            User doctorUser = userController.getUserById(doctorId);
            if (doctorUser == null || !(doctorUser instanceof Doctor)) {
                System.out.println("Invalid doctor ID. Please try again.");
                return;
            }
            Doctor selectedDoctor = (Doctor) doctorUser;

            // Get and display available time slots
            List<String> availableDays = scheduleManager.getAvailableDays(doctorId);
            if (availableDays.isEmpty()) {
                System.out.println("This doctor has no available time slots.");
                return;
            }

            System.out.println("\nDoctor's Schedule:");
            for (String day : availableDays) {
                String timeSlot = scheduleManager.getTimeSlot(doctorId, day);
                System.out.printf("%s: %s hours\n", day, timeSlot);
            }

            // Get appointment time
            System.out.print("\nEnter appointment date and time (yyyy-MM-dd HH:mm): ");
            String dateTimeStr = scanner.nextLine();
            LocalDateTime appointmentDateTime = LocalDateTime.parse(dateTimeStr, DATE_FORMATTER);

            // Validate appointment time
            if (!validateAppointmentTime(appointmentDateTime, selectedDoctor)) {
                return;
            }

            // Create and save appointment
            String appointmentId = "A" + System.currentTimeMillis();
            Appointment appointment = new Appointment(appointmentId, patient, selectedDoctor, appointmentDateTime);
            appointmentList.addAppointment(appointment);

            // Confirm appointment
            confirmAppointment(appointment);

        } catch (Exception e) {
            System.out.println("Error scheduling appointment: " + e.getMessage());
        }
    }

    @Override
    public void rescheduleAppointment(Appointment appointment) {
        try {
            Doctor doctor = appointment.getDoctor();
            List<String> availableDays = scheduleManager.getAvailableDays(doctor.getId());

            System.out.println("\nDoctor's Schedule:");
            for (String day : availableDays) {
                String timeSlot = scheduleManager.getTimeSlot(doctor.getId(), day);
                System.out.printf("%s: %s hours\n", day, timeSlot);
            }

            System.out.print("Enter new appointment date and time (yyyy-MM-dd HH:mm): ");
            String newDateTimeStr = scanner.nextLine();
            LocalDateTime newDateTime = LocalDateTime.parse(newDateTimeStr, DATE_FORMATTER);

            if (!validateAppointmentTime(newDateTime, doctor)) {
                return;
            }

            appointment.setAppointmentDate(newDateTime);
            appointment.setStatus("Pending");
            appointmentList.saveAppointmentsToCSV("SC2002HMS/data/Appointments.csv");
            System.out.println("Appointment rescheduled successfully!");

        } catch (Exception e) {
            System.out.println("Error rescheduling appointment: " + e.getMessage());
        }
    }

    @Override
    public void cancelAppointment(Appointment appointment) {
        System.out.print("Are you sure you want to cancel this appointment? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        if (confirm.equals("Y")) {
            appointment.decline();
            appointmentList.saveAppointmentsToCSV("SC2002HMS/data/Appointments.csv");
            System.out.println("Appointment cancelled successfully.");
        } else {
            System.out.println("Cancellation aborted.");
        }
    }

    @Override
    public void displayAvailableDoctors() {
        System.out.println("\nAvailable Doctors:");
        System.out.println("----------------------------------------");
        List<User> allStaff = userController.getAllUsers();
        List<User> doctors = allStaff.stream()
                .filter(user -> user instanceof Doctor)
                .filter(user -> !scheduleManager.getAvailableDays(user.getId()).isEmpty())
                .toList();

        if (doctors.isEmpty()) {
            System.out.println("No doctors available at the moment.");
            return;
        }

        printDoctorsTable(doctors);
    }

    @Override
    public boolean isDoctorAvailable(Doctor doctor, LocalDateTime appointmentDateTime) {
        List<Appointment> doctorAppointments = appointmentList.getAppointmentsForDoctor(doctor.getId());
        for (Appointment existingAppointment : doctorAppointments) {
            if (existingAppointment.getStatus().equals("Cancelled")) {
                continue;
            }
            if (isTimeSlotOverlapping(appointmentDateTime, existingAppointment.getAppointmentDate())) {
                return false;
            }
        }
        return true;
    }

    private boolean validateAppointmentTime(LocalDateTime dateTime, Doctor doctor) {
        if (dateTime.isBefore(LocalDateTime.now())) {
            System.out.println("Cannot schedule appointments in the past.");
            return false;
        }

        if (!scheduleManager.isDoctorAvailable(doctor.getId(), dateTime)) {
            System.out.println("Doctor is not available at this time. Please check the schedule and choose a valid time slot.");
            return false;
        }

        if (!isDoctorAvailable(doctor, dateTime)) {
            System.out.println("Doctor already has an appointment at this time. Please choose another time.");
            return false;
        }

        return true;
    }

    private boolean isTimeSlotOverlapping(LocalDateTime newTime, LocalDateTime existingTime) {
        LocalDateTime existingEndTime = existingTime.plusHours(1);
        LocalDateTime newEndTime = newTime.plusHours(1);

        return (newTime.isEqual(existingTime) || newTime.isAfter(existingTime)) && newTime.isBefore(existingEndTime) ||
               (newEndTime.isAfter(existingTime) && newEndTime.isBefore(existingEndTime)) ||
               newEndTime.isEqual(existingEndTime);
    }

    private void confirmAppointment(Appointment appointment) {
        System.out.println("\nAppointment scheduled successfully!");
        System.out.println("Appointment ID: " + appointment.getAppointmentId());
        System.out.println("Doctor: " + appointment.getDoctor().getName() + 
                         " (Specialty: " + appointment.getDoctor().getSpecialty() + ")");
        System.out.println("Date/Time: " + appointment.getAppointmentDate().format(DATE_FORMATTER));
        System.out.println("Status: Pending");
    }

    private void printDoctorsTable(List<User> doctors) {
        int[] columnWidths = { 8, 25, 25 };
        String[] headers = { "ID", "Name", "Specialty" };
        printTableRow(headers, columnWidths);
        System.out.println("-".repeat(60));

        for (User user : doctors) {
            Doctor doctor = (Doctor) user;
            String[] rowData = {
                doctor.getId(),
                doctor.getName(),
                doctor.getSpecialty()
            };
            printTableRow(rowData, columnWidths);
        }
        System.out.println("-".repeat(60));
    }

    private void printTableRow(String[] data, int[] widths) {
        StringBuilder row = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            row.append(String.format("%-" + widths[i] + "s", data[i]));
            if (i < data.length - 1) {
                row.append(" | ");
            }
        }
        System.out.println(row.toString());
    }
}