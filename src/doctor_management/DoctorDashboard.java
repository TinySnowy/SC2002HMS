package doctor_management;

import appointment_management.Appointment;
import appointment_management.AppointmentList;
import user_management.Doctor;
import java.util.Scanner;

public class DoctorDashboard {
    private Doctor doctor;
    private AppointmentList appointmentList;
    private Scanner scanner;

    public DoctorDashboard(Doctor doctor, AppointmentList appointmentList) {
        this.doctor = doctor;
        this.appointmentList = appointmentList;
        this.scanner = new Scanner(System.in);
    }

    public void showDashboard() {
        while (true) {
            System.out.println("\nDoctor Dashboard - Dr. " + doctor.getName());
            System.out.println("1. View Pending Appointments");
            System.out.println("2. Accept/Decline Appointment Requests");
            System.out.println("3. Record Appointment Outcomes");
            System.out.println("4. Log Out");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> viewPendingAppointments();
                case 2 -> manageAppointmentRequests();
                case 3 -> recordAppointmentOutcome();
                case 4 -> {
                    System.out.println("Logging out of Doctor Dashboard...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void viewPendingAppointments() {
        System.out.println("\nPending Appointments for Dr. " + doctor.getName() + ":");
        for (Appointment appointment : appointmentList.getAllAppointments()) {
            if ("Pending".equals(appointment.getStatus()) && appointment.getDoctor() == doctor) {
                System.out.println(appointment);
            }
        }
    }

    private void manageAppointmentRequests() {
        System.out.print("Enter Appointment ID to manage: ");
        String appointmentId = scanner.nextLine();
        Appointment appointment = appointmentList.getAppointmentById(appointmentId);

        if (appointment != null && appointment.getDoctor() == doctor) {
            System.out.println("1. Confirm Appointment");
            System.out.println("2. Decline Appointment");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> {
                    appointment.confirm();
                    System.out.println("Appointment confirmed: " + appointment);
                }
                case 2 -> {
                    appointment.decline();
                    System.out.println("Appointment declined: " + appointment);
                }
                default -> System.out.println("Invalid choice.");
            }
        } else {
            System.out.println("Appointment not found or not assigned to you.");
        }
    }

    private void recordAppointmentOutcome() {
        System.out.print("Enter Appointment ID to record outcome: ");
        String appointmentId = scanner.nextLine();
        Appointment appointment = appointmentList.getAppointmentById(appointmentId);

        if (appointment != null && "Confirmed".equals(appointment.getStatus()) && appointment.getDoctor() == doctor) {
            System.out.print("Enter Service Type (e.g., Consultation, X-ray): ");
            String serviceType = scanner.nextLine();
            System.out.print("Enter Consultation Notes: ");
            String consultationNotes = scanner.nextLine();
            System.out.print("Enter Prescribed Medications: ");
            String prescribedMedications = scanner.nextLine();

            appointment.setOutcome(serviceType, consultationNotes, prescribedMedications);
            System.out.println("Appointment outcome recorded.");
        } else {
            System.out.println("Appointment not found, not confirmed, or not assigned to you.");
        }
    }
}
