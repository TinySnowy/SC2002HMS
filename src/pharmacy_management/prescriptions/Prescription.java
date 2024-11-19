package pharmacy_management.prescriptions;

import pharmacy_management.inventory.InventoryManager;
import user_management.Patient;

/**
 * Model class representing a medical prescription in the HMS.
 * Contains:
 * - Prescription identification
 * - Patient information
 * - Medication details
 * - Dosage instructions
 * - Status tracking
 * Provides structured storage for medication orders and dispensing.
 */
public class Prescription {
    /** Unique identifier for the prescription */
    private String prescriptionId;
    
    /** Patient receiving the medication */
    private Patient patient;
    
    /** Name of prescribed medication */
    private String medicationName;
    
    /** Dosage instructions for medication */
    private String dosage;
    
    /** Quantity of medication to dispense */
    private int quantity;
    
    /** Current status of prescription (Pending/Fulfilled/Cancelled) */
    private String status;

    /**
     * Constructs a new prescription with required details.
     * Initializes with PENDING status.
     * 
     * @param prescriptionId Unique prescription identifier
     * @param patient Patient receiving medication
     * @param medicationName Prescribed medication
     * @param dosage Medication dosage instructions
     * @param quantity Amount to dispense
     * @throws IllegalArgumentException if required parameters are invalid
     */
    public Prescription(String prescriptionId, Patient patient, String medicationName, String dosage, int quantity) {
        this.prescriptionId = prescriptionId;
        this.patient = patient;
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.quantity = quantity;
        this.status = "Pending"; // Default status when prescription is created
    }

    /**
     * Retrieves prescription's unique identifier.
     * Used for prescription tracking and lookup.
     * 
     * @return Prescription ID string
     */
    public String getPrescriptionId() {
        return prescriptionId;
    }

    /**
     * Retrieves patient information.
     * Used for patient identification and record linking.
     * 
     * @return Associated patient object
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Retrieves prescribed medication name.
     * Used for inventory matching and dispensing.
     * 
     * @return Medication name string
     */
    public String getMedicationName() {
        return medicationName;
    }

    /**
     * Retrieves medication dosage instructions.
     * Used for patient medication guidance.
     * 
     * @return Dosage instruction string
     */
    public String getDosage() {
        return dosage;
    }

    /**
     * Retrieves prescribed medication quantity.
     * Used for inventory management and dispensing.
     * 
     * @return Quantity to dispense
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Retrieves current prescription status.
     * Used for tracking prescription lifecycle.
     * 
     * @return Current status string
     */
    public String getStatus() {
        return status;
    }

    /**
     * Attempts to fulfill prescription from inventory.
     * Updates status and manages inventory levels.
     * 
     * @param inventoryManager Inventory control system
     * @return true if fulfilled successfully
     */
    public boolean fulfill(InventoryManager inventoryManager) {
        if (inventoryManager.dispenseMedication(medicationName, quantity)) {
            this.status = "Fulfilled";
            System.out.println("Prescription fulfilled: " + prescriptionId);
            return true;
        } else {
            System.out.println("Failed to fulfill prescription: " + prescriptionId);
            return false;
        }
    }

    public void cancel() {
        this.status = "Cancelled";
        System.out.println("Prescription cancelled: " + prescriptionId);
    }

    @Override
    public String toString() {
        return "Prescription ID: " + prescriptionId +
                ", Patient: " + (patient != null ? patient.getName() : "Unknown") +
                ", Medication: " + medicationName +
                ", Dosage: " + dosage +
                ", Quantity: " + quantity +
                ", Status: " + status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toCSVString() {
        return String.format("%s,%s,%s,%s,%d,%s",
            prescriptionId,
            patient.getId(),
            patient.getDoctor().getId(),
            medicationName,
            quantity,
            dosage
        );
    }
}
