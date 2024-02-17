package csddata;

import java.io.BufferedR
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class EquipmentCSVValidator {

    public static void main(String[] args) {
        String filePath = "c:\\Users\\vinay\\Desktop\\Book2.csv";

        // Validate CSV data
        EquipmentCSVValidator csvValidator = new EquipmentCSVValidator();
        csvValidator.validateCsvData(filePath);
    }

    private boolean isValid;

    public void validateCsvData(String filePath) {
        isValid = true;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Validate header
            String header = reader.readLine();
            String[] headers = header.split(",");

            // Validate headers for Equipment Id, Equipment Name, Serial Number, ...
            String[] expectedHeaders = {"Equipment Id", "Equipment Name", "Serial Number", "Company Id", "Type", "Brand", "Model", "Reference", "Location", "Format", "Description", "Function", "IP Address", "Origin", "Barcode Type", "Barcode", "Consumption", "Weight", "Size HxWxD", "Insurance Value", "Purchase Value", "ATA Value", "QTY Value"};
            if (!isValidHeader(headers, expectedHeaders)) {
                System.out.println("Invalid CSV header.");
                isValid = false;
                return; // Stop processing if the header is invalid
            }

            // Validate data lines
            String line;
            int lineNumber = 2; // Start from line 2 (data lines)
            Set<String> uniqueEquipmentIds = new HashSet<>();
            Set<String> uniqueEquipmentNames = new HashSet<>();
            Set<String> uniqueSerialNumbers = new HashSet<>();
            Set<String> uniqueIPAddresses = new HashSet<>();
            Set<String> uniqueBarcodes = new HashSet<>();

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
            dateFormat.setLenient(false);
            Pattern functionPattern = Pattern.compile(".*>.*");

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",", -1); // -1 to keep empty trailing columns

                if (columns.length == headers.length) {
                    // Validate Equipment Id as not empty and unique
                    validateUniqueField(columns[0], lineNumber, uniqueEquipmentIds, "Equipment Id");

                    // Validate Equipment Name as not empty and unique
                    validateUniqueField(columns[1], lineNumber, uniqueEquipmentNames, "Equipment Name");

                    // Validate Serial Number as not empty and unique
                    validateUniqueField(columns[2], lineNumber, uniqueSerialNumbers, "Serial Number");

                    // Validate Company Id as '2' for Prodigious
                    validateCompanyId(columns[3], lineNumber);

                    // Validate other fields as needed
                    validateNotEmpty(columns[4], "Type", lineNumber);
                    validateNotEmpty(columns[5], "Brand", lineNumber);
                    validateNotEmpty(columns[6], "Model", lineNumber);
                    validateNotEmpty(columns[7], "Reference", lineNumber);
                    validateNotEmpty(columns[8], "Location", lineNumber);
                    validateNotEmpty(columns[9], "Format", lineNumber);
                    validateNotEmpty(columns[10], "Description", lineNumber);
                    validateNotEmpty(columns[11], "Function", lineNumber);
                    validateNotEmpty(columns[12], "IP Address", lineNumber);
                    validateNotEmpty(columns[13], "Origin", lineNumber);
                    validateNotEmpty(columns[14], "Barcode Type", lineNumber);

                    // Validate Barcode as not empty and unique
                    validateUniqueField(columns[15], lineNumber, uniqueBarcodes, "Barcode");

                    validateNotEmpty(columns[16], "Consumption", lineNumber);
                    validateNotEmpty(columns[17], "Weight", lineNumber);
                    validateNotEmpty(columns[18], "Size HxWxD", lineNumber);
                    validateNotEmpty(columns[19], "Insurance Value", lineNumber);
                    validateNotEmpty(columns[20], "Purchase Value", lineNumber);
                    validateNotEmpty(columns[21], "ATA Value", lineNumber);
                    validateNotEmpty(columns[22], "QTY Value", lineNumber);

                    // Validate Function to have '>'
                    validatePattern(columns[11], "Function", lineNumber, functionPattern);

                    // Validate other fields as needed
                    // ...
                } else {
                    System.out.println("Invalid number of columns in data line " + lineNumber);
                    isValid = false;
                }

                lineNumber++;
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading CSV file.");
            isValid = false;
        }

        if (isValid) {
            System.out.println("CSV file is valid.");
            downloadData(filePath);
        } else {
            System.out.println("CSV file is invalid.");
        }
    }

    private boolean isValidHeader(String[] actualHeaders, String[] expectedHeaders) {
        if (actualHeaders.length != expectedHeaders.length) {
            return false;
        }

        for (int i = 0; i < expectedHeaders.length; i++) {
            if (!actualHeaders[i].trim().equalsIgnoreCase(expectedHeaders[i])) {
                return false;
            }
        }

        return true;
    }

    private void validateUniqueField(String fieldValue, int lineNumber, Set<String> uniqueValues, String fieldName) {
        if (!isValidString(fieldValue) || !uniqueValues.add(fieldValue)) {
            System.out.println("Invalid or Duplicate " + fieldName + " found in data line " + lineNumber + ": " + fieldValue);
            isValid = false;
        }
    }

    private void validateNotEmpty(String value, String fieldName, int lineNumber) {
        if (!isValidString(value)) {
            System.out.println(fieldName + " is empty or missing in data line " + lineNumber);
            isValid = false;
        }
    }

    private void validateCompanyId(String companyId, int lineNumber) {
        if (!isValidString(companyId) || !companyId.trim().equals("2")) {
            System.out.println("Invalid Company Id in data line " + lineNumber);
            isValid = false;
        }
    }

    private void validatePattern(String value, String fieldName, int lineNumber, Pattern pattern) {
        if (!isValidString(value) || !pattern.matcher(value).matches()) {
            System.out.println(fieldName + " does not match the required pattern in data line " + lineNumber);
            isValid = false;
        }
    }

    private boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private void downloadData(String filePath) {
        // Read valid data and write to a new CSV file
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String outputFilePath = "valid_data.csv";
            FileWriter writer = new FileWriter(outputFilePath);
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line + "\n");
            }
            writer.close();
            System.out.println("Valid data downloaded to: " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error downloading valid data.");
        }
    }
}
