// InventoryManager.java
package pharmacy_management;

import utils.CSVReaderUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
