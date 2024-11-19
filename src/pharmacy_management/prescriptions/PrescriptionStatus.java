package pharmacy_management.prescriptions;

/**
 * Enumeration defining possible states for medical prescriptions in the HMS.
 * Represents the complete lifecycle of a prescription from creation to completion.
 * 
 * Status Flow:
 * 1. PENDING: Initial state after doctor creates prescription
 * 2. DISPENSED: Medication has been provided to patient
 * 3. REJECTED: Prescription cannot be fulfilled
 * 4. CANCELLED: Prescription terminated by doctor or patient
 * 
 * Used for:
 * - Prescription tracking
 * - Workflow management
 * - Inventory control
 * - Audit purposes
 */
public enum PrescriptionStatus {
    /** 
     * Initial state when prescription is created.
     * Indicates:
     * - Awaiting pharmacy processing
     * - Not yet dispensed
     * - Can be modified
     */
    PENDING,
    
    /** 
     * Medication has been provided to patient.
     * Indicates:
     * - Successfully fulfilled
     * - Inventory updated
     * - Cannot be modified
     */
    DISPENSED,
    
    /** 
     * Prescription cannot be fulfilled.
     * Indicates:
     * - Stock unavailable
     * - Invalid prescription
     * - Other blocking issues
     */
    REJECTED,
    
    /** 
     * Prescription terminated before fulfillment.
     * Indicates:
     * - Doctor cancelled
     * - Patient cancelled
     * - Treatment changed
     */
    CANCELLED
}
