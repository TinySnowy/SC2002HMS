package pharmacy_management;

import pharmacy_management.inventory.InventoryManager;
import pharmacy_management.prescriptions.Prescription;
import user_management.UserController;
import utils.CSVReaderUtil;
import utils.CSVWriterUtil;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionService {
  private static final String PRESCRIPTIONS_FILE = "SC2002HMS/data/Prescriptions.csv";
  private final InventoryManager inventoryManager;
  private final UserController userController;

  public PrescriptionService(InventoryManager inventoryManager, UserController userController) {
    this.inventoryManager = inventoryManager;
    this.userController = userController;
  }

  public List<Prescription> getPendingPrescriptions() {
    List<Prescription> pendingPrescriptions = new ArrayList<>();
    try {
      List<String[]> records = CSVReaderUtil.readCSV(PRESCRIPTIONS_FILE);
      boolean isFirstRow = true;
      for (String[] record : records) {
        if (isFirstRow) {
          isFirstRow = false;
          continue;
        }
        String status = record[4].trim();
        if ("Pending".equalsIgnoreCase(status)) {
          Prescription prescription = new Prescription(
              record[0].trim(),
              null,
              record[1].trim(),
              record[2].trim(),
              Integer.parseInt(record[3].trim()));
          pendingPrescriptions.add(prescription);
        }
      }
    } catch (Exception e) {
      System.err.println("Error reading prescriptions: " + e.getMessage());
    }
    return pendingPrescriptions;
  }

  public boolean dispensePrescription(String prescriptionId) {
    try {
      List<String[]> records = CSVReaderUtil.readCSV(PRESCRIPTIONS_FILE);
      List<String[]> updatedRecords = new ArrayList<>();
      boolean prescriptionFound = false;
      boolean dispensed = false;
      updatedRecords.add(records.get(0));
      for (int i = 1; i < records.size(); i++) {
        String[] record = records.get(i);
        if (record[0].trim().equals(prescriptionId)) {
          prescriptionFound = true;
          if ("Pending".equalsIgnoreCase(record[4].trim())) {
            String medicationName = record[1].trim();
            int quantity = Integer.parseInt(record[3].trim());
            if (inventoryManager.dispenseMedication(medicationName, quantity)) {
              record[4] = "Fulfilled";
              dispensed = true;
              System.out.println("Successfully dispensed prescription: " + prescriptionId);
            } else {
              System.out.println("Failed to dispense: Insufficient stock");
            }
          } else {
            System.out.println("Prescription is not in Pending status");
          }
        }
        updatedRecords.add(record);
      }
      if (!prescriptionFound) {
        System.out.println("Prescription not found: " + prescriptionId);
        return false;
      }
      if (dispensed) {
        saveUpdatedPrescriptions(updatedRecords);
        return true;
      }
      return false;
    } catch (Exception e) {
      System.err.println("Error dispensing prescription: " + e.getMessage());
      return false;
    }
  }

  private void saveUpdatedPrescriptions(List<String[]> records) {
    try {
      CSVWriterUtil.writeCSV(PRESCRIPTIONS_FILE, writer -> {
        for (String[] record : records) {
          writer.write(String.join(",", record) + "\n");
        }
      });
    } catch (Exception e) {
      System.err.println("Error saving prescriptions: " + e.getMessage());
    }
  }

  public void displayPendingPrescriptions() {
    List<Prescription> pendingPrescriptions = getPendingPrescriptions();
    if (pendingPrescriptions.isEmpty()) {
      System.out.println("No pending prescriptions found.");
      return;
    }
    System.out.println("\nPending Prescriptions:");
    System.out.println("----------------------------------------");
    System.out.printf("%-15s %-15s %-15s %-10s%n",
        "Prescription ID", "Medication", "Dosage", "Quantity");
    System.out.println("----------------------------------------");
    for (Prescription prescription : pendingPrescriptions) {
      System.out.printf("%-15s %-15s %-15s %-10d%n",
          prescription.getPrescriptionId(),
          prescription.getMedicationName(),
          prescription.getDosage(),
          prescription.getQuantity());
    }
    System.out.println("----------------------------------------");
  }
}