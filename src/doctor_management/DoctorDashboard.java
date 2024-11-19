package doctor_management;

import appointment_management.Appointment;
import appointment_management.AppointmentList;
import patient_management.controllers.MedicalRecordController;
import pharmacy_management.appointments.AppointmentOutcome;
import pharmacy_management.appointments.IAppointmentOutcomeService;
import pharmacy_management.prescriptions.Prescription;
import user_management.Doctor;
import doctor_management.services.ScheduleManagerImpl.TimeSlot;
import doctor_management.services.ScheduleManagerImpl.ScheduleEntry;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DoctorDashboard implements AutoCloseable {
    private final Doctor doctor;
    private final DoctorManagementFacade doctorManager;
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public DoctorDashboard(Doctor doctor, AppointmentList appointmentList,
            IAppointmentOutcomeService outcomeService) {
        this.doctor = doctor;
        this.doctorManager = new DoctorManagementFacade(
                new MedicalRecordController(),
                appointmentList,
                outcomeService);
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
        System.out.println("\nDoctor Dashboard - Dr. " + doctor.getName());
        System.out.println("----------------------------------------");
        System.out.println("1. View Patient Medical Records");
        System.out.println("2. Update Patient Medical Records");
        System.out.println("3. View Schedule");
        System.out.println("4. Set Availability for Appointments");
        System.out.println("5. Accept/Decline Appointment Requests");
        System.out.println("6. Record Appointment Outcome");
        System.out.println("7. Logout");
        System.out.println("----------------------------------------");
    }

    private boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                viewPatientMedicalRecord();
                return true;
            case 2:
                updatePatientMedicalRecord();
                return true;
            case 3:
                viewPersonalSchedule();
                return true;
            case 4:
                setAvailability();
                return true;
            case 5:
                manageAppointmentRequests();
                return true;
            case 6:
                recordAppointmentOutcome();
                return true;
            case 7:
                System.out.println("Logging out of Doctor Dashboard...");
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
                scanner.nextLine(); // Consume newline
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

    private void setAvailability() {
        System.out.println("\nSet Availability");
        System.out.println("----------------------------------------");

        List<ScheduleEntry> scheduleEntries = new ArrayList<>();
        boolean continueAdding = true;

        while (continueAdding) {
            ScheduleEntry entry = getValidatedScheduleEntry();
            if (entry != null) {
                scheduleEntries.add(entry);
                System.out.println("\nSchedule slot added successfully!");
            }

            System.out.print("\nWould you like to add another availability slot? (Y/N): ");
            continueAdding = scanner.nextLine().trim().equalsIgnoreCase("Y");
        }

        if (!scheduleEntries.isEmpty()) {
            doctorManager.setDoctorAvailability(doctor.getId(), scheduleEntries);
            System.out.println("\nAvailability updated successfully!");
            displayAvailableTimeSlots(scheduleEntries);
        } else {
            System.out.println("\nNo availability set.");
        }
    }

    private ScheduleEntry getValidatedScheduleEntry() {
        while (true) {
            try {
                // Get and validate date
                System.out.println("\nEnter the date (YYYY-MM-DD): ");
                String date = scanner.nextLine().trim();
                LocalDate scheduleDate = validateAndParseDate(date);
                if (scheduleDate == null) continue;

                // Get and validate start time
                System.out.println("\nChoose a start time.");
                System.out.println("Note: Time must be in a half hour interval (e.g. 09:00, 09:30, etc).");
                System.out.print("Please enter a time (HH:MM): ");
                String startTime = scanner.nextLine().trim();
                if (!validateTime(startTime)) continue;

                // Get and validate end time
                System.out.println("\nChoose an end time.");
                System.out.println("Note: Time must be in a half hour interval (e.g. 09:00, 09:30, etc).");
                System.out.print("Please enter a time (HH:MM): ");
                String endTime = scanner.nextLine().trim();
                if (!validateTime(endTime)) continue;

                return new ScheduleEntry(scheduleDate, new TimeSlot(startTime, endTime));

            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.print("Would you like to try again? (Y/N): ");
                if (!scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                    return null;
                }
            }
        }
    }

    private LocalDate validateAndParseDate(String date) {
        try {
            LocalDate scheduleDate = LocalDate.parse(date, DATE_FORMATTER);
            if (scheduleDate.isBefore(LocalDate.now())) {
                System.out.println("Cannot set availability for past dates.");
                return null;
            }
            return scheduleDate;
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
            return null;
        }
    }

    private boolean validateTime(String time) {
        try {
            if (!time.matches("^([0-1][0-9]|2[0-3]):[0-5][0-9]$")) {
                System.out.println("Invalid time format. Please use HH:MM format (24-hour).");
                return false;
            }
            return true;
        } catch (Exception e) {
            System.out.println("Invalid time format. Please use HH:MM format.");
            return false;
        }
    }
    private void viewPersonalSchedule() {
        System.out.println("\nDoctor's Schedule - Dr. " + doctor.getName());
        System.out.println("============================================");

        // Get both upcoming appointments and availability
        List<Appointment> upcomingAppointments = doctorManager.getUpcomingAppointments(doctor.getId());
        List<ScheduleEntry> availableSlots = doctorManager.getDoctorAvailability(doctor.getId());

        // Display Upcoming Appointments Section
        displayUpcomingAppointments(upcomingAppointments);

        // Display Available Time Slots Section
        displayAvailableTimeSlots(availableSlots);

        System.out.println("============================================");
    }

    private void displayUpcomingAppointments(List<Appointment> appointments) {
        System.out.println("\nUPCOMING APPOINTMENTS");
        System.out.println("--------------------------------------------");
        
        if (appointments.isEmpty()) {
            System.out.println("No upcoming appointments scheduled.");
            return;
        }

        // Group appointments by date
        Map<LocalDate, List<Appointment>> appointmentsByDate = new TreeMap<>();
        appointments.forEach(apt -> {
            LocalDate date = apt.getAppointmentDate().toLocalDate();
            appointmentsByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(apt);
        });

        // Display appointments grouped by date
        appointmentsByDate.forEach((date, dateAppointments) -> {
            System.out.printf("\n%s (%s):\n", 
                date.format(DateTimeFormatter.ofPattern("EEEE")),
                date.format(DATE_FORMATTER));

            dateAppointments.stream()
                .sorted(Comparator.comparing(Appointment::getAppointmentDate))
                .forEach(apt -> {
                    System.out.printf("  Time: %s\n", 
                        apt.getAppointmentDate().format(TIME_FORMATTER));
                    System.out.printf("  Patient: %s (ID: %s)\n", 
                        apt.getPatient().getName(),
                        apt.getPatient().getId());
                    System.out.printf("  Appointment ID: %s\n", 
                        apt.getAppointmentId());
                    System.out.println("  --------------------");
                });
        });
    }

    private void displayAvailableTimeSlots(List<ScheduleEntry> schedule) {
        System.out.println("\nAVAILABLE TIME SLOTS");
        System.out.println("--------------------------------------------");

        if (schedule.isEmpty()) {
            System.out.println("No availability set for future dates.");
            return;
        }

        // Sort schedule entries by date and time
        schedule.sort((e1, e2) -> {
            int dateCompare = e1.getDate().compareTo(e2.getDate());
            if (dateCompare == 0) {
                return e1.getTimeSlot().getStartTime().compareTo(e2.getTimeSlot().getEndTime());
            }
            return dateCompare;
        });

        // Group time slots by date
        Map<LocalDate, List<TimeSlot>> slotsByDate = new TreeMap<>();
        schedule.forEach(entry -> {
            if (!entry.getDate().isBefore(LocalDate.now())) {
                slotsByDate.computeIfAbsent(entry.getDate(), k -> new ArrayList<>())
                    .add(entry.getTimeSlot());
            }
        });

        // Display time slots grouped by date
        slotsByDate.forEach((date, timeSlots) -> {
            System.out.printf("\n%s (%s):\n",
                date.format(DateTimeFormatter.ofPattern("EEEE")),
                date.format(DATE_FORMATTER));

            timeSlots.stream()
                .sorted(Comparator.comparing(TimeSlot::getStartTime))
                .forEach(slot -> 
                    System.out.printf("  %s - %s\n",
                        slot.getStartTime(),
                        slot.getEndTime()));
        });
    }


    private void viewPatientMedicalRecord() {
        System.out.print("Enter Patient ID (or 'back' to return): ");
        String patientId = scanner.nextLine();

        if (patientId.equalsIgnoreCase("back")) {
            return;
        }

        doctorManager.viewPatientRecord(doctor.getId(), patientId);
    }

    private void updatePatientMedicalRecord() {
        System.out.print("Enter Patient ID (or 'back' to return): ");
        String patientId = scanner.nextLine();

        if (patientId.equalsIgnoreCase("back")) {
            return;
        }

        // First view current record
        doctorManager.viewPatientRecord(doctor.getId(), patientId);

        System.out.print("\nEnter new diagnosis (or press Enter to keep current): ");
        String diagnosis = scanner.nextLine().trim();

        System.out.print("Enter new prescription (or press Enter to keep current): ");
        String prescription = scanner.nextLine().trim();

        if (!diagnosis.isEmpty() || !prescription.isEmpty()) {
            doctorManager.updatePatientRecord(doctor.getId(), patientId, diagnosis, prescription);
            System.out.println("Medical record updated successfully.");
        } else {
            System.out.println("No changes made to the medical record.");
        }
    }

    private void manageAppointmentRequests() {
        while (true) {
            System.out.println("\nPending Appointments:");
            System.out.println("----------------------------------------");
            List<Appointment> appointments = doctorManager.viewPendingAppointments(doctor.getId());

            if (appointments.isEmpty()) {
                System.out.println("No pending appointments found.");
                return;
            }

            appointments.forEach(System.out::println);
            System.out.println("----------------------------------------");

            System.out.print("Enter Appointment ID to manage (or 'back' to return): ");
            String appointmentId = scanner.nextLine();

            if (appointmentId.equalsIgnoreCase("back")) {
                return;
            }

            System.out.println("1. Accept Appointment");
            System.out.println("2. Decline Appointment");
            int choice = getValidatedInput("Enter your choice: ", 1, 2);

            try {
                switch (choice) {
                    case 1:
                        doctorManager.acceptAppointment(doctor.getId(), appointmentId);
                        System.out.println("Appointment accepted successfully.");
                        break;
                    case 2:
                        doctorManager.declineAppointment(doctor.getId(), appointmentId);
                        System.out.println("Appointment declined successfully.");
                        break;
                }
            } catch (Exception e) {
                System.err.println("Error managing appointment: " + e.getMessage());
            }
        }
    }

    private void recordAppointmentOutcome() {
        System.out.print("Enter Appointment ID (or 'back' to return): ");
        String appointmentId = scanner.nextLine();

        if (appointmentId.equalsIgnoreCase("back")) {
            return;
        }

        try {
            System.out.print("Enter Service Type: ");
            String serviceType = scanner.nextLine().trim();

            System.out.print("Enter Consultation Notes: ");
            String consultationNotes = scanner.nextLine().trim();

            if (serviceType.isEmpty() || consultationNotes.isEmpty()) {
                System.out.println("Service type and consultation notes are required.");
                return;
            }

            List<Prescription> prescriptions = new ArrayList<>();
            while (true) {
                System.out.print("\nAdd prescription? (yes/no): ");
                if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                    break;
                }

                System.out.print("Medication Name: ");
                String medicationName = scanner.nextLine().trim();

                System.out.print("Dosage (e.g., 500mg twice daily): ");
                String dosage = scanner.nextLine().trim();

                int quantity = getValidatedInput("Quantity: ", 1, 100);

                String prescriptionId = appointmentId + "-" + prescriptions.size();
                prescriptions.add(new Prescription(
                        prescriptionId,
                        null,
                        medicationName,
                        dosage,
                        quantity));
            }

            AppointmentOutcome outcome = doctorManager.recordAppointmentOutcome(
                    doctor.getId(),
                    appointmentId,
                    serviceType,
                    consultationNotes,
                    prescriptions);

            if (outcome != null) {
                System.out.println("\nAppointment outcome recorded successfully.");
                System.out.println("Service Type: " + serviceType);
                System.out.println("Notes: " + consultationNotes);
                if (!prescriptions.isEmpty()) {
                    System.out.println("\nPrescriptions:");
                    prescriptions.forEach(p -> System.out.println("- " + p.getMedicationName() +
                            " (" + p.getDosage() + ")"));
                }
            }
        } catch (Exception e) {
            System.err.println("Error recording outcome: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}