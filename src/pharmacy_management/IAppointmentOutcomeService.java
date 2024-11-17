package pharmacy_management;

import java.util.List;

public interface IAppointmentOutcomeService {
  List<AppointmentOutcome> getPendingPrescriptionOutcomes();

  boolean updatePrescriptionStatus(String appointmentId, String prescriptionId, String status);

  AppointmentOutcome getOutcomeByAppointmentId(String appointmentId);
}