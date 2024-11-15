package patient_management;

import user_management.Patient;
import utils.CSVReaderUtil;
import utils.CSVWriterUtil;

import java.util.ArrayList;
import java.util.List;

public class MedicalRecordController {
    private List<MedicalRecord> records;

    public MedicalRecordController() {
        records = new ArrayList<>();
        loadRecordsFromCSV("data/Medical_Record.csv");
    }

    public MedicalRecord getRecordByPatientId(String patientId) {
        for (MedicalRecord record : records) {
            if (record.getPatientId().equals(patientId)) {
                return record;
            }
        }
        return null;
    }

    public void updateRecord(MedicalRecord record) {
        // Update or add the record
        records.removeIf(r -> r.getPatientId().equals(record.getPatientId()));
        records.add(record);
    }

    public void saveRecordsToCSV(String filePath) {
        List<String[]> data = new ArrayList<>();
//        data.add(new String[]{"PatientID", "Name", "Diagnosis", "Prescription", "Notes"}); // Header
        data.add(new String[]{"PatientID", "Name", "Diagnosis", "Prescription"}); // Header
        for (MedicalRecord record : records) {
            data.add(new String[]{
                    record.getPatientId(),
                    record.getPatientName(),
                    record.getDiagnosis(),
                    record.getPrescription(),
//                    record.getNotes()
            });
        }
        CSVWriterUtil.writeCSV(filePath, data);
    }

    private void loadRecordsFromCSV(String filePath) {
        List<String[]> rows = CSVReaderUtil.readCSV(filePath);
        boolean isFirstRow = true;
        for (String[] row : rows) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }
            String patientId = row[0];
            String name = row[1];
            String diagnosis = row[2];
            String prescription = row[3];
//            String notes = row[4];

            Patient patient = new Patient(patientId, "password", name, "contact@example.com", "email@example.com");
            MedicalRecord record = new MedicalRecord(patient);
            record.setDiagnosis(diagnosis);
            record.setPrescription(prescription);
//            record.setNotes(notes);

            records.add(record);
        }
    }
}
