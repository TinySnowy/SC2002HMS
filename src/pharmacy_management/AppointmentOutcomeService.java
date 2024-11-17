package pharmacy_management;

import java.time.LocalDateTime;
import java.util.*;
import user_management.Patient;
import utils.CSVReaderUtil;
import user_management.UserController;
import patient_management.MedicalRecordController;
import patient_management.MedicalRecord;

public class AppointmentOutcomeService implements IAppointmentOutcomeService {
  private final Map<String, AppointmentOutcome> outcomes = new HashMap<>();
  private final UserController userController;
  private final MedicalRecordController medicalRecordController;
  private static final String APPOINTMENTS_FILE = "SC2002HMS/data/Appointments.csv";

  public AppointmentOutcomeService(UserController userController) {
    this.userController = userController;
    this.medicalRecordController = new MedicalRecordController();
    loadOutcomes();
  }

  private void loadOutcomes() {
    List<String[]> records = CSVReaderUtil.readCSV(APPOINTMENTS_FILE);
    boolean isFirstRow = true;

    for (String[] record : records) {
      if (isFirstRow) {
        isFirstRow = false;
        continue;
      }

      try {
        String appointmentId = record[0];
        String patientId = record[1];
        String doctorId = record[2];
        LocalDateTime date = LocalDateTime.parse(record[3].replace(" ", "T"));
        // String status = record[4];
        String serviceType = record[5];
        String notes = record.length > 6 ? record[6] : "";

        AppointmentOutcome outcome = new AppointmentOutcome(
            appointmentId, patientId, doctorId, date, serviceType, notes);

        // Load prescription from medical record if it exists
        MedicalRecord medRecord = medicalRecordController.getRecordByPatientId(patientId);
        if (medRecord != null && !medRecord.getPrescription().isEmpty()) {
          Patient patient = (Patient) userController.getUserById(patientId);
          if (patient != null) {
            String[] prescriptionParts = medRecord.getPrescription().split(" ");
            String medicationName = prescriptionParts[0];
            String dosage = prescriptionParts.length > 1 ? prescriptionParts[1] : "";

            Prescription prescription = new Prescription(
                appointmentId + "_PRESC", // Generate prescription ID
                patient,
                medicationName,
                dosage,
                2 // Default quantity, adjust as needed
            );
            outcome.addPrescription(prescription);
          }
        }

        outcomes.put(appointmentId, outcome);
      } catch (Exception e) {
        System.err.println("Error loading appointment outcome: " + e.getMessage());
      }
    }
  }

  @Override
  public List<AppointmentOutcome> getPendingPrescriptionOutcomes() {
    return outcomes.values().stream()
        .filter(outcome -> outcome.getPrescriptions().stream()
            .anyMatch(p -> p.getStatus().equals("Pending")))
        .collect(java.util.stream.Collectors.toList());
  }

  @Override
  public boolean updatePrescriptionStatus(String appointmentId, String prescriptionId, String status) {
    AppointmentOutcome outcome = outcomes.get(appointmentId);
    if (outcome == null)
      return false;

    boolean updated = false;
    for (Prescription prescription : outcome.getPrescriptions()) {
      if (prescription.getPrescriptionId().equals(prescriptionId)) {
        prescription.setStatus(status);
        updated = true;

        // Update medical record if prescription is fulfilled
        if (status.equals("Fulfilled")) {
          MedicalRecord record = medicalRecordController.getRecordByPatientId(outcome.getPatientId());
          if (record != null) {
            String currentPrescription = prescription.getMedicationName() + " " + prescription.getDosage();
            medicalRecordController.updateRecord(
                outcome.getPatientId(),
                record.getDiagnosis(),
                currentPrescription);
          }
        }
        break;
      }
    }

    return updated;
  }

  @Override
  public AppointmentOutcome getOutcomeByAppointmentId(String appointmentId) {
    return outcomes.get(appointmentId);
  }
}