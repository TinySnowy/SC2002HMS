package patient_management.handlers;

import appointment_management.Appointment;
import appointment_management.AppointmentList;
import appointment_management.feedback.AppointmentFeedback;
import appointment_management.feedback.FeedbackService;
import user_management.Patient;
import java.util.Scanner;
import java.util.List;

/**
 * Handles feedback management for patient appointments in the HMS.
 * Manages:
 * - Feedback submission
 * - Feedback viewing
 * - Rating collection
 * - Anonymous feedback options
 * Provides interface for patients to give and view appointment feedback.
 */
public class FeedbackHandler {
    /** Currently active patient */
    private final Patient patient;
    
    /** System-wide appointment list */
    private final AppointmentList appointmentList;
    
    /** Service for managing feedback */
    private final FeedbackService feedbackService;
    
    /** Scanner for user input */
    private final Scanner scanner;

    /**
     * Constructs a new FeedbackHandler with required dependencies.
     * 
     * @param patient Active patient
     * @param appointmentList System appointment list
     * @param feedbackService Service for feedback management
     */
    public FeedbackHandler(Patient patient, AppointmentList appointmentList, FeedbackService feedbackService) {
        this.patient = patient;
        this.appointmentList = appointmentList;
        this.feedbackService = feedbackService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Handles the feedback submission process.
     * Steps:
     * 1. Get completed appointments
     * 2. Display available appointments
     * 3. Select appointment
     * 4. Collect and submit feedback
     */
    public void handleFeedback() {
        // Get appointments eligible for feedback
        List<Appointment> completedAppointments = appointmentList.getAppointmentsForPatient(patient.getId())
            .stream()
            .filter(apt -> "Completed".equalsIgnoreCase(apt.getStatus()))
            .filter(apt -> feedbackService.getFeedbackByAppointment(apt.getAppointmentId()) == null)
            .toList();

        if (completedAppointments.isEmpty()) {
            System.out.println("\nNo completed appointments available for feedback.");
            return;
        }

        displayCompletedAppointments(completedAppointments);
        Appointment selectedAppointment = selectAppointment(completedAppointments);
        
        if (selectedAppointment != null) {
            submitFeedback(selectedAppointment);
        }
    }

    /**
     * Displays completed appointments available for feedback.
     * Shows:
     * - Appointment date
     * - Doctor name
     * - Selection number
     * 
     * @param appointments List of completed appointments
     */
    private void displayCompletedAppointments(List<Appointment> appointments) {
        System.out.println("\nCompleted Appointments Available for Feedback:");
        System.out.println("--------------------------------------------");
        for (int i = 0; i < appointments.size(); i++) {
            Appointment apt = appointments.get(i);
            System.out.printf("%d. Date: %s - Dr. %s\n", 
                i + 1,
                apt.getAppointmentDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                apt.getDoctor().getName());
        }
        System.out.println("--------------------------------------------");
    }

    /**
     * Handles appointment selection for feedback.
     * Validates user input and returns selected appointment.
     * 
     * @param appointments List of available appointments
     * @return Selected appointment or null if cancelled
     */
    private Appointment selectAppointment(List<Appointment> appointments) {
        while (true) {
            System.out.print("\nSelect appointment number (or 0 to cancel): ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice == 0) return null;
                if (choice > 0 && choice <= appointments.size()) {
                    return appointments.get(choice - 1);
                }
                System.out.println("Invalid selection. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /**
     * Handles feedback submission process.
     * Collects:
     * - Rating (1-5)
     * - Comments
     * - Anonymity preference
     * Creates and submits feedback record.
     * 
     * @param appointment Appointment to submit feedback for
     */
    private void submitFeedback(Appointment appointment) {
        System.out.println("\nSubmit Feedback");
        System.out.println("--------------------------------------------");

        // Get rating
        int rating = getRating();
        if (rating == 0) return;

        // Get comment
        System.out.println("\nPlease provide your feedback comments:");
        String comment = scanner.nextLine().trim();

        // Ask for anonymity preference
        System.out.print("\nWould you like to submit this feedback anonymously? (Y/N): ");
        boolean isAnonymous = scanner.nextLine().trim().equalsIgnoreCase("Y");

        // Create and submit feedback
        String feedbackId = "F" + System.currentTimeMillis();
        AppointmentFeedback feedback = new AppointmentFeedback(
            feedbackId,
            appointment.getAppointmentId(),
            patient.getId(),
            appointment.getDoctor().getId(),
            rating,
            comment,
            isAnonymous
        );

        feedbackService.submitFeedback(feedback);
        System.out.println("\nThank you for your feedback!");
    }

    /**
     * Collects and validates rating input.
     * Displays rating scale and validates selection.
     * 
     * @return Selected rating (1-5) or 0 if cancelled
     */
    private int getRating() {
        while (true) {
            System.out.println("\nPlease rate your experience (1-5):");
            System.out.println("5 - Excellent");
            System.out.println("4 - Very Good");
            System.out.println("3 - Good");
            System.out.println("2 - Fair");
            System.out.println("1 - Poor");
            System.out.println("0 - Cancel");
            
            try {
                int rating = Integer.parseInt(scanner.nextLine().trim());
                if (rating == 0) return 0;
                if (rating >= 1 && rating <= 5) {
                    return rating;
                }
                System.out.println("Please enter a number between 1 and 5.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public void viewMyFeedback() {
        List<AppointmentFeedback> myFeedback = feedbackService.getFeedbackByPatient(patient.getId());
        
        if (myFeedback.isEmpty()) {
            System.out.println("\nYou haven't submitted any feedback yet.");
            return;
        }

        System.out.println("\nYour Submitted Feedback:");
        System.out.println("--------------------------------------------");
        
        for (AppointmentFeedback feedback : myFeedback) {
            Appointment apt = appointmentList.getAppointmentById(feedback.getAppointmentId());
            if (apt != null) {
                System.out.printf("\nAppointment Date: %s\n", 
                    apt.getAppointmentDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                System.out.printf("Doctor: %s\n", apt.getDoctor().getName());
                System.out.println(feedback.toString());
                System.out.println("--------------------------------------------");
            }
        }
    }
}