package appointment_management.feedback;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FeedbackService {
    private static final String FEEDBACK_FILE = "SC2002HMS/data/AppointmentFeedback.csv";
    private final List<AppointmentFeedback> feedbackList;

    public FeedbackService() {
        this.feedbackList = new ArrayList<>();
        loadFeedbackFromCsv();
    }

    public void submitFeedback(AppointmentFeedback feedback) {
        feedbackList.add(feedback);
        saveFeedbackToCsv();
    }

    public List<AppointmentFeedback> getFeedbackByDoctor(String doctorId) {
        return feedbackList.stream()
            .filter(f -> f.getDoctorId().equals(doctorId))
            .collect(Collectors.toList());
    }

    public List<AppointmentFeedback> getFeedbackByPatient(String patientId) {
        return feedbackList.stream()
            .filter(f -> f.getPatientId().equals(patientId))
            .collect(Collectors.toList());
    }

    public AppointmentFeedback getFeedbackByAppointment(String appointmentId) {
        return feedbackList.stream()
            .filter(f -> f.getAppointmentId().equals(appointmentId))
            .findFirst()
            .orElse(null);
    }

    public double getAverageRatingForDoctor(String doctorId) {
        return feedbackList.stream()
            .filter(f -> f.getDoctorId().equals(doctorId))
            .mapToInt(AppointmentFeedback::getRating)
            .average()
            .orElse(0.0);
    }

    public List<AppointmentFeedback> getAllFeedback() {
        return new ArrayList<>(feedbackList);
    }

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