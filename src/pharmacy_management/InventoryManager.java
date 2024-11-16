package pharmacy_management;

import utils.CSVReaderUtil;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {
    private Map<String, Map<String, Integer>> inventory; // {MedicationName: {ExpiryDate: Quantity}}
    private Map<String, Integer> lowStockThresholds; // {MedicationName: LowStockThreshold}
    private Map<String, Prescription> prescriptions; // Stores prescriptions by ID

    public InventoryManager() {
        inventory = new HashMap<>();
        prescriptions = new HashMap<>();
        lowStockThresholds = new HashMap<>();
    }

    public void loadInventoryFromCSV(String filePath) {
        try {
            CSVReaderUtil.readCSV(filePath, (line) -> {
                if (line[0].equalsIgnoreCase("Medication Name"))
                    return;

                String medicationName = line[0];
                int initialStock = Integer.parseInt(line[1]);
                int lowStockThreshold = Integer.parseInt(line[2]);
                String expiryDate = line[3];

                addMedication(medicationName, initialStock, expiryDate);
                lowStockThresholds.put(medicationName, lowStockThreshold);
            });
            System.out.println("Inventory and low stock thresholds loaded successfully from " + filePath);
        } catch (Exception e) {
            System.err.println("Error loading inventory from CSV: " + e.getMessage());
        }
    }

    public void addMedication(String medicationName, int quantity, String expiryDate) {
        inventory.putIfAbsent(medicationName, new HashMap<>());
        Map<String, Integer> stockByDate = inventory.get(medicationName);
        stockByDate.put(expiryDate, stockByDate.getOrDefault(expiryDate, 0) + quantity);

        System.out.println(
                "Added " + quantity + " units of " + medicationName + " (Expiry: " + expiryDate + ") to inventory.");
        checkLowStock(medicationName);
    }

    public void addPrescription(Prescription prescription) {
        if (prescription.getPatient() == null) {
            System.err.println("Cannot add prescription with null patient.");
            return;
        }
        prescriptions.put(prescription.getPrescriptionId(), prescription);
        System.out.println("Added prescription: " + prescription);
    }

    public Prescription getPrescriptionById(String prescriptionId) {
        return prescriptions.get(prescriptionId);
    }

    public boolean fulfillPrescription(String prescriptionId) {
        Prescription prescription = getPrescriptionById(prescriptionId);
        if (prescription == null) {
            System.out.println("Prescription not found.");
            return false;
        }

        boolean dispensed = dispenseMedication(prescription.getMedicationName(), prescription.getQuantity());
        if (dispensed) {
            prescription.setStatus("Fulfilled");
            System.out.println("Prescription fulfilled: " + prescriptionId);
            return true;
        } else {
            System.out.println("Failed to fulfill prescription: " + prescriptionId);
            return false;
        }
    }

    public boolean dispenseMedication(String medicationName, int quantity) {
        if (!inventory.containsKey(medicationName)) {
            System.out.println("Medication not available in inventory.");
            return false;
        }

        Map<String, Integer> stockByDate = inventory.get(medicationName);
        for (Map.Entry<String, Integer> entry : stockByDate.entrySet()) {
            String expiryDate = entry.getKey();
            int stock = entry.getValue();

            if (stock >= quantity) {
                stockByDate.put(expiryDate, stock - quantity);
                System.out.println(
                        "Dispensed " + quantity + " units of " + medicationName + " (Expiry: " + expiryDate + ").");
                return true;
            }
        }
        System.out.println("Insufficient stock for " + medicationName + " or stock expired.");
        return false;
    }

    public void checkInventory() {
        System.out.println("Current Inventory:");
        for (Map.Entry<String, Map<String, Integer>> entry : inventory.entrySet()) {
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
        for (String medicationName : inventory.keySet()) {
            checkLowStock(medicationName);
        }
    }

    public void requestRefill(String medicationName, int quantity, String expiryDate) {
        System.out.println(
                "Refill request for " + quantity + " units of " + medicationName + " (Expiry: " + expiryDate + ").");
        addMedication(medicationName, quantity, expiryDate);
    }

    public void checkLowStock(String medicationName) {
        Map<String, Integer> stockByDate = inventory.get(medicationName);
        int totalStock = stockByDate.values().stream().mapToInt(Integer::intValue).sum();

        int threshold = lowStockThresholds.getOrDefault(medicationName, 10);
        if (totalStock < threshold) {
            System.out.println("Warning: Low stock for " + medicationName + ". Total stock: " + totalStock + " units.");
        }
    }
}
