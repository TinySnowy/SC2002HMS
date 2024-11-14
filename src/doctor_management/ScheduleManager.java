package doctor_management;

import java.util.Scanner;

public class ScheduleManager {
    public void updateAvailability(Doctor doctor) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Current Availability: " + doctor.getAvailability());
        System.out.print("Enter new availability (e.g., Mon-Fri: 9 AM - 5 PM): ");
        String newAvailability = scanner.nextLine();
        doctor.setAvailability(newAvailability);
        System.out.println("Availability updated successfully!");
    }
}
