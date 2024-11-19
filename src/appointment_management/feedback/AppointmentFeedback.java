package appointment_management.feedback;

import java.time.LocalDateTime;

public class AppointmentFeedback {
    private final String feedbackId;
    private final String appointmentId;
    private final String patientId;
    private final String doctorId;
    private final int rating;           // 1-5 rating
    private final String comment;
    private final LocalDateTime submissionTime;
    private boolean isAnonymous;

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

    // Getters
    public String getFeedbackId() { return feedbackId; }
    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getSubmissionTime() { return submissionTime; }
    public boolean isAnonymous() { return isAnonymous; }

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

    @Override
    public String toString() {
        return String.format("Rating: %d/5\nComment: %s\nSubmitted: %s", 
            rating, comment, submissionTime);
    }
}