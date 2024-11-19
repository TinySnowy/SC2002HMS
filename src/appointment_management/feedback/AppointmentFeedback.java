package appointment_management.feedback;

import java.time.LocalDateTime;

/**
 * Represents feedback for a hospital appointment.
 * Stores and manages patient feedback data including ratings, comments,
 * and submission details. Supports both anonymous and identified feedback.
 * Critical for quality assurance and service improvement monitoring.
 */
public class AppointmentFeedback {
    /** Unique identifier for the feedback entry */
    private final String feedbackId;
    
    /** Reference to the appointment this feedback is for */
    private final String appointmentId;
    
    /** ID of the patient who submitted the feedback */
    private final String patientId;
    
    /** ID of the doctor being evaluated */
    private final String doctorId;
    
    /** Numerical rating (1-5 scale) given by the patient */
    private final int rating;
    
    /** Detailed comment or review provided by the patient */
    private final String comment;
    
    /** Timestamp when the feedback was submitted */
    private final LocalDateTime submissionTime;
    
    /** Flag indicating if the feedback should be displayed anonymously */
    private boolean isAnonymous;

    /**
     * Constructs a new feedback entry with all necessary details.
     * Automatically sets submission time to current timestamp.
     * 
     * @param feedbackId Unique identifier for this feedback
     * @param appointmentId Associated appointment identifier
     * @param patientId ID of the feedback provider
     * @param doctorId ID of the doctor being reviewed
     * @param rating Numerical rating (1-5)
     * @param comment Detailed feedback text
     * @param isAnonymous Whether to hide patient identity
     */
    public AppointmentFeedback(String feedbackId, String appointmentId, String patientId, 
                             String doctorId, int rating, String comment, 
                             boolean isAnonymous) {
        this.feedbackId = feedbackId;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.rating = rating;
        this.comment = comment;
        this.isAnonymous = isAnonymous;
        this.submissionTime = LocalDateTime.now();
    }

    /**
     * Getter for feedback unique identifier
     * @return Feedback ID
     */
    public String getFeedbackId() { return feedbackId; }

    /**
     * Getter for associated appointment ID
     * @return Appointment ID
     */
    public String getAppointmentId() { return appointmentId; }

    /**
     * Getter for patient identifier
     * @return Patient ID
     */
    public String getPatientId() { return patientId; }

    /**
     * Getter for doctor identifier
     * @return Doctor ID
     */
    public String getDoctorId() { return doctorId; }

    /**
     * Getter for numerical rating
     * @return Rating value (1-5)
     */
    public int getRating() { return rating; }

    /**
     * Getter for feedback comment
     * @return Detailed feedback text
     */
    public String getComment() { return comment; }

    /**
     * Getter for submission timestamp
     * @return DateTime when feedback was submitted
     */
    public LocalDateTime getSubmissionTime() { return submissionTime; }

    /**
     * Checks if feedback is anonymous
     * @return true if feedback should be displayed anonymously
     */
    public boolean isAnonymous() { return isAnonymous; }

    /**
     * Converts feedback to CSV format for storage.
     * Escapes commas in comments to prevent CSV parsing issues.
     * Format: feedbackId,appointmentId,patientId,doctorId,rating,comment,submissionTime,isAnonymous
     * 
     * @return CSV formatted string representation of feedback
     */
    public String toCsvString() {
        return String.join(",",
            feedbackId,
            appointmentId,
            patientId,
            doctorId,
            String.valueOf(rating),
            comment.replace(",", ";"),  // Escape commas in comments
            submissionTime.toString(),
            String.valueOf(isAnonymous)
        );
    }

    /**
     * Provides string representation of feedback for display.
     * Shows rating, comment, and submission time in formatted layout.
     * 
     * @return Formatted string representation of feedback
     */
    @Override
    public String toString() {
        return String.format("Rating: %d/5\nComment: %s\nSubmitted: %s", 
            rating, comment, submissionTime);
    }
}