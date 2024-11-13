package pharmacy_management;

import user_management.Patient;

public class Prescription {
    private String prescriptionId;
    private Patient patient;
    private String medicationName;
    private String dosage;
    private String status; // "Pending", "Fulfilled", "Cancelled"

    public Prescription(String prescriptionId, Patient patient, String medicationName, String dosage) {
        this.prescriptionId = prescriptionId;
        this.patient = patient;
        this.medicationName = medicationName;
        this.dosage = dosage;
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

    public String getStatus() {
        return status;
    }

    public void fulfill() {
        this.status = "Fulfilled";
    }

    public void cancel() {
        this.status = "Cancelled";
    }

    @Override
    public String toString() {
        return "Prescription ID: " + prescriptionId + ", Patient: " + patient.getName() + ", Medication: " + medicationName + ", Dosage: " + dosage + ", Status: " + status;
    }
}
