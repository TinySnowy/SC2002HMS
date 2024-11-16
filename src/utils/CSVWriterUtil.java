package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CSVWriterUtil {

    public interface CSVWriter {
        void write(BufferedWriter writer) throws IOException;
    }

    public static void writeCSV(String filePath, CSVWriter csvWriter) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            csvWriter.write(writer);
            System.out.println("Data successfully written to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }
}
