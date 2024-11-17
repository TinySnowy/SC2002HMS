// InventoryManager.java
package pharmacy_management.inventory;

import utils.CSVReaderUtil;
import utils.CSVWriterUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pharmacy_management.prescriptions.Prescription;

public class InventoryManager {
    private static Map<String, Map<String, Integer>> sharedInventory = new HashMap<>();
    private static Map<String, Integer> sharedLowStockThresholds = new HashMap<>();
    private final Map<String, Prescription> prescriptions;
    private final IReplenishmentService replenishmentService;
    private final String pharmacistId;

    public InventoryManager(IReplenishmentService replenishmentService, String pharmacistId) {
        this.prescriptions = new HashMap<>();
        this.replenishmentService = replenishmentService;
        this.pharmacistId = pharmacistId;
    }

    public void loadInventoryFromCSV(String filePath) {
        try {
            List<String[]> records = CSVReaderUtil.readCSV(filePath);
            boolean isFirstRow = true;

            for (String[] line : records) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }

                try {
                    String medicationName = line[0].trim();
                    int initialStock = Integer.parseInt(line[1].trim());
                    int lowStockThreshold = Integer.parseInt(line[2].trim());
                    String expiryDate = line[3].trim();

                    addMedication(medicationName, initialStock, expiryDate);
                    sharedLowStockThresholds.put(medicationName, lowStockThreshold);

                    System.out.println("Loaded: " + medicationName + " with " + initialStock + " units");
                } catch (Exception e) {
                    System.err.println("Error loading line: " + String.join(",", line));
                    System.err.println("Error details: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading inventory from CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addMedication(String medicationName, int quantity, String expiryDate) {
        sharedInventory.putIfAbsent(medicationName, new HashMap<>());
        Map<String, Integer> stockByDate = sharedInventory.get(medicationName);
        stockByDate.put(expiryDate, stockByDate.getOrDefault(expiryDate, 0) + quantity);
        checkLowStock(medicationName);
        saveMedicineListToCSV();
    }

    public void setLowStockThreshold(String medicationName, int newThreshold) {
        try {
            String filePath = "SC2002HMS/data/Medicine_List.csv";
            List<String[]> records = CSVReaderUtil.readCSV(filePath);
            List<String[]> updatedRecords = new ArrayList<>();

            boolean medicationFound = false;

            // Update the threshold for the specified medication
            for (String[] record : records) {
                if (record[0].equalsIgnoreCase(medicationName)) {
                    record[2] = String.valueOf(newThreshold); // Update the low stock threshold
                    medicationFound = true;
                }
                updatedRecords.add(record); // Add the record to the updated list
            }

            if (!medicationFound) {
                System.out.println("Medication not found in the file: " + medicationName);
                return;
            }

            // Write the updated records back to the file
            CSVWriterUtil.writeCSV(filePath, writer -> {
                for (String[] record : updatedRecords) {
                    writer.write(String.join(",", record) + "\n");
                }
            });

            // Update the in-memory threshold map
            sharedLowStockThresholds.put(medicationName, newThreshold);

            System.out.println("Low stock threshold updated successfully for " + medicationName);
        } catch (Exception e) {
            System.err.println("Error updating low stock threshold: " + e.getMessage());
        }
    }

    public void saveMedicineListToCSV() {
        String MEDICINE_FILE = "SC2002HMS/data/Medicine_List.csv";
        try {
            CSVWriterUtil.writeCSV(MEDICINE_FILE, writer -> {
                writer.write("Medication Name,Current Stock,Low Stock Threshold,Expiry Date\n");
                for (Map.Entry<String, Map<String, Integer>> entry : sharedInventory.entrySet()) {
                    String medicationName = entry.getKey();
                    Map<String, Integer> stockByDate = entry.getValue();

                    for (Map.Entry<String, Integer> stockEntry : stockByDate.entrySet()) {
                        writer.write(String.format("%s,%d,%d,%s\n",
                                medicationName,
                                stockEntry.getValue(),
                                sharedLowStockThresholds.getOrDefault(medicationName, 10),
                                stockEntry.getKey()));
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("Error saving medicine list: " + e.getMessage());
        }
    }

    public void checkInventory() {
        if (sharedInventory.isEmpty()) {
            System.out.println("Inventory is empty!");
            return;
        }

        for (Map.Entry<String, Map<String, Integer>> entry : sharedInventory.entrySet()) {
            String medicationName = entry.getKey();
            Map<String, Integer> stockByDate = entry.getValue();
            System.out.println("Medication: " + medicationName);
            for (Map.Entry<String, Integer> dateEntry : stockByDate.entrySet()) {
                System.out.println("  Expiry: " + dateEntry.getKey() + ", Quantity: " + dateEntry.getValue());
            }
        }
    }

    public void checkAllLowStock() {
        System.out.println("\nLow Stock Alerts:");
        for (String medicationName : sharedInventory.keySet()) {
            checkLowStock(medicationName);
        }
    }

    public void checkLowStock(String medicationName) {
        Map<String, Integer> stockByDate = sharedInventory.get(medicationName);
        if (stockByDate != null) {
            int totalStock = stockByDate.values().stream().mapToInt(Integer::intValue).sum();
            int threshold = sharedLowStockThresholds.getOrDefault(medicationName, 10);
            if (totalStock < threshold) {
                System.out.println("Warning: Low stock for " + medicationName +
                        ". Total stock: " + totalStock + " units. (Threshold: " + threshold + ")");
            }
        }
    }

    public void requestRefill(String medicationName, int quantity, String expiryDate) {
        if (!sharedInventory.containsKey(medicationName)) {
            System.out.println("Error: Medication not found in inventory.");
            return;
        }

        try {
            String requestId = replenishmentService.createRequest(medicationName, quantity, pharmacistId);
            System.out.println("Replenishment request created successfully!");
            System.out.println("Request ID: " + requestId);
            System.out.println("Status: PENDING");
            System.out.println("Awaiting administrator approval.");
        } catch (Exception e) {
            System.out.println("Error creating replenishment request: " + e.getMessage());
        }
    }

    public boolean dispenseMedication(String medicationName, int quantity) {
        if (!sharedInventory.containsKey(medicationName)) {
            System.out.println("Medication not available in inventory.");
            return false;
        }

        Map<String, Integer> stockByDate = sharedInventory.get(medicationName);
        for (Map.Entry<String, Integer> entry : stockByDate.entrySet()) {
            String expiryDate = entry.getKey();
            int stock = entry.getValue();
            if (stock >= quantity) {
                stockByDate.put(expiryDate, stock - quantity);
                System.out.println("Dispensed " + quantity + " units of " +
                        medicationName + " (Expiry: " + expiryDate + ")");
                return true;
            }
        }
        System.out.println("Insufficient stock for " + medicationName);
        return false;
    }

    public Prescription getPrescriptionById(String prescriptionId) {
        return prescriptions.get(prescriptionId);
    }

    public void addPrescription(Prescription prescription) {
        if (prescription.getPatient() == null) {
            System.err.println("Cannot add prescription with null patient.");
            return;
        }
        prescriptions.put(prescription.getPrescriptionId(), prescription);
    }
}