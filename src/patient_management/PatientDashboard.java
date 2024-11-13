package patient_management;

import user_management.Patient;

import java.util.Scanner;

public class PatientDashboard {
    private Patient patient;
    private MedicalRecord medicalRecord;
    private AppointmentHandler appointmentHandler;
    private AppointmentOutcomeViewer appointmentOutcomeViewer;
    private Scanner scanner;

    public PatientDashboard(Patient patient) {
        this.patient = patient;
        this.medicalRecord = new MedicalRecord(patient);
        this.appointmentHandler = new AppointmentHandler(patient);
        this.appointmentOutcomeViewer = new AppointmentOutcomeViewer(patient);
        this.scanner = new Scanner(System.in);
    }

    public void showDashboard() {
        while (true) {
            System.out.println("\nPatient Dashboard - " + patient.getName());
            System.out.println("1. View Medical Records");
            System.out.println("2. Manage Appointments");
            System.out.println("3. View Appointment Outcomes");
            System.out.println("4. Log Out");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> viewMedicalRecords();
                case 2 -> manageAppointments();
                case 3 -> viewAppointmentOutcomes();
                case 4 -> {
                    System.out.println("Logging out of Patient Dashboard...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void viewMedicalRecords() {
        medicalRecord.displayRecord();
    }

    private void manageAppointments() {
        appointmentHandler.showAppointmentMenu();
    }

    private void viewAppointmentOutcomes() {
        appointmentOutcomeViewer.displayOutcomes();
    }
}
