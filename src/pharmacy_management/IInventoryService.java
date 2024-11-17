package pharmacy_management;

import java.util.List;

public interface IInventoryService {
  List<Medication> getAllMedications();

  Medication getMedicationByName(String name);

  boolean updateStock(String medicationName, int quantity);

  List<Medication> getLowStockMedications();

  void addMedication(Medication medication);
}
