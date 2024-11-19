package patient_management.controllers;

import patient_management.model.MedicalRecord;
import utils.CSVReaderUtil;
import utils.CSVWriterUtil;
import user_management.UserController;
import user_management.User;
import user_management.Patient;
import java.io.File;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing patient medical records in the HMS.
 * Handles:
 * - Medical record CRUD operations
 * - Data persistence
 * - Record validation
 * - CSV file management
 * - Record access control
 * Provides centralized management of patient medical information.
 */
public class MedicalRecordController {
    /** Map of patient IDs to their medical records */
    private final Map<String, MedicalRecord> recordsMap;
    
    /** File path for medical record persistence */
    private static final String MEDICAL_RECORD_PATH = "SC2002HMS/data/Medical_Record.csv";
    
    /** Controller for user management operations */
    private final UserController userController;

    /**
     * Constructs a new MedicalRecordController.
     * Initializes record storage and loads existing records.
     * Sets up user management integration.
     */
    public MedicalRecordController() {
        this.recordsMap = new HashMap<>();
        this.userController = UserController.getInstance();
        initializeFile();
        loadRecordsFromCSV();
    }

    /**
     * Initializes the medical records file.
     * Creates file and directory if they don't exist.
     * Ensures data persistence structure is ready.
     */
    private void initializeFile() {
        File file = new File(MEDICAL_RECORD_PATH);
        if (!file.exists()) {
            File directory = file.getParentFile();
            if (!directory.exists()) {
                directory.mkdirs();
            }
            saveRecords(); // This will create the file with headers
        }
    }

    /**
     * Loads medical records from CSV storage.
     * Parses record data and creates record objects.
     * Validates record information during loading.
     */
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
                    createRecordFromRow(row);
                }
            } catch (Exception e) {
                System.err.println("Error loading medical record for patient: " +
                        (row.length > 0 ? row[0] : "unknown") + ": " + e.getMessage());
            }
        }
    }

    /**
     * Creates a medical record from CSV row data.
     * Validates and processes record information.
     * 
     * @param row Array of record data from CSV
     */
    private void createRecordFromRow(String[] row) {
        String patientId = row[0].trim();
        String name = row[1].trim();
        String diagnosis = row[2].trim();
        String prescription = row[3].trim();

        if (patientId.isEmpty() || name.isEmpty()) {
            return;
        }

        // Create the record even if the patient doesn't exist yet
        // This handles cases where medical records are loaded before patients
        MedicalRecord record = new MedicalRecord(patientId, name, diagnosis, prescription);
        recordsMap.put(patientId, record);
    }

    /**
     * Retrieves a medical record by patient ID.
     * 
     * @param patientId ID of the patient
     * @return Medical record or null if not found
     */
    public MedicalRecord getRecordByPatientId(String patientId) {
        return recordsMap.get(patientId);
    }

    /**
     * Updates an existing medical record.
     * Validates and processes record updates.
     * 
     * @param patientId ID of the patient
     * @param diagnosis New diagnosis information
     * @param prescription New prescription information
     */
    public void updateRecord(String patientId, String diagnosis, String prescription) {
        MedicalRecord record = recordsMap.get(patientId);
        if (record != null) {
            if (diagnosis != null && !diagnosis.trim().isEmpty()) {
                record.setDiagnosis(diagnosis.trim());
            }
            if (prescription != null && !prescription.trim().isEmpty()) {
                record.setPrescription(prescription.trim());
            }
            saveRecords();
            System.out.println("Medical record updated successfully for patient: " + patientId);
        } else {
            System.err.println("No medical record found for patient: " + patientId);
        }
    }

    public void addNewRecord(String patientId, String name) {
        if (patientId == null || patientId.trim().isEmpty() ||
                name == null || name.trim().isEmpty()) {
            System.err.println("Patient ID and name cannot be empty");
            return;
        }

        if (!recordsMap.containsKey(patientId)) {
            // Get patient from UserController
            User user = userController.getUserById(patientId);
            if (!(user instanceof Patient)) {
                System.err.println("Invalid patient ID");
                return;
            }
            Patient patient = (Patient) user;

            MedicalRecord record = new MedicalRecord(patientId.trim(), name.trim(), "",   "");            
            recordsMap.put(patientId, record);
            saveRecords();
            System.out.println("New medical record created for patient: " + name);
        } else {
            System.err.println("Medical record already exists for patient ID: " + patientId);
        }
    }

    public void updateMedicalRecord(String patientId, String diagnosis, String prescription, String doctorId) {
        MedicalRecord record = recordsMap.get(patientId);
        if (record != null) {
            if (diagnosis != null && !diagnosis.trim().isEmpty()) {
                record.setDiagnosis(diagnosis.trim());
            }
            if (prescription != null && !prescription.trim().isEmpty()) {
                record.setPrescription(prescription.trim());
            }
            saveRecords();
            System.out.println("Medical record updated successfully by Doctor " + doctorId + 
                             " for patient: " + patientId);
        } else {
            System.err.println("No medical record found for patient: " + patientId);
        }
    }

    private void saveRecords() {
        CSVWriterUtil.writeCSV(MEDICAL_RECORD_PATH, writer -> {
            try {
                writeRecordsToCSV(writer);
            } catch (Exception e) {
                System.err.println("Error saving medical records: " + e.getMessage());
                throw new RuntimeException("Failed to save medical records", e);
            }
        });
    }

    private void writeRecordsToCSV(BufferedWriter writer) throws Exception {
        // Write header
        writer.write("PatientID,Name,Diagnosis,Prescription\n");

        // Write records
        for (MedicalRecord record : recordsMap.values()) {
            String diagnosis = escapeCSV(record.getDiagnosis());
            String prescription = escapeCSV(record.getPrescription());

            writer.write(String.format("%s,%s,%s,%s\n",
                    record.getPatientId(),
                    record.getPatientName(),
                    diagnosis,
                    prescription));
        }
    }

    private String escapeCSV(String field) {
        if (field == null)
            return "";
        field = field.replace("\"", "\"\""); // Escape quotes
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            field = "\"" + field + "\"";
        }
        return field;
    }

    public List<MedicalRecord> getAllRecords() {
        return new ArrayList<>(recordsMap.values());
    }

    public boolean hasRecord(String patientId) {
        return recordsMap.containsKey(patientId);
    }
}