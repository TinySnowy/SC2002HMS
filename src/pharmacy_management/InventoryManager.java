package pharmacy_management;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {
    private Map<String, Integer> inventory;

    public InventoryManager() {
        inventory = new HashMap<>();
    }

    public void addMedication(String medicationName, int quantity) {
        inventory.put(medicationName, inventory.getOrDefault(medicationName, 0) + quantity);
        System.out.println("Added " + quantity + " units of " + medicationName + " to inventory.");
    }

    public boolean dispenseMedication(String medicationName, int quantity) {
        int currentStock = inventory.getOrDefault(medicationName, 0);
        if (currentStock >= quantity) {
            inventory.put(medicationName, currentStock - quantity);
            System.out.println("Dispensed " + quantity + " units of " + medicationName + ".");
            return true;
        } else {
            System.out.println("Insufficient stock for " + medicationName + ".");
            return false;
        }
    }

    public void requestRefill(String medicationName, int quantity) {
        System.out.println("Refill request: " + quantity + " units of " + medicationName);
        addMedication(medicationName, quantity);
    }

    public void checkInventory() {
        System.out.println("Current Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " units");
        }
    }
}
