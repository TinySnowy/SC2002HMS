package doctor_management.interfaces;

public interface IMedicalRecordManager {
  void viewMedicalRecord(String patientId);

  void updateMedicalRecord(String patientId, String diagnosis, String prescription, String doctorId);

  boolean hasAccessToRecord(String doctorId, String patientId);

  void grantAccess(String doctorId, String patientId);

  void revokeAccess(String doctorId, String patientId);
}