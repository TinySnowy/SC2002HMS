package doctor_management;

import appointment_management.Appointment;
import appointment_management.AppointmentList;
import patient_management.controllers.MedicalRecordController;
import pharmacy_management.appointments.AppointmentOutcome;
import pharmacy_management.appointments.IAppointmentOutcomeService;
import pharmacy_management.prescriptions.Prescription;
import user_management.Doctor;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DoctorDashboard implements AutoCloseable {
    private final Doctor doctor;
    private final DoctorManagementFacade doctorManager;
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
                int choice = getValidatedInput("Enter your choice: ", 1, 8);
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
        System.out.println("3. View Personal Schedule");
        System.out.println("4. Set Availability for Appointments");
        System.out.println("5. Accept/Decline Appointment Requests");
        System.out.println("6. View Upcoming Appointments");
        System.out.println("7. Record Appointment Outcome");
        System.out.println("8. Logout");
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
                viewUpcomingAppointments();
                return true;
            case 7:
                recordAppointmentOutcome();
                return true;
            case 8:
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

    private void viewPersonalSchedule() {
        Map<String, String> schedule = doctorManager.getDoctorAvailability(doctor.getId());
        System.out.println("\nCurrent Schedule:");
        System.out.println("----------------------------------------");
        if (schedule.isEmpty()) {
            System.out.println("No availability set.");
        } else {
            for (Map.Entry<String, String> entry : schedule.entrySet()) {
                System.out.printf("%s: %s%n", entry.getKey(), entry.getValue());
            }
        }
        System.out.println("----------------------------------------");
    }

    private void setAvailability() {
        System.out.println("\nSet Availability");
        System.out.println("----------------------------------------");

        Map<String, String> weeklySchedule = new HashMap<>();
        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };

        System.out.println("Enter availability for each day (format: HH:mm-HH:mm, e.g., 09:00-17:00)");
        System.out.println("Press Enter to skip a day or keep current schedule");

        for (String day : days) {
            System.out.printf("%s: ", day);
            String time = scanner.nextLine().trim();
            if (!time.isEmpty()) {
                weeklySchedule.put(day, time);
            }
        }

        if (!weeklySchedule.isEmpty()) {
            doctorManager.setDoctorAvailability(doctor.getId(), weeklySchedule);
            System.out.println("\nAvailability updated successfully!");
        } else {
            System.out.println("\nNo availability set.");
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

    private void viewUpcomingAppointments() {
        System.out.println("\nUpcoming Appointments:");
        System.out.println("----------------------------------------");
        List<Appointment> appointments = doctorManager.viewPendingAppointments(doctor.getId());

        if (appointments.isEmpty()) {
            System.out.println("No upcoming appointments found.");
            return;
        }

        for (Appointment appointment : appointments) {
            System.out.println("\nAppointment ID: " + appointment.getAppointmentId());
            System.out.println("Patient: " + appointment.getPatient().getName());
            System.out.println("Date: " + appointment.getAppointmentDate().format(DATE_FORMATTER));
            System.out.println("Status: " + appointment.getStatus());
            System.out.println("----------------------------------------");
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