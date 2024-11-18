package doctor_management.services;

import doctor_management.interfaces.IMedicalRecordManager;
import patient_management.model.MedicalRecord;
import patient_management.controllers.MedicalRecordController;

public class MedicalRecordManagerImpl implements IMedicalRecordManager {
  private final MedicalRecordController recordController;

  public MedicalRecordManagerImpl(MedicalRecordController recordController) {
    this.recordController = recordController;
  }

  @Override
  public void viewMedicalRecord(String patientId) {
    MedicalRecord record = recordController.getRecordByPatientId(patientId);
    if (record != null) {
      displayRecord(record);
    } else {
      System.out.println("Medical record not found for patient ID: " + patientId);
    }
  }

  @Override
  public void updateMedicalRecord(String patientId, String diagnosis, String prescription) {
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

  @Override
  public boolean hasAccessToRecord(String doctorId, String patientId) {
    // Simply check if the patient record exists since we're not implementing access
    // control
    return recordController.hasRecord(patientId);
  }

  @Override
  public void grantAccess(String doctorId, String patientId) {
    // Since we're not implementing access control, just validate patient exists
    if (recordController.hasRecord(patientId)) {
      System.out.println("Access is automatically granted to existing patient records.");
    } else {
      System.out.println("Patient record not found: " + patientId);
    }
  }

  @Override
  public void revokeAccess(String doctorId, String patientId) {
    // Since we're not implementing access control, just validate patient exists
    if (recordController.hasRecord(patientId)) {
      System.out.println("Access control is not implemented - all doctors have access to patient records.");
    } else {
      System.out.println("Patient record not found: " + patientId);
    }
  }

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