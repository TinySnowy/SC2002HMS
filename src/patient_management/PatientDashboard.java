package patient_management;

import appointment_management.AppointmentList;
import appointment_management.DoctorScheduleManager;
import appointment_management.feedback.FeedbackService;
import pharmacy_management.appointments.AppointmentOutcomeService;
import user_management.*;
import admin_management.handlers.staff.StaffDisplayHandler;
import patient_management.handlers.*;
import patient_management.controllers.MedicalRecordController;
import patient_management.utils.PatientMenuPrinter;
import patient_management.interfaces.*;

/**
 * Main dashboard interface for patient interactions in the HMS.
 * Manages:
 * - Medical record access
 * - Appointment operations
 * - Personal information updates
 * - Feedback submission
 * - User session control
 * Serves as the central hub for all patient-related activities.
 */
public class PatientDashboard {
    /** Currently active patient user */
    private final Patient patient;
    
    /** Controller for managing medical records */
    private final MedicalRecordController medicalRecordController;
    
    /** Interface for viewing appointment details */
    private final IAppointmentViewer appointmentViewer;
    
    /** Interface for managing appointment operations */
    private final IAppointmentHandler appointmentHandler;
    
    /** Interface for managing personal information */
    private final IPersonalInfoManager personalInfoManager;
    
    /** Handler for feedback operations */
    private final FeedbackHandler feedbackHandler;
    
    /** Utility for menu display and input */
    private final PatientMenuPrinter menuPrinter;
    
    /** System-wide appointment management */
    private final AppointmentList appointmentList;
    
    /** Controller for user management */
    private final UserController userController;

    /**
     * Constructs a new PatientDashboard with required dependencies.
     * Initializes all necessary components and services.
     * Sets up handlers and viewers for patient operations.
     * 
     * @param patient Currently logged-in patient
     * @param appointmentList System appointment manager
     * @param userController User management system
     */
    public PatientDashboard(Patient patient, AppointmentList appointmentList, UserController userController) {
        this.patient = patient;
        this.userController = userController;
        this.appointmentList = appointmentList;
        this.medicalRecordController = new MedicalRecordController();
        
        // Initialize services
        AppointmentOutcomeService appointmentOutcomeService = new AppointmentOutcomeService(userController);
        DoctorScheduleManager scheduleManager = new DoctorScheduleManager();
        FeedbackService feedbackService = new FeedbackService();
        
        // Initialize handlers
        this.appointmentViewer = new AppointmentViewer(patient, appointmentList, appointmentOutcomeService);
        this.appointmentHandler = new AppointmentHandler(patient, appointmentList, userController, scheduleManager);
        this.personalInfoManager = new PersonalInfoHandler(patient, userController);
        this.feedbackHandler = new FeedbackHandler(patient, appointmentList, feedbackService);
        this.menuPrinter = new PatientMenuPrinter();
    }

    public void showDashboard() {
        boolean running = true;
        while (running) {
            try {
                menuPrinter.displayMenu(patient);
                int choice = menuPrinter.getValidatedInput("Enter your choice: ", 1, 9);
                running = handleMenuChoice(choice);
            } catch (Exception e) {
                menuPrinter.displayError(e.getMessage());
            }
        }
    }

    private boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                viewMedicalRecords();
                return true;
            case 2:
                appointmentViewer.viewAllAppointments();
                return true;
            case 3:
                appointmentHandler.scheduleAppointment();
                return true;
            case 4:
                manageExistingAppointments();
                return true;
            case 5:
                appointmentViewer.viewAppointmentOutcomes();
                return true;
            case 6:
                personalInfoManager.updatePersonalInfo();
                return true;
            case 7:
                feedbackHandler.handleFeedback();
                return true;
            case 8:
                feedbackHandler.viewMyFeedback();
                return true;
            case 9:
                System.out.println("Logging out of Patient Dashboard...");
                return false;
            default:
                menuPrinter.displayError("Invalid choice. Please try again.");
                return true;
        }
    }

    private void viewMedicalRecords() {
        var record = medicalRecordController.getRecordByPatientId(patient.getId());
        if (record == null) {
            menuPrinter.displayError("No medical record found.");
            return;
        }
        
        PatientDetailViewer detailViewer = new PatientDetailViewer();
        detailViewer.displayFullPatientRecord(record);
    }

    private void manageExistingAppointments() {
        var appointments = appointmentList.getAppointmentsForPatient(patient.getId());
        
        if (appointments.isEmpty()) {
            menuPrinter.displayError("No appointments found to manage.");
            return;
        }

        System.out.println("\nYour Appointments:");
        for (int i = 0; i < appointments.size(); i++) {
            System.out.println((i + 1) + ". " + appointments.get(i));
        }

        System.out.println("\nManage Appointments");
        System.out.println("1. Reschedule Appointment");
        System.out.println("2. Cancel Appointment");
        System.out.println("3. Back to Main Menu");

        int choice = menuPrinter.getValidatedInput("Enter your choice: ", 1, 3);
        if (choice == 3) return;

        int appointmentIndex = menuPrinter.getValidatedInput(
            "Enter the number of the appointment to manage: ", 1, appointments.size()) - 1;
        var selectedAppointment = appointments.get(appointmentIndex);

        if (selectedAppointment.getStatus().equals("Completed") ||
            selectedAppointment.getStatus().equals("Cancelled")) {
            menuPrinter.displayError("Cannot modify completed or cancelled appointments.");
            return;
        }

        switch (choice) {
            case 1:
                appointmentHandler.rescheduleAppointment(selectedAppointment);
                break;
            case 2:
                appointmentHandler.cancelAppointment(selectedAppointment);
                break;
        }
    }

    public void close() {
        menuPrinter.close();
    }
}