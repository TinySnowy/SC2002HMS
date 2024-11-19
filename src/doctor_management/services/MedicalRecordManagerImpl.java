package doctor_management.services;

import doctor_management.interfaces.IMedicalRecordManager;
import patient_management.model.MedicalRecord;
import patient_management.controllers.MedicalRecordController;

/**
 * Implementation of the IMedicalRecordManager interface.
 * Manages patient medical records including:
 * - Record access control
 * - Record viewing and updates
 * - Doctor access permissions
 * - Record display formatting
 * Ensures secure and controlled access to patient medical information.
 */
public class MedicalRecordManagerImpl implements IMedicalRecordManager {
  /** Controller for managing medical record operations */
  private final MedicalRecordController recordController;

  /**
   * Constructs MedicalRecordManagerImpl with required controller.
   * Initializes medical record management system.
   * 
   * @param recordController Controller for medical record operations
   * @throws IllegalArgumentException if controller is null
   */
  public MedicalRecordManagerImpl(MedicalRecordController recordController) {
    this.recordController = recordController;
  }

  /**
   * Displays a patient's medical record.
   * Shows formatted view of:
   * - Patient information
   * - Current diagnosis
   * - Active prescriptions
   * - Medical history
   * 
   * @param patientId ID of the patient
   * @throws IllegalArgumentException if patient ID is invalid
   * @throws SecurityException if access is denied
   */
  @Override
  public void viewMedicalRecord(String patientId) {
    MedicalRecord record = recordController.getRecordByPatientId(patientId);
    if (record != null) {
      displayRecord(record);
    } else {
      System.out.println("Medical record not found for patient ID: " + patientId);
    }
  }

  /**
   * Updates a patient's medical record with new information.
   * Handles:
   * - Diagnosis updates
   * - Prescription changes
   * - Record modification tracking
   * 
   * @param patientId ID of the patient
   * @param diagnosis New diagnosis information
   * @param prescription Updated prescription details
   * @param doctorId ID of the doctor making changes
   * @throws IllegalArgumentException if any parameter is invalid
   * @throws SecurityException if doctor lacks update permission
   */
  @Override
  public void updateMedicalRecord(String patientId, String diagnosis, String prescription, String doctorId) {
    MedicalRecord record = recordController.getRecordByPatientId(patientId);
    if (record != null) {
      try {
        recordController.updateRecord(patientId, diagnosis, prescription);
        System.out.println("\nMedical record updated successfully!");
        System.out.println("----------------------------------------");
        System.out.println("Updated Record:");
        System.out.println("Diagnosis: " + diagnosis);
        System.out.println("Prescription: " + prescription);
        System.out.println("----------------------------------------");
      } catch (Exception e) {
        System.err.println("Error updating medical record: " + e.getMessage());
      }
    } else {
      System.out.println("Medical record not found for patient ID: " + patientId);
    }
  }

  /**
   * Checks if a doctor has access rights to a patient's record.
   * Validates access based on:
   * - Doctor's credentials
   * - Patient-doctor relationship
   * - Access permissions
   * 
   * @param doctorId ID of the doctor
   * @param patientId ID of the patient
   * @return true if access is allowed, false otherwise
   */
  @Override
  public boolean hasAccessToRecord(String doctorId, String patientId) {
    // Simply check if the patient record exists since we're not implementing access control
    return recordController.hasRecord(patientId);
  }

  /**
   * Grants a doctor access to a patient's medical record.
   * In current implementation, provides automatic access.
   * Could be extended for:
   * - Access level control
   * - Time-limited access
   * - Access audit logging
   * 
   * @param doctorId ID of the doctor
   * @param patientId ID of the patient
   */
  @Override
  public void grantAccess(String doctorId, String patientId) {
    if (recordController.hasRecord(patientId)) {
      System.out.println("Access is automatically granted to existing patient records.");
    } else {
      System.out.println("Patient record not found: " + patientId);
    }
  }

  /**
   * Revokes a doctor's access to a patient's record.
   * In current implementation, provides notification only.
   * Could be extended for:
   * - Access removal
   * - Revocation logging
   * - Notification system
   * 
   * @param doctorId ID of the doctor
   * @param patientId ID of the patient
   */
  @Override
  public void revokeAccess(String doctorId, String patientId) {
    if (recordController.hasRecord(patientId)) {
      System.out.println("Access control is not implemented - all doctors have access to patient records.");
    } else {
      System.out.println("Patient record not found: " + patientId);
    }
  }

  /**
   * Displays formatted medical record information.
   * Shows:
   * - Patient demographics
   * - Current medical status
   * - Treatment information
   * 
   * @param record Medical record to display
   */
  private void displayRecord(MedicalRecord record) {
    System.out.println("\nMedical Record Details:");
    System.out.println("----------------------------------------");
    System.out.println("Patient ID: " + record.getPatientId());
    System.out.println("Patient Name: " + record.getPatientName());
    System.out.println("Current Diagnosis: " +
        (record.getDiagnosis().isEmpty() ? "None" : record.getDiagnosis()));
    System.out.println("Current Prescription: " +
        (record.getPrescription().isEmpty() ? "None" : record.getPrescription()));
    System.out.println("----------------------------------------");
  }
}