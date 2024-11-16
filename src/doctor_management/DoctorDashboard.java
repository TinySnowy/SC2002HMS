package doctor_management;

import appointment_management.Appointment;
import appointment_management.AppointmentList;
import patient_management.MedicalRecordController;
import pharmacy_management.Prescription;
import user_management.Doctor;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DoctorDashboard implements AutoCloseable {
    private final Doctor doctor;
    private final AppointmentList appointmentList;
    private final ScheduleManager scheduleManager;
    private final MedicalRecordUpdater medicalRecordUpdater;
    private final Scanner scanner;

    public DoctorDashboard(Doctor doctor, AppointmentList appointmentList) {
        this.doctor = doctor;
        this.appointmentList = appointmentList;
        MedicalRecordController medicalRecordController = new MedicalRecordController();
        this.medicalRecordUpdater = new MedicalRecordUpdater(medicalRecordController);
        this.scheduleManager = new ScheduleManager();
        this.scanner = new Scanner(System.in);
    }

    public void showDashboard() {
        boolean running = true;
        while (running) {
            try {
                displayMenu();
                int choice = getValidatedInput("Enter your choice: ", 1, 6);
                running = handleMenuChoice(choice);
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                scanner.nextLine(); // Clear scanner buffer
            }
        }
    }

    private void displayMenu() {
        System.out.println("\nDoctor Dashboard - Dr. " + doctor.getName());
        System.out.println("----------------------------------------");
        System.out.println("1. View Pending Appointments");
        System.out.println("2. Accept/Decline Appointment Requests");
        System.out.println("3. Record Appointment Outcomes");
        System.out.println("4. Update Availability");
        System.out.println("5. Update Patient Medical Record");
        System.out.println("6. Log Out");
        System.out.println("----------------------------------------");
    }

    private boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                viewPendingAppointments();
                return true;
            case 2:
                manageAppointmentRequests();
                return true;
            case 3:
                recordAppointmentOutcome();
                return true;
            case 4:
                updateAvailability();
                return true;
            case 5:
                updatePatientMedicalRecord();
                return true;
            case 6:
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
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    private void viewPendingAppointments() {
        System.out.println("\nPending Appointments for Dr. " + doctor.getName() + ":");
        System.out.println("----------------------------------------");
        boolean found = false;
        for (Appointment appointment : appointmentList.getAllAppointments()) {
            if ("Pending".equals(appointment.getStatus()) &&
                    appointment.getDoctor().getId().equals(doctor.getId())) {
                System.out.println(appointment);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No pending appointments found.");
        }
        System.out.println("----------------------------------------");
    }

    private void manageAppointmentRequests() {
        viewPendingAppointments();
        System.out.print("Enter Appointment ID to manage (or 'back' to return): ");
        String appointmentId = scanner.nextLine();

        if (appointmentId.equalsIgnoreCase("back")) {
            return;
        }

        Appointment appointment = appointmentList.getAppointmentById(appointmentId);
        if (appointment != null && appointment.getDoctor().getId().equals(doctor.getId())) {
            System.out.println("1. Confirm Appointment");
            System.out.println("2. Decline Appointment");
            int choice = getValidatedInput("Enter your choice: ", 1, 2);

            switch (choice) {
                case 1:
                    appointment.confirm();
                    System.out.println("Appointment confirmed successfully.");
                    break;
                case 2:
                    appointment.decline();
                    System.out.println("Appointment declined successfully.");
                    break;
            }
        } else {
            System.out.println("Appointment not found or not assigned to you.");
        }
    }

    private void recordAppointmentOutcome() {
        System.out.print("Enter Appointment ID (or 'back' to return): ");
        String appointmentId = scanner.nextLine();

        if (appointmentId.equalsIgnoreCase("back")) {
            return;
        }

        Appointment appointment = appointmentList.getAppointmentById(appointmentId);
        if (appointment == null || !appointment.getDoctor().getId().equals(doctor.getId())) {
            System.out.println("Appointment not found or not assigned to you.");
            return;
        }

        if (!"Confirmed".equals(appointment.getStatus())) {
            System.out.println("Can only record outcomes for confirmed appointments.");
            return;
        }

        try {
            System.out.print("Enter Service Type: ");
            String serviceType = scanner.nextLine();

            System.out.print("Enter Consultation Notes: ");
            String consultationNotes = scanner.nextLine();

            List<Prescription> prescriptions = new ArrayList<>();
            while (true) {
                System.out.print("\nAdd prescription? (yes/no): ");
                if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                    break;
                }

                System.out.print("Medication Name: ");
                String medicationName = scanner.nextLine();

                System.out.print("Dosage: ");
                String dosage = scanner.nextLine();

                int quantity = getValidatedInput("Quantity: ", 1, 100);

                prescriptions.add(new Prescription(
                        appointmentId + "-" + prescriptions.size(),
                        appointment.getPatient(),
                        medicationName,
                        dosage,
                        quantity));
            }

            appointment.setOutcome(serviceType, consultationNotes, prescriptions);
            System.out.println("Appointment outcome recorded successfully.");

        } catch (Exception e) {
            System.err.println("Error recording outcome: " + e.getMessage());
        }
    }

    private void updateAvailability() {
        scheduleManager.updateAvailability(doctor);
    }

    private void updatePatientMedicalRecord() {
        System.out.print("Enter Patient ID (or 'back' to return): ");
        String patientId = scanner.nextLine();

        if (patientId.equalsIgnoreCase("back")) {
            return;
        }

        medicalRecordUpdater.updateRecords(patientId);
    }

    @Override
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
        if (medicalRecordUpdater != null) {
            medicalRecordUpdater.close();
        }
    }
}