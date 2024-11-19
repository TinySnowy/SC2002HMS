package doctor_management.interfaces;

import doctor_management.services.ScheduleManagerImpl.ScheduleEntry;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface defining the contract for managing doctor schedules and availability.
 * Provides standardized methods for:
 * - Setting and retrieving doctor availability
 * - Validating time slots
 * - Managing doctor schedules
 * - Checking appointment availability
 * Essential for coordinating doctor availability with appointment scheduling.
 */
public interface IScheduleManager {
    /**
     * Sets the availability schedule for a specific doctor.
     * Updates or creates new schedule entries for the doctor.
     * Should validate:
     * - Time slot conflicts
     * - Working hours policies
     * - Schedule overlap
     * 
     * @param doctorId Unique identifier of the doctor
     * @param scheduleEntries List of schedule entries defining available time slots
     * @throws IllegalArgumentException if doctorId is invalid or scheduleEntries is null
     * @throws IllegalStateException if schedule cannot be set (e.g., conflicts)
     */
    void setAvailability(String doctorId, List<ScheduleEntry> scheduleEntries);

    /**
     * Retrieves the current availability schedule for a specific doctor.
     * Returns all scheduled time slots, including:
     * - Regular office hours
     * - Special availability periods
     * - Blocked time slots
     * 
     * @param doctorId Unique identifier of the doctor
     * @return List of schedule entries showing available time slots
     * @throws IllegalArgumentException if doctorId is invalid
     */
    List<ScheduleEntry> getAvailability(String doctorId);

    /**
     * Checks if a doctor is available at a specific date and time.
     * Validates against:
     * - Scheduled availability
     * - Existing appointments
     * - Working hours policies
     * 
     * @param doctorId Unique identifier of the doctor
     * @param dateTime Specific date and time to check
     * @return true if doctor is available, false otherwise
     * @throws IllegalArgumentException if doctorId is invalid or dateTime is null
     */
    boolean isAvailable(String doctorId, LocalDateTime dateTime);

    /**
     * Validates a time slot format.
     * Ensures time format follows system standards.
     * Checks for:
     * - Valid time format (HH:mm)
     * - Within operating hours
     * - Proper interval alignment
     * 
     * @param time Time string to validate
     * @return true if time slot format is valid, false otherwise
     */
    boolean validateTimeSlot(String time);

    /**
     * Validates a date format and value.
     * Ensures date is:
     * - In valid format (yyyy-MM-dd)
     * - Not in the past
     * - Within scheduling window
     * 
     * @param date Date string to validate
     * @return true if date is valid, false otherwise
     */
    boolean validateDate(String date);
}