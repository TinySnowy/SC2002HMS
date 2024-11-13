package patient_management;

import user_management.Patient;
import appointment_management.Appointment;
import appointment_management.AppointmentList;

import java.time.LocalDateTime;
import java.util.Scanner;

public class AppointmentHandler {
    private Patient patient;
    private AppointmentList appointmentList;
    private Scanner scanner;

    public AppointmentHandler(Patient patient) {
        this.patient = patient;
        this.appointmentList = new AppointmentList();
        this.scanner = new Scanner(System.in);
    }

    public void showAppointmentMenu() {
        System.out.println("\nAppointment Management for " + patient.getName());
        System.out.println("1. Schedule Appointment");
        System.out.println("2. Reschedule Appointment");
        System.out.println("3. Cancel Appointment");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1 -> scheduleAppointment();
            case 2 -> rescheduleAppointment();
            case 3 -> cancelAppointment();
            default -> System.out.println("Invalid choice. Returning to dashboard.");
        }
    }

    private void scheduleAppointment() {
        System.out.print("Enter appointment date and time (YYYY-MM-DD HH:MM): ");
        String dateTimeInput = scanner.nextLine();
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeInput.replace(" ", "T"));
        String appointmentId = "A" + (appointmentList.getAllAppointments().size() + 1);

        Appointment appointment = new Appointment(appointmentId, patient, null, dateTime); // Doctor set to null for simplicity
        appointmentList.addAppointment(appointment);
        System.out.println("Appointment scheduled: " + appointment);
    }

    private void rescheduleAppointment() {
        System.out.print("Enter appointment ID to reschedule: ");
        String appointmentId = scanner.nextLine();
        Appointment appointment = appointmentList.getAppointmentById(appointmentId);

        if (appointment != null) {
            System.out.print("Enter new date and time (YYYY-MM-DD HH:MM): ");
            String newDateTimeInput = scanner.nextLine();
            LocalDateTime newDateTime = LocalDateTime.parse(newDateTimeInput.replace(" ", "T"));
            appointment.setAppointmentDate(newDateTime);
            System.out.println("Appointment rescheduled: " + appointment);
        } else {
            System.out.println("Appointment not found.");
        }
    }

    private void cancelAppointment() {
        System.out.print("Enter appointment ID to cancel: ");
        String appointmentId = scanner.nextLine();
        Appointment appointment = appointmentList.getAppointmentById(appointmentId);

        if (appointment != null) {
            appointment.decline();
            System.out.println("Appointment cancelled: " + appointment);
        } else {
            System.out.println("Appointment not found.");
        }
    }
}
