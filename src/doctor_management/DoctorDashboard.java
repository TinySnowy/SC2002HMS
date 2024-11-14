package doctor_management;

import appointment_management.Appointment;
import appointment_management.AppointmentList;
import pharmacy_management.Prescription;
import user_management.Doctor;
import patient_management.MedicalRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DoctorDashboard {
    private Doctor doctor;
    private AppointmentList appointmentList;
    private ScheduleManager scheduleManager;
    private MedicalRecordUpdater medicalRecordUpdater;
    private Scanner scanner;

    public DoctorDashboard(Doctor doctor, AppointmentList appointmentList) {
        this.doctor = doctor;
        this.appointmentList = appointmentList;
        this.scheduleManager = new ScheduleManager();
        this.medicalRecordUpdater = new MedicalRecordUpdater();
        this.scanner = new Scanner(System.in);
    }

    public void showDashboard() {
        while (true) {
            System.out.println("\nDoctor Dashboard - Dr. " + doctor.getName());
            System.out.println("1. View Pending Appointments");
            System.out.println("2. Accept/Decline Appointment Requests");
            System.out.println("3. Record Appointment Outcomes");
            System.out.println("4. Update Availability");
            System.out.println("5. Update Patient Medical Record");
            System.out.println("6. Log Out");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> viewPendingAppointments();
                case 2 -> manageAppointmentRequests();
                case 3 -> recordAppointmentOutcome();
                case 4 -> updateAvailability();
                case 5 -> updatePatientMedicalRecord();
                case 6 -> {
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

            List<Prescription> prescriptions = new ArrayList<>();
            System.out.println("Enter Prescribed Medications (type 'done' when finished):");

            while (true) {
                System.out.print("Medication Name (or 'done' to finish): ");
                String medicationName = scanner.nextLine();
                if ("done".equalsIgnoreCase(medicationName)) {
                    break;
                }

                System.out.print("Dosage: ");
                String dosage = scanner.nextLine();

                System.out.print("Quantity: ");
                int quantity = scanner.nextInt();
                scanner.nextLine(); // consume newline

                // Create and add the prescription to the list
                prescriptions.add(new Prescription(appointmentId + "-" + prescriptions.size(), appointment.getPatient(), medicationName, dosage, quantity));
            }

            // Update the appointment with outcome details and prescriptions
            appointment.setOutcome(serviceType, consultationNotes, prescriptions);
            System.out.println("Appointment outcome recorded successfully with prescriptions.");
        } else {
            System.out.println("Appointment not found, not confirmed, or not assigned to you.");
        }
    }


    private void updateAvailability() {
        System.out.println("Updating availability for Dr. " + doctor.getName());
        scheduleManager.updateAvailability(doctor);
    }

    private void updatePatientMedicalRecord() {
        System.out.print("Enter Patient ID to update medical record: ");
        String patientId = scanner.nextLine();
        medicalRecordUpdater.updateRecords(patientId);
    }
}