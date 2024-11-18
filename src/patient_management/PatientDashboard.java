package patient_management;

import appointment_management.Appointment;
import appointment_management.AppointmentList;
import appointment_management.DoctorScheduleManager;
import user_management.*;
import pharmacy_management.appointments.AppointmentOutcomeService;
import pharmacy_management.appointments.AppointmentOutcome;
import admin_management.handlers.staff.StaffDisplayHandler;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class PatientDashboard {
    private final Patient patient;
    private final MedicalRecordController medicalRecordController;
    private final AppointmentList appointmentList;
    private final AppointmentOutcomeService appointmentOutcomeService;
    private final UserController userController;
    private final StaffDisplayHandler staffDisplayHandler;
    private final DoctorScheduleManager scheduleManager;
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public PatientDashboard(Patient patient, AppointmentList appointmentList, UserController userController) {
        this.patient = patient;
        this.appointmentList = appointmentList;
        this.userController = userController;
        this.medicalRecordController = new MedicalRecordController();
        this.appointmentOutcomeService = new AppointmentOutcomeService(userController);
        this.staffDisplayHandler = new StaffDisplayHandler(userController);
        this.scheduleManager = new DoctorScheduleManager();
        this.scanner = new Scanner(System.in);
    }

    public void showDashboard() {
        boolean running = true;
        while (running) {
            try {
                displayMenu();
                int choice = getValidatedInput("Enter your choice: ", 1, 7);
                running = handleMenuChoice(choice);
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    private void displayMenu() {
        System.out.println("\nPatient Dashboard - " + patient.getName());
        System.out.println("----------------------------------------");
        System.out.println("1. View Medical Records");
        System.out.println("2. View Appointments");
        System.out.println("3. Schedule New Appointment");
        System.out.println("4. Manage Existing Appointments");
        System.out.println("5. View Appointment Outcomes");
        System.out.println("6. Update Personal Information");
        System.out.println("7. Log Out");
        System.out.println("----------------------------------------");
    }

    private boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                viewMedicalRecords();
                return true;
            case 2:
                viewAppointments();
                return true;
            case 3:
                scheduleAppointment();
                return true;
            case 4:
                manageAppointments();
                return true;
            case 5:
                viewAppointmentOutcomes();
                return true;
            case 6:
                updatePersonalInfo();
                return true;
            case 7:
                System.out.println("Logging out of Patient Dashboard...");
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
                return true;
        }
    }

    private int getValidatedInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int input = scanner.nextInt();
                scanner.nextLine();
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf("Please enter a number between %d and %d%n", min, max);
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private void viewMedicalRecords() {
        MedicalRecord record = medicalRecordController.getRecordByPatientId(patient.getId());
        if (record == null) {
            System.out.println("No medical record found.");
            return;
        }
        System.out.println("\nMedical Record for " + patient.getName());
        System.out.println("----------------------------------------");
        System.out.println("Patient ID: " + record.getPatientId());
        System.out.println("Name: " + record.getPatientName());
        String diagnosis = record.getDiagnosis();
        if (diagnosis != null && !diagnosis.isEmpty()) {
            System.out.println("Diagnosis: " + diagnosis);
        } else {
            System.out.println("Diagnosis: None");
        }
        String prescription = record.getPrescription();
        if (prescription != null && !prescription.isEmpty()) {
            System.out.println("Prescription: " + prescription);
        } else {
            System.out.println("Prescription: None");
        }
        System.out.println("----------------------------------------");
    }

    private void viewAppointments() {
        List<Appointment> patientAppointments = appointmentList.getAppointmentsForPatient(patient.getId());
        if (patientAppointments.isEmpty()) {
            System.out.println("\nNo appointments found.");
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        System.out.println("\nYour Appointments");
        System.out.println("----------------------------------------");

        System.out.println("\nUpcoming Appointments:");
        boolean hasUpcoming = false;
        for (Appointment appointment : patientAppointments) {
            if (appointment.getAppointmentDate().isAfter(now) &&
                    !appointment.getStatus().equalsIgnoreCase("Cancelled")) {
                displayAppointmentDetails(appointment);
                hasUpcoming = true;
            }
        }
        if (!hasUpcoming) {
            System.out.println("No upcoming appointments.");
        }

        System.out.println("\nPast Appointments:");
        boolean hasPast = false;
        for (Appointment appointment : patientAppointments) {
            if (appointment.getAppointmentDate().isBefore(now) ||
                    appointment.getStatus().equalsIgnoreCase("Completed") ||
                    appointment.getStatus().equalsIgnoreCase("Cancelled")) {
                displayAppointmentDetails(appointment);
                hasPast = true;
            }
        }
        if (!hasPast) {
            System.out.println("No past appointments.");
        }
        System.out.println("----------------------------------------");
    }

    private void displayAppointmentDetails(Appointment appointment) {
        System.out.println("\nAppointment ID: " + appointment.getAppointmentId());
        System.out.println("Date: " + appointment.getAppointmentDate().format(DATE_FORMATTER));
        System.out.println("Status: " + appointment.getStatus());
        if (appointment.getDoctor() != null) {
            System.out.println("Doctor: " + appointment.getDoctor().getName());
        } else {
            System.out.println("Doctor: Not yet assigned");
        }
        if (appointment.getStatus().equalsIgnoreCase("Completed")) {
            AppointmentOutcome outcome = appointmentOutcomeService
                    .getOutcomeByAppointmentId(appointment.getAppointmentId());
            if (outcome != null) {
                System.out.println("Service Type: " + outcome.getServiceType());
                if (outcome.getPrescriptions() != null && !outcome.getPrescriptions().isEmpty()) {
                    System.out.println("Prescriptions Issued: Yes");
                }
            }
        }
        System.out.println("----------------------------------------");
    }

    private void scheduleAppointment() {
        try {
            System.out.println("\nSchedule New Appointment");
            System.out.println("----------------------------------------");

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

            System.out.print("\nEnter appointment date and time (yyyy-MM-dd HH:mm): ");
            String dateTimeStr = scanner.nextLine();
            LocalDateTime appointmentDateTime = LocalDateTime.parse(dateTimeStr, DATE_FORMATTER);

            if (appointmentDateTime.isBefore(LocalDateTime.now())) {
                System.out.println("Cannot schedule appointments in the past.");
                return;
            }

            if (!scheduleManager.isDoctorAvailable(doctorId, appointmentDateTime)) {
                System.out.println(
                        "Doctor is not available at this time. Please check the schedule and choose a valid time slot.");
                return;
            }

            if (!isDoctorAvailable(selectedDoctor, appointmentDateTime)) {
                System.out.println("Doctor already has an appointment at this time. Please choose another time.");
                return;
            }

            String appointmentId = "A" + System.currentTimeMillis();
            Appointment appointment = new Appointment(appointmentId, patient, selectedDoctor, appointmentDateTime);
            appointmentList.addAppointment(appointment);

            System.out.println("\nAppointment scheduled successfully!");
            System.out.println("Appointment ID: " + appointmentId);
            System.out.println(
                    "Doctor: " + selectedDoctor.getName() + " (Specialty: " + selectedDoctor.getSpecialty() + ")");
            System.out.println("Date/Time: " + appointmentDateTime.format(DATE_FORMATTER));
            System.out.println("Status: Pending");

        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format. Please use yyyy-MM-dd HH:mm");
        } catch (Exception e) {
            System.out.println("Error scheduling appointment: " + e.getMessage());
        }
    }

    private void displayAvailableDoctors() {
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

    private boolean isDoctorAvailable(Doctor doctor, LocalDateTime appointmentDateTime) {
        List<Appointment> doctorAppointments = appointmentList.getAppointmentsForDoctor(doctor.getId());
        for (Appointment existingAppointment : doctorAppointments) {
            if (existingAppointment.getStatus().equals("Cancelled")) {
                continue;
            }
            LocalDateTime existingDateTime = existingAppointment.getAppointmentDate();
            LocalDateTime existingEndTime = existingDateTime.plusHours(1);
            LocalDateTime newEndTime = appointmentDateTime.plusHours(1);
            if ((appointmentDateTime.isEqual(existingDateTime) ||
                    appointmentDateTime.isAfter(existingDateTime)) &&
                    appointmentDateTime.isBefore(existingEndTime)) {
                return false;
            }
            if ((newEndTime.isAfter(existingDateTime) &&
                    newEndTime.isBefore(existingEndTime)) ||
                    newEndTime.isEqual(existingEndTime)) {
                return false;
            }
        }
        return true;
    }

    private void manageAppointments() {
        List<Appointment> patientAppointments = appointmentList.getAppointmentsForPatient(patient.getId());
        if (patientAppointments.isEmpty()) {
            System.out.println("No appointments found to manage.");
            return;
        }

        System.out.println("\nYour Appointments:");
        for (int i = 0; i < patientAppointments.size(); i++) {
            System.out.println((i + 1) + ". " + patientAppointments.get(i));
        }

        System.out.println("\nManage Appointments");
        System.out.println("1. Reschedule Appointment");
        System.out.println("2. Cancel Appointment");
        System.out.println("3. Back to Main Menu");

        int choice = getValidatedInput("Enter your choice: ", 1, 3);
        if (choice == 3)
            return;

        System.out.print("Enter the number of the appointment to manage: ");
        int appointmentIndex = getValidatedInput("", 1, patientAppointments.size()) - 1;
        Appointment selectedAppointment = patientAppointments.get(appointmentIndex);

        if (selectedAppointment.getStatus().equals("Completed") ||
                selectedAppointment.getStatus().equals("Cancelled")) {
            System.out.println("Cannot modify completed or cancelled appointments.");
            return;
        }

        switch (choice) {
            case 1:
                rescheduleAppointment(selectedAppointment);
                break;
            case 2:
                cancelAppointment(selectedAppointment);
                break;
        }
    }

    private void rescheduleAppointment(Appointment appointment) {
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

            if (newDateTime.isBefore(LocalDateTime.now())) {
                System.out.println("Cannot reschedule to a past date/time.");
                return;
            }

            if (!scheduleManager.isDoctorAvailable(doctor.getId(), newDateTime)) {
                System.out.println(
                        "Doctor is not available at this time. Please check the schedule and choose a valid time slot.");
                return;
            }

            if (!isDoctorAvailable(doctor, newDateTime)) {
                System.out.println("Doctor already has an appointment at this time. Please choose another time.");
                return;
            }

            appointment.setAppointmentDate(newDateTime);
            appointment.setStatus("Pending");
            appointmentList.saveAppointmentsToCSV("SC2002HMS/data/Appointments.csv");
            System.out.println("Appointment rescheduled successfully!");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format. Please use yyyy-MM-dd HH:mm");
        } catch (Exception e) {
            System.out.println("Error rescheduling appointment: " + e.getMessage());
        }
    }

    private void cancelAppointment(Appointment appointment) {
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

    private void viewAppointmentOutcomes() {
        System.out.println("\nAppointment Outcomes");
        System.out.println("----------------------------------------");
        List<Appointment> completedAppointments = appointmentList.getAppointmentsForPatient(patient.getId());
        if (completedAppointments.isEmpty()) {
            System.out.println("No appointment outcomes available.");
            return;
        }

        for (Appointment appointment : completedAppointments) {
            AppointmentOutcome outcome = appointmentOutcomeService
                    .getOutcomeByAppointmentId(appointment.getAppointmentId());
            if (outcome != null) {
                System.out.println("\nAppointment ID: " + outcome.getAppointmentId());
                System.out.println("Date: " + outcome.getAppointmentDate().format(DATE_FORMATTER));
                System.out.println("Service Type: " + outcome.getServiceType());
                System.out.println("Consultation Notes: " + outcome.getConsultationNotes());

                if (!outcome.getPrescriptions().isEmpty()) {
                    System.out.println("Prescriptions:");
                    outcome.getPrescriptions()
                            .forEach(prescription -> System.out.println("- " + prescription.getMedicationName() +
                                    " (" + prescription.getDosage() + ")"));
                }
                System.out.println("----------------------------------------");
            }
        }
    }

    private void updatePersonalInfo() {
        System.out.println("\nUpdate Personal Information");
        System.out.println("----------------------------------------");
        System.out.println("Current Information:");
        System.out.println("Name: " + patient.getName());
        System.out.println("Contact: " + patient.getContactInfo());
        System.out.println("Email: " + patient.getEmail());
        System.out.println("----------------------------------------");

        try {
            System.out.print("Enter new contact number (or press Enter to keep current): ");
            String newContact = scanner.nextLine().trim();
            if (!newContact.isEmpty()) {
                patient.setContactInfo(newContact);
            }

            System.out.print("Enter new email (or press Enter to keep current): ");
            String newEmail = scanner.nextLine().trim();
            if (!newEmail.isEmpty()) {
                patient.setEmail(newEmail);
            }

            userController.updateUser(patient);
            System.out.println("Personal information updated successfully!");

        } catch (Exception e) {
            System.err.println("Error updating personal information: " + e.getMessage());
        }
    }

    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}