package patient_management.handlers;

import appointment_management.*;
import patient_management.interfaces.IAppointmentHandler;
import user_management.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import doctor_management.services.ScheduleManagerImpl.ScheduleEntry;
import doctor_management.services.ScheduleManagerImpl.TimeSlot;

/**
 * Handles appointment-related operations for patients in the HMS.
 * Manages:
 * - Appointment scheduling
 * - Appointment rescheduling
 * - Appointment cancellation
 * - Doctor availability checking
 * - Time slot management
 * Provides interface between patients and appointment system.
 */
public class AppointmentHandler implements IAppointmentHandler {
    /** Currently active patient */
    private final Patient patient;
    
    /** System-wide appointment list */
    private final AppointmentList appointmentList;
    
    /** Controller for user management */
    private final UserController userController;
    
    /** Manager for doctor schedules */
    private final DoctorScheduleManager scheduleManager;
    
    /** Scanner for user input */
    private final Scanner scanner;
    
    /** Date-time formatter for appointments */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Constructs a new AppointmentHandler with required dependencies.
     * 
     * @param patient Active patient
     * @param appointmentList System appointment list
     * @param userController User management controller
     * @param scheduleManager Doctor schedule manager
     */
    public AppointmentHandler(Patient patient, AppointmentList appointmentList, 
                            UserController userController, DoctorScheduleManager scheduleManager) {
        this.patient = patient;
        this.appointmentList = appointmentList;
        this.userController = userController;
        this.scheduleManager = scheduleManager;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Handles the appointment scheduling process.
     * Steps:
     * 1. Get available doctors
     * 2. Display doctor list
     * 3. Select doctor
     * 4. Choose time slot
     * 5. Create appointment
     */
    @Override
    public void scheduleAppointment() {
        try {
            // Get available doctors
            List<User> availableDoctors = getAvailableDoctors();
            if (availableDoctors.isEmpty()) {
                System.out.println("No doctors are currently available for appointments.");
                return;
            }

            // Display available doctors
            displayAvailableDoctors();

            // Select doctor
            Doctor selectedDoctor = selectDoctor(availableDoctors);
            if (selectedDoctor == null) return;

            // Get available slots for selected doctor
            List<ScheduleEntry> availableSlots = scheduleManager.getAvailableSlots(selectedDoctor.getId());
            if (availableSlots.isEmpty()) {
                System.out.println("No available time slots for the selected doctor.");
                return;
            }

            // Display and select time slot
            displayAvailableTimeSlots(availableSlots);
            LocalDateTime selectedDateTime = selectTimeSlot(availableSlots);
            if (selectedDateTime == null) return;

            // Create and save appointment
            createAppointment(selectedDoctor, selectedDateTime);

        } catch (Exception e) {
            System.out.println("Error scheduling appointment: " + e.getMessage());
        }
    }

    /**
     * Retrieves list of available doctors.
     * Filters doctors based on:
     * - Active status
     * - Available time slots
     * 
     * @return List of available doctors
     */
    private List<User> getAvailableDoctors() {
        List<User> allUsers = userController.getAllUsers();
        return allUsers.stream()
            .filter(user -> user instanceof Doctor)
            .filter(doctor -> !scheduleManager.getAvailableSlots(doctor.getId()).isEmpty())
            .collect(Collectors.toList());
    }

    /**
     * Displays list of available doctors.
     * Shows:
     * - Doctor ID
     * - Name
     * - Specialty
     */
    @Override
    public void displayAvailableDoctors() {
        List<User> doctors = getAvailableDoctors();
        System.out.println("\nAvailable Doctors:");
        System.out.println("----------------------------------------");
        
        if (doctors.isEmpty()) {
            System.out.println("No doctors available at the moment.");
            return;
        }

        printDoctorsTable(doctors);
    }

    /**
     * Handles doctor selection process.
     * Validates selection and returns chosen doctor.
     * 
     * @param availableDoctors List of available doctors
     * @return Selected doctor or null if cancelled
     */
    private Doctor selectDoctor(List<User> availableDoctors) {
        while (true) {
            System.out.print("\nEnter the Doctor ID you wish to schedule with (or 'cancel' to abort): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("cancel")) {
                return null;
            }

            User doctor = availableDoctors.stream()
                .filter(d -> d.getId().equals(input))
                .findFirst()
                .orElse(null);

            if (doctor instanceof Doctor) {
                return (Doctor) doctor;
            }

            System.out.println("Invalid doctor ID. Please try again.");
        }
    }

    /**
     * Displays available time slots for a doctor.
     * Shows:
     * - Date
     * - Day of week
     * - Time range
     * 
     * @param slots List of available schedule entries
     */
    private void displayAvailableTimeSlots(List<ScheduleEntry> slots) {
        System.out.println("\nAvailable Time Slots:");
        System.out.println("----------------------------------------");
        
        for (int i = 0; i < slots.size(); i++) {
            ScheduleEntry entry = slots.get(i);
            System.out.printf("%d. %s - %s: %s-%s\n", 
                i + 1,
                entry.getDate().format(DateTimeFormatter.ofPattern("EEEE")),
                entry.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                entry.getTimeSlot().getStartTime(),
                entry.getTimeSlot().getEndTime());
        }
        System.out.println("----------------------------------------");
    }

    /**
     * Handles time slot selection process.
     * Validates selection and returns chosen time.
     * 
     * @param availableSlots List of available time slots
     * @return Selected date-time or null if cancelled
     */
    private LocalDateTime selectTimeSlot(List<ScheduleEntry> availableSlots) {
        while (true) {
            System.out.print("\nEnter the number of your preferred time slot (or 0 to cancel): ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                if (choice == 0) {
                    return null;
                }

                if (choice > 0 && choice <= availableSlots.size()) {
                    ScheduleEntry selected = availableSlots.get(choice - 1);
                    return selected.getDate().atTime(
                        java.time.LocalTime.parse(selected.getTimeSlot().getStartTime(),
                        DateTimeFormatter.ofPattern("HH:mm")));
                }

                System.out.println("Invalid selection. Please choose a number between 1 and " + availableSlots.size());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /**
     * Creates new appointment with selected details.
     * Handles:
     * - Appointment ID generation
     * - Status setting
     * - Data persistence
     * 
     * @param doctor Selected doctor
     * @param dateTime Selected date and time
     */
    private void createAppointment(Doctor doctor, LocalDateTime dateTime) {
        String appointmentId = "A" + System.currentTimeMillis();
        Appointment appointment = new Appointment(appointmentId, patient, doctor, dateTime);
        appointment.setStatus("PENDING");
        appointmentList.addAppointment(appointment);
        
        System.out.println("\nAppointment scheduled successfully!");
        System.out.println("Appointment ID: " + appointmentId);
        System.out.println("Doctor: " + doctor.getName() + " (Specialty: " + doctor.getSpecialty() + ")");
        System.out.println("Date/Time: " + dateTime.format(DATE_FORMATTER));
        System.out.println("Status: PENDING");
        System.out.println("Waiting for doctor's confirmation...");

        // Save the updated appointments
        appointmentList.saveAppointmentsToCSV("SC2002HMS/data/Appointments.csv");
    }

    /**
     * Handles appointment rescheduling process.
     * Steps:
     * 1. Check doctor availability
     * 2. Display new time slots
     * 3. Select new time
     * 4. Update appointment
     * 
     * @param appointment Appointment to reschedule
     */
    @Override
    public void rescheduleAppointment(Appointment appointment) {
        try {
            Doctor doctor = appointment.getDoctor();
            List<ScheduleEntry> availableSlots = scheduleManager.getAvailableSlots(doctor.getId());

            if (availableSlots.isEmpty()) {
                System.out.println("No available time slots for rescheduling.");
                return;
            }

            displayAvailableTimeSlots(availableSlots);
            LocalDateTime newDateTime = selectTimeSlot(availableSlots);
            
            if (newDateTime == null) {
                System.out.println("Rescheduling cancelled.");
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

    /**
     * Handles appointment cancellation process.
     * Confirms cancellation and updates status.
     * 
     * @param appointment Appointment to cancel
     */
    @Override
    public void cancelAppointment(Appointment appointment) {
        System.out.print("Are you sure you want to cancel this appointment? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        if (confirm.equals("Y")) {
            appointment.setStatus("Cancelled");
            appointmentList.saveAppointmentsToCSV("SC2002HMS/data/Appointments.csv");
            System.out.println("Appointment cancelled successfully.");
        } else {
            System.out.println("Cancellation aborted.");
        }
    }

    /**
     * Formats and prints doctor information in table format.
     * Displays:
     * - ID
     * - Name
     * - Specialty
     * 
     * @param doctors List of doctors to display
     */
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

    /**
     * Prints a formatted table row.
     * Handles column width and alignment.
     * 
     * @param data Row data to print
     * @param widths Column widths
     */
    private void printTableRow(String[] data, int[] widths) {
        StringBuilder row = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            row.append(String.format("%-" + widths[i] + "s", data[i]));
            if (i < data.length - 1) {
                row.append(" | ");
            }
        }
        System.out.println(row);
    }
}