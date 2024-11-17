package doctor_management.services;

import doctor_management.interfaces.IScheduleManager;
import utils.CSVReaderUtil;
import utils.CSVWriterUtil;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleManagerImpl implements IScheduleManager {
  private final Map<String, Map<String, String>> doctorSchedules;
  private static final String SCHEDULE_FILE = "SC2002HMS/data/DoctorSchedules.csv";
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  public ScheduleManagerImpl() {
    this.doctorSchedules = new HashMap<>();
    loadSchedulesFromCSV();
  }

  @Override
  public void setAvailability(String doctorId, Map<String, String> weeklySchedule) {
    doctorSchedules.put(doctorId, new HashMap<>(weeklySchedule));
    saveSchedulesToCSV();
  }

  @Override
  public Map<String, String> getAvailability(String doctorId) {
    return new HashMap<>(doctorSchedules.getOrDefault(doctorId, new HashMap<>()));
  }

  @Override
  public boolean isAvailable(String doctorId, LocalDateTime dateTime) {
    Map<String, String> schedule = doctorSchedules.get(doctorId);
    if (schedule == null)
      return false;

    String dayOfWeek = dateTime.getDayOfWeek().toString();
    String timeSlot = schedule.get(dayOfWeek);
    return isTimeWithinSlot(dateTime.toLocalTime(), timeSlot);
  }

  private boolean isTimeWithinSlot(LocalTime time, String timeSlot) {
    if (timeSlot == null || timeSlot.isEmpty())
      return false;

    try {
      String[] parts = timeSlot.split("-");
      if (parts.length != 2)
        return false;

      LocalTime start = LocalTime.parse(parts[0].trim(), TIME_FORMATTER);
      LocalTime end = LocalTime.parse(parts[1].trim(), TIME_FORMATTER);

      return !time.isBefore(start) && !time.isAfter(end);
    } catch (Exception e) {
      System.err.println("Error parsing time slot: " + e.getMessage());
      return false;
    }
  }

  private void loadSchedulesFromCSV() {
    try {
      List<String[]> records = CSVReaderUtil.readCSV(SCHEDULE_FILE);
      boolean isFirstRow = true;

      for (String[] record : records) {
        if (isFirstRow) {
          isFirstRow = false;
          continue;
        }

        String doctorId = record[0];
        Map<String, String> schedule = doctorSchedules
            .computeIfAbsent(doctorId, k -> new HashMap<>());
        schedule.put(record[1], record[2]);
      }
    } catch (Exception e) {
      System.err.println("Error loading schedules: " + e.getMessage());
    }
  }

  private void saveSchedulesToCSV() {
    try {
      CSVWriterUtil.writeCSV(SCHEDULE_FILE, writer -> {
        writer.write("DoctorID,Day,TimeSlot\n");
        for (Map.Entry<String, Map<String, String>> entry : doctorSchedules.entrySet()) {
          String doctorId = entry.getKey();
          Map<String, String> schedule = entry.getValue();

          for (Map.Entry<String, String> scheduleEntry : schedule.entrySet()) {
            writer.write(String.format("%s,%s,%s\n",
                doctorId,
                scheduleEntry.getKey(),
                scheduleEntry.getValue()));
          }
        }
      });
    } catch (Exception e) {
      System.err.println("Error saving schedules: " + e.getMessage());
    }
  }
}
