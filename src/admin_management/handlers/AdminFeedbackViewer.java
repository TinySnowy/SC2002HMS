package admin_management.handlers;

import appointment_management.feedback.AppointmentFeedback;
import appointment_management.feedback.FeedbackService;
import appointment_management.AppointmentList;
import user_management.UserController;
import user_management.Doctor;
import java.io.*;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class AdminFeedbackViewer {
    private final FeedbackService feedbackService;
    private final AppointmentList appointmentList;
    private final UserController userController;
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String REPORT_PATH = "SC2002HMS/reports/feedback/";

    public AdminFeedbackViewer(FeedbackService feedbackService, AppointmentList appointmentList, UserController userController) {
        this.feedbackService = feedbackService;
        this.appointmentList = appointmentList;
        this.userController = userController;
        this.scanner = new Scanner(System.in);
        createReportDirectory();
    }

    private void createReportDirectory() {
        File directory = new File(REPORT_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public void viewOverallSummary() {
        System.out.println("\nOverall Feedback Summary");
        System.out.println("----------------------------------------");

        List<AppointmentFeedback> allFeedback = feedbackService.getAllFeedback();
        if (allFeedback.isEmpty()) {
            System.out.println("No feedback received yet.");
            return;
        }

        printFeedbackStatistics(allFeedback);
        pressEnterToContinue();
    }

    public void viewFeedbackByDoctor() {
        List<Doctor> doctors = userController.getAllDoctors();
        if (doctors.isEmpty()) {
            System.out.println("No doctors found in the system.");
            return;
        }

        displayDoctorList(doctors);
        Doctor selectedDoctor = selectDoctor(doctors);
        if (selectedDoctor != null) {
            displayDoctorFeedback(selectedDoctor);
        }
    }

    public void viewRecentFeedback() {
        System.out.println("\nRecent Feedback");
        System.out.println("----------------------------------------");
        displayRecentFeedback(10);
        pressEnterToContinue();
    }

    public void generateFeedbackReport() {
        System.out.println("\nGenerate Feedback Report");
        System.out.println("----------------------------------------");
        System.out.println("1. Overall Hospital Report");
        System.out.println("2. Doctor-specific Report");
        System.out.println("3. Back");

        try {
            System.out.print("Enter your choice: ");
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            switch (choice) {
                case 1: generateOverallReport(); break;
                case 2: generateDoctorReport(); break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    public void displayDoctorFeedback(Doctor doctor) {
        System.out.printf("\nFeedback for Dr. %s (%s)\n", doctor.getName(), doctor.getSpecialty());
        System.out.println("----------------------------------------");

        List<AppointmentFeedback> doctorFeedback = feedbackService.getFeedbackByDoctor(doctor.getId());
        if (doctorFeedback.isEmpty()) {
            System.out.println("No feedback received yet for this doctor.");
            return;
        }

        double averageRating = feedbackService.getAverageRatingForDoctor(doctor.getId());
        System.out.printf("Average Rating: %.1f/5.0 (Total: %d reviews)\n\n", 
            averageRating, doctorFeedback.size());

        doctorFeedback.stream()
            .sorted((f1, f2) -> f2.getSubmissionTime().compareTo(f1.getSubmissionTime()))
            .forEach(feedback -> {
                System.out.printf("Date: %s\n", feedback.getSubmissionTime().format(DATE_FORMATTER));
                System.out.printf("Rating: %d/5\n", feedback.getRating());
                if (!feedback.isAnonymous()) {
                    System.out.println("Patient ID: " + feedback.getPatientId());
                }
                if (!feedback.getComment().isEmpty()) {
                    System.out.println("Comment: " + feedback.getComment());
                }
                System.out.println("----------------------------------------");
            });

        pressEnterToContinue();
    }

    private void generateOverallReport() {
        List<AppointmentFeedback> allFeedback = feedbackService.getAllFeedback();
        if (allFeedback.isEmpty()) {
            System.out.println("No feedback data available to generate report.");
            return;
        }

        String filename = String.format("%shospital_feedback_report_%s.txt", 
            REPORT_PATH,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Hospital Feedback Report");
            writer.println("Generated: " + LocalDateTime.now().format(DATE_FORMATTER));
            writer.println("========================================\n");

            writeOverallStatistics(writer, allFeedback);
            writeDoctorStatistics(writer);
            writeRecentFeedback(writer, allFeedback);

            System.out.println("Report generated successfully: " + filename);
        } catch (IOException e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    private void generateDoctorReport() {
        List<Doctor> doctors = userController.getAllDoctors();
        displayDoctorList(doctors);
        Doctor selectedDoctor = selectDoctor(doctors);
        
        if (selectedDoctor == null) return;

        List<AppointmentFeedback> doctorFeedback = feedbackService.getFeedbackByDoctor(selectedDoctor.getId());
        if (doctorFeedback.isEmpty()) {
            System.out.println("No feedback data available for this doctor.");
            return;
        }

        String filename = String.format("%sdoctor_%s_feedback_report_%s.txt", 
            REPORT_PATH,
            selectedDoctor.getId(),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.printf("Feedback Report - Dr. %s (%s)\n", 
                selectedDoctor.getName(), selectedDoctor.getSpecialty());
            writer.println("Generated: " + LocalDateTime.now().format(DATE_FORMATTER));
            writer.println("========================================\n");

            writeDoctorFeedbackReport(writer, selectedDoctor, doctorFeedback);
            
            System.out.println("Report generated successfully: " + filename);
        } catch (IOException e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    private void writeOverallStatistics(PrintWriter writer, List<AppointmentFeedback> feedback) {
        double averageRating = feedback.stream()
            .mapToInt(AppointmentFeedback::getRating)
            .average()
            .orElse(0.0);

        Map<Integer, Long> distribution = feedback.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                AppointmentFeedback::getRating,
                java.util.stream.Collectors.counting()));

        writer.println("Overall Statistics");
        writer.println("----------------------------------------");
        writer.printf("Total Feedback: %d\n", feedback.size());
        writer.printf("Average Rating: %.1f/5.0\n\n", averageRating);
        writer.println("Rating Distribution:");
        
        for (int i = 5; i >= 1; i--) {
            long count = distribution.getOrDefault(i, 0L);
            double percentage = (count * 100.0) / feedback.size();
            writer.printf("%d stars: %.1f%% (%d reviews)\n", i, percentage, count);
        }
        writer.println();
    }

    private void writeDoctorStatistics(PrintWriter writer) {
        writer.println("Doctor-wise Statistics");
        writer.println("----------------------------------------");
        
        List<Doctor> doctors = userController.getAllDoctors();
        for (Doctor doctor : doctors) {
            double avgRating = feedbackService.getAverageRatingForDoctor(doctor.getId());
            List<AppointmentFeedback> doctorFeedback = feedbackService.getFeedbackByDoctor(doctor.getId());
            
            writer.printf("Dr. %s (%s)\n", doctor.getName(), doctor.getSpecialty());
            writer.printf("Average Rating: %.1f/5.0 (%d reviews)\n\n", 
                avgRating, doctorFeedback.size());
        }
        writer.println();
    }

    private void writeRecentFeedback(PrintWriter writer, List<AppointmentFeedback> feedback) {
        writer.println("Recent Feedback");
        writer.println("----------------------------------------");
        
        feedback.stream()
            .sorted((f1, f2) -> f2.getSubmissionTime().compareTo(f1.getSubmissionTime()))
            .limit(10)
            .forEach(f -> {
                Doctor doctor = (Doctor) userController.getUserById(f.getDoctorId());
                writer.printf("Doctor: Dr. %s\n", doctor.getName());
                writer.printf("Date: %s\n", f.getSubmissionTime().format(DATE_FORMATTER));
                writer.printf("Rating: %d/5\n", f.getRating());
                if (!f.getComment().isEmpty()) {
                    writer.printf("Comment: %s\n", f.getComment());
                }
                writer.println("----------------------------------------");
            });
    }

    private void writeDoctorFeedbackReport(PrintWriter writer, Doctor doctor, List<AppointmentFeedback> feedback) {
        double avgRating = feedbackService.getAverageRatingForDoctor(doctor.getId());
        
        writer.printf("Average Rating: %.1f/5.0 (%d reviews)\n\n", avgRating, feedback.size());
        
        Map<Integer, Long> distribution = feedback.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                AppointmentFeedback::getRating,
                java.util.stream.Collectors.counting()));

        writer.println("Rating Distribution:");
        for (int i = 5; i >= 1; i--) {
            long count = distribution.getOrDefault(i, 0L);
            double percentage = (count * 100.0) / feedback.size();
            writer.printf("%d stars: %.1f%% (%d reviews)\n", i, percentage, count);
        }

        writer.println("\nDetailed Feedback:");
        writer.println("----------------------------------------");
        
        feedback.stream()
            .sorted((f1, f2) -> f2.getSubmissionTime().compareTo(f1.getSubmissionTime()))
            .forEach(f -> {
                writer.printf("Date: %s\n", f.getSubmissionTime().format(DATE_FORMATTER));
                writer.printf("Rating: %d/5\n", f.getRating());
                if (!f.isAnonymous()) {
                    writer.println("Patient ID: " + f.getPatientId());
                }
                if (!f.getComment().isEmpty()) {
                    writer.println("Comment: " + f.getComment());
                }
                writer.println("----------------------------------------");
            });
    }

    private void displayDoctorList(List<Doctor> doctors) {
        System.out.println("\nSelect Doctor:");
        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            System.out.printf("%d. Dr. %s (%s)\n", i + 1, doctor.getName(), doctor.getSpecialty());
        }
    }

    private Doctor selectDoctor(List<Doctor> doctors) {
        try {
            System.out.print("Enter doctor number (or 0 to go back): ");
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == 0) return null;
            if (choice > 0 && choice <= doctors.size()) {
                return doctors.get(choice - 1);
            }
            System.out.println("Invalid selection.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
        return null;
    }

    private void displayRecentFeedback(int limit) {
        feedbackService.getAllFeedback().stream()
            .sorted((f1, f2) -> f2.getSubmissionTime().compareTo(f1.getSubmissionTime()))
            .limit(limit)
            .forEach(this::displayFeedbackEntry);
    }

    private void displayFeedbackEntry(AppointmentFeedback feedback) {
        Doctor doctor = (Doctor) userController.getUserById(feedback.getDoctorId());
        System.out.printf("Doctor: Dr. %s\n", doctor.getName());
        System.out.printf("Date: %s\n", feedback.getSubmissionTime().format(DATE_FORMATTER));
        System.out.printf("Rating: %d/5\n", feedback.getRating());
        if (!feedback.getComment().isEmpty()) {
            System.out.println("Comment: " + feedback.getComment());
        }
        System.out.println("----------------------------------------");
    }

    private void printFeedbackStatistics(List<AppointmentFeedback> feedback) {
        double averageRating = feedback.stream()
            .mapToInt(AppointmentFeedback::getRating)
            .average()
            .orElse(0.0);

        Map<Integer, Long> distribution = feedback.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                AppointmentFeedback::getRating,
                java.util.stream.Collectors.counting()));

        System.out.printf("Total Feedback: %d\n", feedback.size());
        System.out.printf("Average Rating: %.1f/5.0\n\n", averageRating);
        
        System.out.println("Rating Distribution:");
        for (int i = 5; i >= 1; i--) {
            long count = distribution.getOrDefault(i, 0L);
            double percentage = (count * 100.0) / feedback.size();
            System.out.printf("%d stars: %s (%.1f%%) - %d reviews\n", 
                i, "★".repeat((int)(percentage/5)), percentage, count);
        }
    }

    private void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}