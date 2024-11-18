package doctor_management.interfaces;

import doctor_management.services.ScheduleManagerImpl.ScheduleEntry;
import java.time.LocalDateTime;
import java.util.List;

public interface IScheduleManager {
    void setAvailability(String doctorId, List<ScheduleEntry> scheduleEntries);
    List<ScheduleEntry> getAvailability(String doctorId);
    boolean isAvailable(String doctorId, LocalDateTime dateTime);
    boolean validateTimeSlot(String time);
    boolean validateDate(String date);
}