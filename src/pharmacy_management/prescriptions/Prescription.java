package pharmacy_management.prescriptions;

import pharmacy_management.inventory.InventoryManager;
import user_management.Patient;

public class Prescription {
    private String prescriptionId;
    private Patient patient;
    private String medicationName;
    private String dosage;
    private int quantity;
    private String status; // "Pending", "Fulfilled", "Cancelled"

    public Prescription(String prescriptionId, Patient patient, String medicationName, String dosage, int quantity) {
        this.prescriptionId = prescriptionId;
        this.patient = patient;
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.quantity = quantity;
        this.status = "Pending"; // Default status when prescription is created
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public Patient getPatient() {
        return patient;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public String getDosage() {
        return dosage;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

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
