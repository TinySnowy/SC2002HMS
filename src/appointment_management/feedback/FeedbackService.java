package appointment_management.feedback;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing appointment feedback in the hospital system.
 * Handles all feedback-related operations including:
 * - Submission and storage of feedback
 * - Retrieval of feedback by various criteria
 * - Calculation of doctor ratings
 * - Persistence of feedback data in CSV format
 */
public class FeedbackService {
    /** File path for storing feedback data persistently */
    private static final String FEEDBACK_FILE = "SC2002HMS/data/AppointmentFeedback.csv";
    
    /** In-memory list of all feedback entries */
    private final List<AppointmentFeedback> feedbackList;

    /**
     * Constructs a new FeedbackService.
     * Initializes the feedback list and loads existing feedback from CSV file.
     * Creates feedback file if it doesn't exist.
     */
    public FeedbackService() {
        this.feedbackList = new ArrayList<>();
        loadFeedbackFromCsv();
    }

    /**
     * Submits new feedback into the system.
     * Adds feedback to in-memory list and persists to CSV file.
     * 
     * @param feedback The feedback entry to submit
     */
    public void submitFeedback(AppointmentFeedback feedback) {
        feedbackList.add(feedback);
        saveFeedbackToCsv();
    }

    /**
     * Retrieves all feedback for a specific doctor.
     * Filters feedback list by doctor ID.
     * 
     * @param doctorId ID of the doctor to get feedback for
     * @return List of feedback entries for the specified doctor
     */
    public List<AppointmentFeedback> getFeedbackByDoctor(String doctorId) {
        return feedbackList.stream()
            .filter(f -> f.getDoctorId().equals(doctorId))
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all feedback from a specific patient.
     * Filters feedback list by patient ID.
     * 
     * @param patientId ID of the patient to get feedback from
     * @return List of feedback entries from the specified patient
     */
    public List<AppointmentFeedback> getFeedbackByPatient(String patientId) {
        return feedbackList.stream()
            .filter(f -> f.getPatientId().equals(patientId))
            .collect(Collectors.toList());
    }

    /**
     * Retrieves feedback for a specific appointment.
     * Returns null if no feedback exists for the appointment.
     * 
     * @param appointmentId ID of the appointment to get feedback for
     * @return Feedback for the specified appointment, or null if none exists
     */
    public AppointmentFeedback getFeedbackByAppointment(String appointmentId) {
        return feedbackList.stream()
            .filter(f -> f.getAppointmentId().equals(appointmentId))
            .findFirst()
            .orElse(null);
    }

    /**
     * Calculates the average rating for a specific doctor.
     * Returns 0.0 if no feedback exists for the doctor.
     * 
     * @param doctorId ID of the doctor to calculate rating for
     * @return Average rating between 0.0 and 5.0
     */
    public double getAverageRatingForDoctor(String doctorId) {
        return feedbackList.stream()
            .filter(f -> f.getDoctorId().equals(doctorId))
            .mapToInt(AppointmentFeedback::getRating)
            .average()
            .orElse(0.0);
    }

    /**
     * Retrieves all feedback entries in the system.
     * Returns a new list to prevent external modification.
     * 
     * @return List of all feedback entries
     */
    public List<AppointmentFeedback> getAllFeedback() {
        return new ArrayList<>(feedbackList);
    }

    /**
     * Loads feedback data from CSV file into memory.
     * Creates new file with headers if it doesn't exist.
     * Handles file I/O operations and data parsing.
     */
    private void loadFeedbackFromCsv() {
        File file = new File(FEEDBACK_FILE);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                    writer.println("FeedbackId,AppointmentId,PatientId,DoctorId,Rating,Comment,SubmissionTime,IsAnonymous");
                }
            } catch (IOException e) {
                System.err.println("Error creating feedback file: " + e.getMessage());
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    feedbackList.add(new AppointmentFeedback(
                        parts[0], // feedbackId
                        parts[1], // appointmentId
                        parts[2], // patientId
                        parts[3], // doctorId
                        Integer.parseInt(parts[4]), // rating
                        parts[5], // comment
                        Boolean.parseBoolean(parts[7]) // isAnonymous
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading feedback: " + e.getMessage());
        }
    }

    /**
     * Saves all feedback data from memory to CSV file.
     * Overwrites existing file with current feedback list.
     * Includes headers and proper CSV formatting.
     */
    private void saveFeedbackToCsv() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FEEDBACK_FILE))) {
            writer.println("FeedbackId,AppointmentId,PatientId,DoctorId,Rating,Comment,SubmissionTime,IsAnonymous");
            for (AppointmentFeedback feedback : feedbackList) {
                writer.println(feedback.toCsvString());
            }
        } catch (IOException e) {
            System.err.println("Error saving feedback: " + e.getMessage());
        }
    }
}