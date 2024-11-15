package patient_management;

import appointment_management.Appointment;
import appointment_management.AppointmentList;
import user_management.Patient;
import utils.CSVReaderUtil;
import utils.CSVWriterUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PatientDashboard {
    private Patient patient;
    private MedicalRecord medicalRecord;
    private AppointmentList appointmentList;
    private AppointmentOutcomeViewer appointmentOutcomeViewer;
    private Scanner scanner;

    public PatientDashboard(Patient patient, AppointmentList appointmentList) {
        this.patient = patient;
        this.appointmentList = appointmentList;
        this.appointmentOutcomeViewer = new AppointmentOutcomeViewer(patient);
        this.scanner = new Scanner(System.in);
        loadMedicalRecord(); // Dynamically load the patient's medical record
    }

    public void showDashboard() {
        while (true) {
            System.out.println("\nPatient Dashboard - " + patient.getName());
            System.out.println("1. View Medical Records");
            System.out.println("2. View Appointments");
            System.out.println("3. View Appointment Outcomes");
            System.out.println("4. Update Personal Information");
            System.out.println("5. Log Out");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> viewMedicalRecords();
                case 2 -> viewAppointments();
                case 3 -> viewAppointmentOutcomes();
                case 4 -> updatePersonalInfo();
                case 5 -> {
                    System.out.println("Logging out of Patient Dashboard...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void loadMedicalRecord() {
        List<String[]> records = CSVReaderUtil.readCSV("data/Medical_Record.csv");

        for (String[] record : records) {
            if (record[0].equals(patient.getId())) {
                medicalRecord = new MedicalRecord(patient);
                medicalRecord.setDiagnosis(record[2]);
                medicalRecord.setPrescription(record[3]);
//                medicalRecord.setNotes(record.length > 4 ? record[4] : "");
                return;
            }
        }
        System.out.println("No existing medical record found for " + patient.getName() + ". Creating a new record.");
        medicalRecord = new MedicalRecord(patient); // Create a new record if none found
    }

    private void viewMedicalRecords() {
        medicalRecord.displayRecord();
    }

    private void viewAppointments() {
        System.out.println("\nAppointments for " + patient.getName() + ":");
        List<Appointment> patientAppointments = getAppointmentsForPatient();

        if (patientAppointments.isEmpty()) {
            System.out.println("No appointments found.");
        } else {
            for (Appointment appointment : patientAppointments) {
                System.out.println(appointment);
            }
        }
    }

    private List<Appointment> getAppointmentsForPatient() {
        List<Appointment> patientAppointments = new ArrayList<>();
        for (Appointment appointment : appointmentList.getAllAppointments()) {
            if (appointment.getPatient() != null && appointment.getPatient().getId().equals(patient.getId())) {
                patientAppointments.add(appointment);
            }
        }
        return patientAppointments;
    }

    private void viewAppointmentOutcomes() {
        appointmentOutcomeViewer.displayOutcomes();
    }

    private void updatePersonalInfo() {
        System.out.print("Enter new email: ");
        String newEmail = scanner.nextLine();
        System.out.print("Enter new contact number: ");
        String newContact = scanner.nextLine();

        patient.setContactInfo(newEmail, newContact);
        System.out.println("Personal information updated successfully.");
    }

    private void saveMedicalRecord() {
        List<String[]> records = CSVReaderUtil.readCSV("data/Medical_Record.csv");
        List<String[]> updatedRecords = new ArrayList<>();

        boolean recordFound = false;
        for (String[] record : records) {
            if (record[0].equals(patient.getId())) {
                updatedRecords.add(new String[]{
                        patient.getId(),
                        patient.getName(),
                        medicalRecord.getDiagnosis(),
                        medicalRecord.getPrescription(),
//                        medicalRecord.getNotes()
                });
                recordFound = true;
            } else {
                updatedRecords.add(record);
            }
        }

        if (!recordFound) {
            updatedRecords.add(new String[]{
                    patient.getId(),
                    patient.getName(),
                    medicalRecord.getDiagnosis(),
                    medicalRecord.getPrescription(),
//                    medicalRecord.getNotes()
            });
        }

        CSVWriterUtil.writeCSV("data/Medical_Record.csv", updatedRecords);
        System.out.println("Medical record saved successfully.");
    }
}
