package patient_management;

import utils.CSVReaderUtil;
import utils.CSVWriterUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicalRecordController {
    private final Map<String, MedicalRecord> recordsMap;
    private static final String MEDICAL_RECORD_PATH = "SC2002HMS/data/Medical_Record.csv";

    public MedicalRecordController() {
        this.recordsMap = new HashMap<>();
        loadRecordsFromCSV();
    }

    private void loadRecordsFromCSV() {
        List<String[]> rows = CSVReaderUtil.readCSV(MEDICAL_RECORD_PATH);
        boolean isFirstRow = true;

        for (String[] row : rows) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            try {
                if (row.length >= 4) {
                    String patientId = row[0];
                    String name = row[1];
                    String diagnosis = row[2];
                    String prescription = row[3];

                    MedicalRecord record = new MedicalRecord(
                            patientId,
                            name,
                            diagnosis,
                            prescription);

                    recordsMap.put(patientId, record);
                }
            } catch (Exception e) {
                System.err.println("Error loading medical record for patient: " + row[0]);
            }
        }
    }

    public MedicalRecord getRecordByPatientId(String patientId) {
        return recordsMap.get(patientId);
    }

    public void updateRecord(String patientId, String diagnosis, String prescription) {
        MedicalRecord record = recordsMap.get(patientId);
        if (record != null) {
            record.setDiagnosis(diagnosis);
            record.setPrescription(prescription);
            saveRecords();
        }
    }

    public void addNewRecord(String patientId, String name) {
        if (!recordsMap.containsKey(patientId)) {
            MedicalRecord record = new MedicalRecord(patientId, name, "", "");
            recordsMap.put(patientId, record);
            saveRecords();
        }
    }

    public void saveRecords() {
        CSVWriterUtil.writeCSV(MEDICAL_RECORD_PATH, writer -> {
            try {
                writer.write("PatientID,Name,Diagnosis,Prescription\n");
                for (MedicalRecord record : recordsMap.values()) {
                    writer.write(String.format("%s,%s,%s,%s\n",
                            record.getPatientId(),
                            record.getPatientName(),
                            record.getDiagnosis(),
                            record.getPrescription()));
                }
            } catch (Exception e) {
                System.err.println("Error saving medical records: " + e.getMessage());
            }
        });
    }

    public List<MedicalRecord> getAllRecords() {
        return new ArrayList<>(recordsMap.values());
    }
}