package appointment_management;

import utils.CSVReaderUtil;
import java.time.LocalDateTime;

import java.time.format.TextStyle;
import java.util.*;

public class DoctorScheduleManager {
  private Map<String, Map<String, String>> doctorSchedules;
  private static final String SCHEDULE_FILE = "SC2002HMS/data/DoctorSchedules.csv";

  public DoctorScheduleManager() {
    this.doctorSchedules = new HashMap<>();
    loadSchedules();
  }

  private void loadSchedules() {
    List<String[]> records = CSVReaderUtil.readCSV(SCHEDULE_FILE);
    boolean isFirstRow = true;
    for (String[] record : records) {
      if (isFirstRow) {
        isFirstRow = false;
        continue;
      }
      String doctorId = record[0];
      String day = record[1];
      String timeSlot = record[2];

      doctorSchedules.computeIfAbsent(doctorId, k -> new HashMap<>())
          .put(day, timeSlot);
    }
  }

  public boolean isDoctorAvailable(String doctorId, LocalDateTime appointmentTime) {
    String dayOfWeek = appointmentTime.getDayOfWeek()
        .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

    Map<String, String> schedules = doctorSchedules.get(doctorId);
    if (schedules == null || !schedules.containsKey(dayOfWeek)) {
      return false;
    }

    String timeSlot = schedules.get(dayOfWeek);
    String[] hours = timeSlot.split("-");
    int startHour = Integer.parseInt(hours[0]);
    int endHour = Integer.parseInt(hours[1]);

    int appointmentHour = appointmentTime.getHour();
    return appointmentHour >= startHour && appointmentHour < endHour;
  }

  public List<String> getAvailableDays(String doctorId) {
    Map<String, String> schedules = doctorSchedules.get(doctorId);
    if (schedules == null) {
      return new ArrayList<>();
    }
    return new ArrayList<>(schedules.keySet());
  }

  public String getTimeSlot(String doctorId, String day) {
    Map<String, String> schedules = doctorSchedules.get(doctorId);
    if (schedules == null) {
      return null;
    }
    return schedules.get(day);
  }
}