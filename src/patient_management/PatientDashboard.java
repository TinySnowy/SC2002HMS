package patient_management;

import appointment_management.Appointment;
import appointment_management.AppointmentList;
import user_management.Patient;
// import utils.CSVReaderUtil;
// import utils.CSVWriterUtil;

// import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PatientDashboard {
    private final Patient patient;
    private final MedicalRecordController medicalRecordController;
    private final AppointmentList appointmentList;
    private final AppointmentOutcomeViewer appointmentOutcomeViewer;
    private final Scanner scanner;
    // private static final String MEDICAL_RECORD_PATH =
    // "SC2002HMS/data/Medical_Record.csv";

    public PatientDashboard(Patient patient, AppointmentList appointmentList) {
        this.patient = patient;
        this.appointmentList = appointmentList;
        this.medicalRecordController = new MedicalRecordController();
        this.appointmentOutcomeViewer = new AppointmentOutcomeViewer(patient);
        this.scanner = new Scanner(System.in);
    }

    public void showDashboard() {
        boolean running = true;
        while (running) {
            try {
                displayMenu();
                int choice = getValidatedInput("Enter your choice: ", 1, 5);
                running = handleMenuChoice(choice);
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                scanner.nextLine(); // Clear scanner buffer
            }
        }
    }

    private void displayMenu() {
        System.out.println("\nPatient Dashboard - " + patient.getName());
        System.out.println("----------------------------------------");
        System.out.println("1. View Medical Records");
        System.out.println("2. View Appointments");
        System.out.println("3. View Appointment Outcomes");
        System.out.println("4. Update Personal Information");
        System.out.println("5. Log Out");
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
                viewAppointmentOutcomes();
                return true;
            case 4:
                updatePersonalInfo();
                return true;
            case 5:
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

        // Display Diagnosis
        String diagnosis = record.getDiagnosis();
        if (diagnosis != null && !diagnosis.isEmpty()) {
            System.out.println("Diagnosis: " + diagnosis);
        } else {
            System.out.println("Diagnosis: None");
        }

        // Display Prescription
        String prescription = record.getPrescription();
        if (prescription != null && !prescription.isEmpty()) {
            System.out.println("Prescription: " + prescription);
        } else {
            System.out.println("Prescription: None");
        }
        System.out.println("----------------------------------------");
    }

    private void viewAppointments() {
        System.out.println("\nAppointments for " + patient.getName() + ":");
        System.out.println("----------------------------------------");
        List<Appointment> patientAppointments = getAppointmentsForPatient();

        if (patientAppointments.isEmpty()) {
            System.out.println("No appointments found.");
        } else {
            for (Appointment appointment : patientAppointments) {
                System.out.println(appointment);
            }
        }
        System.out.println("----------------------------------------");
    }

    private List<Appointment> getAppointmentsForPatient() {
        return appointmentList.getAppointmentsForPatient(patient.getId());
    }

    private void viewAppointmentOutcomes() {
        appointmentOutcomeViewer.displayOutcomes();
    }

    private void updatePersonalInfo() {
        try {
            System.out.print("Enter new email (or press Enter to keep current): ");
            String newEmail = scanner.nextLine().trim();
            if (!newEmail.isEmpty()) {
                patient.setEmail(newEmail);
            }

            System.out.print("Enter new contact number (or press Enter to keep current): ");
            String newContact = scanner.nextLine().trim();
            if (!newContact.isEmpty()) {
                patient.setContactInfo(newContact);
            }

            System.out.println("Personal information updated successfully.");
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