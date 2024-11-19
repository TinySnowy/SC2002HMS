package doctor_management.interfaces;

/**
 * Interface defining the contract for medical record management operations.
 * Provides a standardized set of methods for managing patient medical records
 * with focus on:
 * - Record access control
 * - Record viewing permissions
 * - Record updates and modifications
 * - Access management between doctors and patients
 * Ensures proper handling of sensitive medical information.
 */
public interface IMedicalRecordManager {
    /**
     * Retrieves and displays a patient's medical record.
     * Should verify access permissions before displaying information.
     * Displays comprehensive medical history including:
     * - Past consultations
     * - Diagnoses
     * - Prescriptions
     * - Treatment history
     * 
     * @param patientId Unique identifier of the patient
     * @throws IllegalArgumentException if patientId is invalid
     * @throws SecurityException if requesting user lacks access permissions
     */
    void viewMedicalRecord(String patientId);

    /**
     * Updates a patient's medical record with new information.
     * Adds new medical data while preserving historical records.
     * Should validate doctor's authorization before allowing updates.
     * 
     * @param patientId Unique identifier of the patient
     * @param diagnosis Current medical diagnosis
     * @param prescription Prescribed medications and treatments
     * @param doctorId Identifier of the doctor making the update
     * @throws IllegalArgumentException if any parameters are invalid
     * @throws SecurityException if doctor lacks update permissions
     * @throws IllegalStateException if record cannot be updated
     */
    void updateMedicalRecord(String patientId, String diagnosis, 
                           String prescription, String doctorId);

    /**
     * Checks if a doctor has access rights to a patient's medical record.
     * Verifies access permissions based on:
     * - Doctor-patient relationship
     * - Granted access permissions
     * - Department/specialty access rules
     * 
     * @param doctorId Identifier of the doctor requesting access
     * @param patientId Identifier of the patient whose record is being accessed
     * @return true if doctor has access, false otherwise
     * @throws IllegalArgumentException if either ID is invalid
     */
    boolean hasAccessToRecord(String doctorId, String patientId);

    /**
     * Grants a doctor access to a patient's medical record.
     * Should validate:
     * - Doctor's credentials
     * - Patient consent (if required)
     * - Existing access permissions
     * Records access grant for audit purposes.
     * 
     * @param doctorId Identifier of the doctor being granted access
     * @param patientId Identifier of the patient whose record is being shared
     * @throws IllegalArgumentException if either ID is invalid
     * @throws IllegalStateException if access cannot be granted
     * @throws SecurityException if granting user lacks authorization
     */
    void grantAccess(String doctorId, String patientId);

    /**
     * Revokes a doctor's access to a patient's medical record.
     * Should validate:
     * - Existing access permissions
     * - Authorization to revoke access
     * Records access revocation for audit purposes.
     * 
     * @param doctorId Identifier of the doctor whose access is being revoked
     * @param patientId Identifier of the patient whose record is being restricted
     * @throws IllegalArgumentException if either ID is invalid
     * @throws IllegalStateException if access cannot be revoked
     * @throws SecurityException if revoking user lacks authorization
     */
    void revokeAccess(String doctorId, String patientId);
}