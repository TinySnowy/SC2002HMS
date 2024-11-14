package pharmacy_management;

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
    // Fulfill prescription and update inventory
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
        return "Prescription ID: " + prescriptionId + ", Patient: " + patient.getName() + ", Medication: " + medicationName +
                ", Dosage: " + dosage + ", Quantity: " + quantity + ", Status: " + status;
    }

    public void setStatus(String fulfilled) {
        this.status = "Fulfilled";
    }
}
