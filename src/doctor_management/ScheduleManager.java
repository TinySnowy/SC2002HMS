package doctor_management;

import user_management.Doctor;
import java.util.Scanner;

public class ScheduleManager implements AutoCloseable {
    private final Scanner scanner;

    public ScheduleManager() {
        this.scanner = new Scanner(System.in);
    }

    public void updateAvailability(Doctor doctor) {
        try {
            System.out.println("\nUpdate Availability");
            System.out.println("----------------------------------------");
            System.out.println("Current Availability: " +
                    (doctor.getAvailability() == null ? "Not set" : doctor.getAvailability()));

            System.out.println("\nEnter availability for each day (or press Enter to skip):");
            StringBuilder availability = new StringBuilder();

            String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
            for (String day : days) {
                System.out.print(day + " (e.g., 9 AM - 5 PM): ");
                String time = scanner.nextLine().trim();
                if (!time.isEmpty()) {
                    if (availability.length() > 0) {
                        availability.append(", ");
                    }
                    availability.append(day).append(": ").append(time);
                }
            }

            if (availability.length() > 0) {
                doctor.setAvailability(availability.toString());
                System.out.println("\nAvailability updated successfully!");
            } else {
                System.out.println("\nNo availability set.");
            }

        } catch (Exception e) {
            System.err.println("Error updating availability: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}