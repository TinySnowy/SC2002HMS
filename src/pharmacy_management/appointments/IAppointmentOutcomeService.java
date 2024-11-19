package pharmacy_management.appointments;

import java.util.List;

/**
 * Interface defining the contract for appointment outcome management.
 * Specifies essential operations for:
 * - Prescription tracking
 * - Outcome management
 * - Status updates
 * - Data persistence
 * Provides standardized methods for handling medical consultation results.
 */
public interface IAppointmentOutcomeService {
  /**
   * Retrieves all appointment outcomes with pending prescriptions.
   * Implementation should:
   * - Filter for pending status
   * - Sort by relevance
   * - Validate prescriptions
   * - Handle empty results
   * Used by pharmacy for prescription processing.
   * 
   * @return List of outcomes with pending prescriptions
   */
  List<AppointmentOutcome> getPendingPrescriptionOutcomes();

  /**
   * Updates the status of a specific prescription.
   * Implementation should:
   * - Validate appointment existence
   * - Verify prescription
   * - Update status
   * - Persist changes
   * - Handle medical record updates
   * - Manage error cases
   * 
   * @param appointmentId ID of target appointment
   * @param prescriptionId ID of target prescription
   * @param status New status value
   * @return true if update successful, false otherwise
   */
  boolean updatePrescriptionStatus(String appointmentId, String prescriptionId, String status);

  /**
   * Retrieves appointment outcome by appointment ID.
   * Implementation should:
   * - Validate input
   * - Handle missing outcomes
   * - Return complete outcome data
   * - Manage access control
   * 
   * @param appointmentId ID of target appointment
   * @return Associated outcome or null if not found
   */
  AppointmentOutcome getOutcomeByAppointmentId(String appointmentId);

  /**
   * Saves a new appointment outcome record.
   * Implementation should:
   * - Validate outcome data
   * - Ensure data consistency
   * - Handle duplicates
   * - Persist changes
   * - Manage errors
   * - Update related records
   * 
   * @param outcome New outcome to save
   * @return true if save successful, false otherwise
   */
  boolean saveAppointmentOutcome(AppointmentOutcome outcome);
}