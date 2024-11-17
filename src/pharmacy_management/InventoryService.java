package pharmacy_management;

import java.util.*;
import java.time.LocalDate;
import utils.CSVReaderUtil;
import utils.CSVWriterUtil;

public class InventoryService implements IInventoryService {
  private final Map<String, Medication> medications = new HashMap<>();
  private static final String INVENTORY_FILE = "SC2002HMS/data/Medicine_List.csv";

  public InventoryService() {
    loadInventory();
  }

  private void loadInventory() {
    List<String[]> records = CSVReaderUtil.readCSV(INVENTORY_FILE);
    boolean isFirstRow = true;

    for (String[] record : records) {
      if (isFirstRow) {
        isFirstRow = false;
        continue;
      }

      try {
        String name = record[0];
        int stock = Integer.parseInt(record[1]);
        int threshold = Integer.parseInt(record[2]);
        LocalDate expiry = LocalDate.parse(record[3]);

        medications.put(name, new Medication(name, stock, threshold, expiry));
      } catch (Exception e) {
        System.err.println("Error loading medication: " + e.getMessage());
      }
    }
  }

  @Override
  public List<Medication> getAllMedications() {
    return new ArrayList<>(medications.values());
  }

  @Override
  public Medication getMedicationByName(String name) {
    return medications.get(name);
  }

  @Override
  public boolean updateStock(String medicationName, int quantity) {
    Medication medication = medications.get(medicationName);
    if (medication == null)
      return false;

    medication.updateStock(quantity);
    saveInventory();
    return true;
  }

  @Override
  public List<Medication> getLowStockMedications() {
    return medications.values().stream()
        .filter(Medication::isLowStock)
        .collect(java.util.stream.Collectors.toList());
  }

  @Override
  public void addMedication(Medication medication) {
    medications.put(medication.getName(), medication);
    saveInventory();
  }

  private void saveInventory() {
    CSVWriterUtil.writeCSV(INVENTORY_FILE, writer -> {
      writer.write("Medication Name,Current Stock,Low Stock Threshold,Expiry Date\n");
      for (Medication med : medications.values()) {
        writer.write(String.format("%s,%d,%d,%s\n",
            med.getName(),
            med.getStockLevel(),
            med.getLowStockThreshold(),
            med.getExpiryDate()));
      }
    });
  }
}
