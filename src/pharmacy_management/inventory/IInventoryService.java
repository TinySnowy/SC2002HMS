package pharmacy_management.inventory;

import java.util.List;

import pharmacy_management.Medication;

public interface IInventoryService {
  List<Medication> getAllMedications();

  Medication getMedicationByName(String name);

  boolean updateStock(String medicationName, int quantity);

  List<Medication> getLowStockMedications();

  void addMedication(Medication medication);
}
