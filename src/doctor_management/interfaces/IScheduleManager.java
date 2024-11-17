package doctor_management.interfaces;

import java.time.LocalDateTime;
import java.util.Map;

public interface IScheduleManager {
  void setAvailability(String doctorId, Map<String, String> weeklySchedule);

  Map<String, String> getAvailability(String doctorId);

  boolean isAvailable(String doctorId, LocalDateTime dateTime);
}
