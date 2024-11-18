package appointment_management;

import utils.CSVReaderUtil;
import utils.CSVWriterUtil;
import doctor_management.services.ScheduleManagerImpl.TimeSlot;
import doctor_management.services.ScheduleManagerImpl.ScheduleEntry;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DoctorScheduleManager {
  private Map<String, List<ScheduleEntry>> doctorSchedules;
  private final AppointmentList appointmentList;
  private static final String SCHEDULE_FILE = "SC2002HMS/data/DoctorSchedules.csv";
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  public DoctorScheduleManager() {
      this.doctorSchedules = new HashMap<>();
      this.appointmentList = new AppointmentList();
      loadSchedules();
      System.out.println("DoctorScheduleManager initialized with " + doctorSchedules.size() + " schedules"); // Debug
  }

  private void loadSchedules() {
      List<String[]> records = CSVReaderUtil.readCSV(SCHEDULE_FILE);
      boolean isFirstRow = true;
      
      System.out.println("Loading doctor schedules from: " + SCHEDULE_FILE); // Debug
      
      for (String[] record : records) {
          if (isFirstRow) {
              isFirstRow = false;
              continue;
          }
          if (record.length >= 4) {
              String doctorId = record[0].trim();
              String dateStr = record[1].trim();
              String startTime = record[2].trim();
              String endTime = record[3].trim();

              try {
                  LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
                  TimeSlot timeSlot = new TimeSlot(startTime, endTime);
                  ScheduleEntry entry = new ScheduleEntry(date, timeSlot);
                  
                  List<ScheduleEntry> doctorEntries = doctorSchedules.computeIfAbsent(
                      doctorId, k -> new ArrayList<>());
                  doctorEntries.add(entry);
                  
                  System.out.println("Loaded schedule for doctor " + doctorId + 
                                   " on " + dateStr + " (" + startTime + "-" + endTime + ")"); // Debug
              } catch (Exception e) {
                  System.err.println("Error loading schedule entry: " + e.getMessage());
              }
          }
      }
  }

  public List<ScheduleEntry> getAvailableSlots(String doctorId) {
      List<ScheduleEntry> availableSlots = new ArrayList<>();
      List<ScheduleEntry> doctorSlots = doctorSchedules.get(doctorId);
      
      System.out.println("Finding available slots for doctor: " + doctorId); // Debug
      
      if (doctorSlots == null || doctorSlots.isEmpty()) {
          System.out.println("No schedule found for doctor: " + doctorId); // Debug
          return availableSlots;
      }

      List<Appointment> doctorAppointments = appointmentList.getAppointmentsForDoctor(doctorId);
      System.out.println("Found " + doctorAppointments.size() + " existing appointments"); // Debug
      
      LocalDateTime now = LocalDateTime.now();

      for (ScheduleEntry entry : doctorSlots) {
          LocalDate date = entry.getDate();
          TimeSlot timeSlot = entry.getTimeSlot();
          
          List<TimeSlot> halfHourSlots = generateHalfHourSlots(
              timeSlot.getStartTime(), 
              timeSlot.getEndTime()
          );

          System.out.println("Generated " + halfHourSlots.size() + 
                           " half-hour slots for " + date); // Debug

          for (TimeSlot slot : halfHourSlots) {
              LocalDateTime slotDateTime = date.atTime(
                  LocalTime.parse(slot.getStartTime(), TIME_FORMATTER));
              
              if (slotDateTime.isAfter(now) && 
                  !isTimeSlotBooked(slotDateTime, doctorAppointments)) {
                  availableSlots.add(new ScheduleEntry(date, slot));
              }
          }
      }
      
      System.out.println("Found " + availableSlots.size() + " available slots"); // Debug
      return availableSlots;
  }

  private boolean isTimeSlotBooked(LocalDateTime slotDateTime, List<Appointment> doctorAppointments) {
      boolean isBooked = doctorAppointments.stream()
          .filter(a -> !a.getStatus().equalsIgnoreCase("Cancelled"))
          .anyMatch(appointment -> {
              LocalDateTime appointmentTime = appointment.getAppointmentDate();
              boolean timeMatch = appointmentTime.toLocalDate().equals(slotDateTime.toLocalDate()) &&
                                appointmentTime.getHour() == slotDateTime.getHour() &&
                                appointmentTime.getMinute() == slotDateTime.getMinute();
              if (timeMatch) {
                  System.out.println("Slot " + slotDateTime + " is booked with appointment ID: " + 
                                   appointment.getAppointmentId()); // Debug
              }
              return timeMatch;
          });
      
      return isBooked;
  }

  private List<TimeSlot> generateHalfHourSlots(String startTimeStr, String endTimeStr) {
      List<TimeSlot> slots = new ArrayList<>();
      try {
          LocalTime startTime = LocalTime.parse(startTimeStr, TIME_FORMATTER);
          LocalTime endTime = LocalTime.parse(endTimeStr, TIME_FORMATTER);
          LocalTime currentTime = startTime;

          while (currentTime.isBefore(endTime)) {
              String currentTimeStr = currentTime.format(TIME_FORMATTER);
              String nextTimeStr = currentTime.plusMinutes(30).format(TIME_FORMATTER);
              slots.add(new TimeSlot(currentTimeStr, nextTimeStr));
              currentTime = currentTime.plusMinutes(30);
          }
      } catch (Exception e) {
          System.err.println("Error generating time slots: " + e.getMessage());
      }
      return slots;
  }
}