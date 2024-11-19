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

/**
 * Manages doctor schedules and availability in the hospital management system.
 * Handles:
 * - Doctor schedule tracking
 * - Available time slot management
 * - Schedule conflicts
 * - Appointment time validation
 * Provides interface for schedule operations and availability checks.
 */
public class DoctorScheduleManager {
  /** Maps doctor IDs to their schedule entries */
  private Map<String, List<ScheduleEntry>> doctorSchedules;
  /** Reference to appointment list for checking conflicts */
  private final AppointmentList appointmentList;
  /** File path for storing doctor schedules */
  private static final String SCHEDULE_FILE = "SC2002HMS/data/DoctorSchedules.csv";
  /** Formatter for parsing and formatting dates */
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  /** Formatter for parsing and formatting times */
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  /**
   * Constructs a new DoctorScheduleManager.
   * Initializes schedule storage and loads existing schedules.
   * Sets up appointment tracking for conflict checking.
   */
  public DoctorScheduleManager() {
      this.doctorSchedules = new HashMap<>();
      this.appointmentList = new AppointmentList();
      loadSchedules();
  }

  /**
   * Loads doctor schedules from CSV storage.
   * Parses schedule data and creates schedule entries.
   * Handles data validation and format conversion.
   */
  private void loadSchedules() {
      List<String[]> records = CSVReaderUtil.readCSV(SCHEDULE_FILE);
      boolean isFirstRow = true;
      
      
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
                  

              } catch (Exception e) {
                  System.err.println("Error loading schedule entry: " + e.getMessage());
              }
          }
      }
  }

  /**
   * Retrieves available time slots for a specific doctor.
   * Filters out booked and past time slots.
   * Breaks available time into 30-minute intervals.
   * 
   * @param doctorId ID of the doctor to check
   * @return List of available schedule entries
   */
  public List<ScheduleEntry> getAvailableSlots(String doctorId) {
      List<ScheduleEntry> availableSlots = new ArrayList<>();
      List<ScheduleEntry> doctorSlots = doctorSchedules.get(doctorId);
      
      
      if (doctorSlots == null || doctorSlots.isEmpty()) {
          return availableSlots;
      }

      List<Appointment> doctorAppointments = appointmentList.getAppointmentsForDoctor(doctorId);
      
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

  /**
   * Checks if a specific time slot is already booked.
   * Compares slot with existing appointments.
   * Excludes cancelled appointments from consideration.
   * 
   * @param slotDateTime DateTime to check
   * @param doctorAppointments List of doctor's appointments
   * @return true if slot is booked, false otherwise
   */
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

  /**
   * Generates 30-minute time slots within a time range.
   * Splits a larger time block into standard appointment intervals.
   * 
   * @param startTimeStr Start time of the range
   * @param endTimeStr End time of the range
   * @return List of 30-minute time slots
   */
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