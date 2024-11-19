// InventoryManager.java
package pharmacy_management.inventory;

import utils.CSVReaderUtil;
import utils.CSVWriterUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pharmacy_management.prescriptions.Prescription;

/**
 * Manages pharmacy inventory operations in the HMS.
 * Handles:
 * - Stock tracking by expiry date
 * - Low stock monitoring
 * - Inventory replenishment
 * - Medication dispensing
 * - CSV data persistence
 * Provides centralized control of medication inventory.
 */
public class InventoryManager {
    /** Shared inventory mapping medication names to stock by expiry date */
    private static Map<String, Map<String, Integer>> sharedInventory = new HashMap<>();
    
    /** Threshold levels for low stock alerts by medication */
    private static Map<String, Integer> sharedLowStockThresholds = new HashMap<>();
    
    /** Active prescriptions in the system */
    private final Map<String, Prescription> prescriptions;
    
    /** Service for handling inventory replenishment */
    private final IReplenishmentService replenishmentService;
    
    /** ID of pharmacist managing inventory */
    private final String pharmacistId;
    
    /** File path for medication data storage */
    private static final String MEDICINE_FILE = "SC2002HMS/data/Medicine_List.csv";

    /**
     * Initializes inventory manager with required services.
     * Sets up data structures and links dependencies.
     * 
     * @param replenishmentService Service for stock replenishment
     * @param pharmacistId ID of managing pharmacist
     */
    public InventoryManager(IReplenishmentService replenishmentService, String pharmacistId) {
        this.prescriptions = new HashMap<>();
        this.replenishmentService = replenishmentService;
        this.pharmacistId = pharmacistId;
    }

    /**
     * Loads inventory data from CSV file.
     * Processes:
     * - Medication names
     * - Stock levels
     * - Thresholds
     * - Expiry dates
     * Handles file reading and parsing errors.
     * 
     * @param filePath Path to inventory CSV file
     */
    public void loadInventoryFromCSV(String filePath) {
        try {
            List<String[]> records = CSVReaderUtil.readCSV(filePath);
            boolean isFirstRow = true;
            sharedInventory.clear();
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

    /**
     * Adds new medication stock to inventory.
     * Updates stock levels and checks thresholds.
     * Manages expiry date tracking.
     * 
     * @param medicationName Name of medication
     * @param quantity Amount to add
     * @param expiryDate Expiration date of stock
     */
    public void addMedication(String medicationName, int quantity, String expiryDate) {
        sharedInventory.putIfAbsent(medicationName, new HashMap<>());
        Map<String, Integer> stockByDate = sharedInventory.get(medicationName);
        stockByDate.put(expiryDate, stockByDate.getOrDefault(expiryDate, 0) + quantity);
        checkLowStock(medicationName);
        saveMedicineListToCSV();
    }

    /**
     * Updates low stock threshold for medication.
     * Manages alert levels and updates CSV storage.
     * 
     * @param medicationName Target medication
     * @param newThreshold New threshold value
     */
    public void setLowStockThreshold(String medicationName, int newThreshold) {
        try {
            List<String[]> records = CSVReaderUtil.readCSV(MEDICINE_FILE);
            List<String[]> updatedRecords = new ArrayList<>();
            boolean medicationFound = false;
            for (String[] record : records) {
                if (record[0].equalsIgnoreCase(medicationName)) {
                    record[2] = String.valueOf(newThreshold);
                    medicationFound = true;
                }
                updatedRecords.add(record);
            }
            if (!medicationFound) {
                System.out.println("Medication not found in the file: " + medicationName);
                return;
            }
            CSVWriterUtil.writeCSV(MEDICINE_FILE, writer -> {
                for (String[] record : updatedRecords) {
                    writer.write(String.join(",", record) + "\n");
                }
            });
            sharedLowStockThresholds.put(medicationName, newThreshold);
            System.out.println("Low stock threshold updated successfully for " + medicationName);
        } catch (Exception e) {
            System.err.println("Error updating low stock threshold: " + e.getMessage());
        }
    }

    /**
     * Saves current inventory state to CSV.
     * Handles:
     * - Data formatting
     * - File writing
     * - Error recovery
     */
    public void saveMedicineListToCSV() {
        try {
            CSVWriterUtil.writeCSV(MEDICINE_FILE, writer -> {
                writer.write("Medication Name,Current Stock,Low Stock Threshold,Expiry Date\n");
                for (Map.Entry<String, Map<String, Integer>> entry : sharedInventory.entrySet()) {
                    String medicationName = entry.getKey();
                    Map<String, Integer> stockByDate = entry.getValue();
                    for (Map.Entry<String, Integer> stockEntry : stockByDate.entrySet()) {
                        if (stockEntry.getValue() > 0) {
                            writer.write(String.format("%s,%d,%d,%s\n",
                                    medicationName,
                                    stockEntry.getValue(),
                                    sharedLowStockThresholds.getOrDefault(medicationName, 10),
                                    stockEntry.getKey()));
                        }
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("Error saving medicine list: " + e.getMessage());
        }
    }

    /**
     * Displays current inventory status.
     * Shows:
     * - Medication names
     * - Stock levels
     * - Expiry dates
     * Handles empty inventory case.
     */
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

    /**
     * Checks all medications for low stock.
     * Generates alerts for items below threshold.
     * Used for inventory management.
     */
    public void checkAllLowStock() {
        System.out.println("\nLow Stock Alerts:");
        for (String medicationName : sharedInventory.keySet()) {
            checkLowStock(medicationName);
        }
    }

    /**
     * Checks specific medication for low stock.
     * Compares total stock against threshold.
     * 
     * @param medicationName Medication to check
     */
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

    /**
     * Requests inventory replenishment.
     * Creates and tracks replenishment orders.
     * 
     * @param medicationName Medication to reorder
     * @param quantity Amount to request
     * @param expiryDate Expected expiry date
     */
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

    /**
     * Dispenses medication from inventory.
     * Manages:
     * - Stock reduction
     * - Expiry date tracking
     * - Low stock alerts
     * 
     * @param medicationName Medication to dispense
     * @param quantity Amount to dispense
     * @return true if dispensed successfully
     */
    public boolean dispenseMedication(String medicationName, int quantity) {
        if (!sharedInventory.containsKey(medicationName)) {
            System.out.println("Medication not available in inventory.");
            return false;
        }
        Map<String, Integer> stockByDate = sharedInventory.get(medicationName);
        boolean dispensed = false;
        for (Map.Entry<String, Integer> entry : stockByDate.entrySet()) {
            String expiryDate = entry.getKey();
            int stock = entry.getValue();
            if (stock >= quantity) {
                stockByDate.put(expiryDate, stock - quantity);
                dispensed = true;
                System.out.println("Dispensed " + quantity + " units of " +
                        medicationName + " (Expiry: " + expiryDate + ")");
                break;
            }
        }
        if (dispensed) {
            saveMedicineListToCSV();
            checkLowStock(medicationName);
            return true;
        }
        System.out.println("Insufficient stock for " + medicationName);
        return false;
    }

    /**
     * Retrieves prescription by ID.
     * Used for prescription validation.
     * 
     * @param prescriptionId ID to look up
     * @return Associated prescription or null
     */
    public Prescription getPrescriptionById(String prescriptionId) {
        return prescriptions.get(prescriptionId);
    }

    /**
     * Adds new prescription to system.
     * Validates patient information.
     * 
     * @param prescription New prescription to add
     */
    public void addPrescription(Prescription prescription) {
        if (prescription.getPatient() == null) {
            System.err.println("Cannot add prescription with null patient.");
            return;
        }
        prescriptions.put(prescription.getPrescriptionId(), prescription);
    }
}