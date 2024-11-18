package doctor_management.services;

import doctor_management.interfaces.IScheduleManager;
import utils.CSVReaderUtil;
import utils.CSVWriterUtil;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ScheduleManagerImpl implements IScheduleManager {
    private final Map<String, DoctorSchedule> doctorSchedules;
    private static final String SCHEDULE_FILE = "SC2002HMS/data/DoctorSchedules.csv";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ScheduleManagerImpl() {
        this.doctorSchedules = new HashMap<>();
        loadSchedulesFromCSV();
    }

    @Override
    public void setAvailability(String doctorId, List<ScheduleEntry> scheduleEntries) {
        DoctorSchedule schedule = new DoctorSchedule(scheduleEntries);
        doctorSchedules.put(doctorId, schedule);
        saveSchedulesToCSV();
    }

    @Override
    public List<ScheduleEntry> getAvailability(String doctorId) {
        DoctorSchedule schedule = doctorSchedules.get(doctorId);
        return schedule != null ? schedule.getScheduleEntries() : new ArrayList<>();
    }

    @Override
    public boolean isAvailable(String doctorId, LocalDateTime dateTime) {
        DoctorSchedule schedule = doctorSchedules.get(doctorId);
        if (schedule == null) return false;
        return schedule.isTimeSlotAvailable(dateTime);
    }

    @Override
    public boolean validateTimeSlot(String time) {
        try {
            LocalTime proposedTime = LocalTime.parse(time, TIME_FORMATTER);
            return proposedTime.getMinute() % 30 == 0;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @Override
    public boolean validateDate(String date) {
        try {
            LocalDate.parse(date, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
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
                String date = record[1];
                String startTime = record[2];
                String endTime = record[3];

                DoctorSchedule schedule = doctorSchedules.computeIfAbsent(
                    doctorId, k -> new DoctorSchedule(new ArrayList<>()));
                
                schedule.addScheduleEntry(new ScheduleEntry(
                    LocalDate.parse(date, DATE_FORMATTER),
                    new TimeSlot(startTime, endTime)
                ));
            }
        } catch (Exception e) {
            System.err.println("Error loading schedules: " + e.getMessage());
        }
    }

    private void saveSchedulesToCSV() {
        try {
            CSVWriterUtil.writeCSV(SCHEDULE_FILE, writer -> {
                writer.write("DoctorID,Date,StartTime,EndTime\n");
                for (Map.Entry<String, DoctorSchedule> entry : doctorSchedules.entrySet()) {
                    String doctorId = entry.getKey();
                    DoctorSchedule schedule = entry.getValue();

                    for (ScheduleEntry scheduleEntry : schedule.getScheduleEntries()) {
                        TimeSlot timeSlot = scheduleEntry.getTimeSlot();
                        writer.write(String.format("%s,%s,%s,%s\n",
                            doctorId,
                            scheduleEntry.getDate().format(DATE_FORMATTER),
                            timeSlot.getStartTime(),
                            timeSlot.getEndTime()));
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("Error saving schedules: " + e.getMessage());
        }
    }

    // Inner class to represent a time slot with validation
    public static class TimeSlot {
        private final LocalTime startTime;
        private final LocalTime endTime;

        public TimeSlot(String startTime, String endTime) {
            this.startTime = LocalTime.parse(startTime, TIME_FORMATTER);
            this.endTime = LocalTime.parse(endTime, TIME_FORMATTER);
            validateTimeSlot();
        }

        private void validateTimeSlot() {
            if (startTime.getMinute() % 30 != 0 || endTime.getMinute() % 30 != 0) {
                throw new IllegalArgumentException("Times must be in half-hour intervals");
            }
            if (!startTime.isBefore(endTime)) {
                throw new IllegalArgumentException("Start time must be before end time");
            }
        }

        public boolean isTimeWithinSlot(LocalTime time) {
            return !time.isBefore(startTime) && !time.isAfter(endTime);
        }

        public String getStartTime() {
            return startTime.format(TIME_FORMATTER);
        }

        public String getEndTime() {
            return endTime.format(TIME_FORMATTER);
        }

        @Override
        public String toString() {
            return String.format("%s-%s", getStartTime(), getEndTime());
        }
    }

    // Class to represent a schedule entry (date + time slot)
    public static class ScheduleEntry {
        private final LocalDate date;
        private final TimeSlot timeSlot;

        public ScheduleEntry(LocalDate date, TimeSlot timeSlot) {
            this.date = date;
            this.timeSlot = timeSlot;
        }

        public LocalDate getDate() {
            return date;
        }

        public TimeSlot getTimeSlot() {
            return timeSlot;
        }

        public boolean isDateTimeWithinSlot(LocalDateTime dateTime) {
            return date.equals(dateTime.toLocalDate()) && 
                   timeSlot.isTimeWithinSlot(dateTime.toLocalTime());
        }

        @Override
        public String toString() {
            return String.format("%s: %s", date.format(DATE_FORMATTER), timeSlot);
        }
    }

    // Inner class to represent a doctor's complete schedule
    private static class DoctorSchedule {
        private final List<ScheduleEntry> scheduleEntries;

        public DoctorSchedule(List<ScheduleEntry> scheduleEntries) {
            this.scheduleEntries = new ArrayList<>(scheduleEntries);
        }

        public void addScheduleEntry(ScheduleEntry entry) {
            scheduleEntries.add(entry);
        }

        public boolean isTimeSlotAvailable(LocalDateTime dateTime) {
            return scheduleEntries.stream()
                .anyMatch(entry -> entry.isDateTimeWithinSlot(dateTime));
        }

        public List<ScheduleEntry> getScheduleEntries() {
            return new ArrayList<>(scheduleEntries);
        }
    }
}