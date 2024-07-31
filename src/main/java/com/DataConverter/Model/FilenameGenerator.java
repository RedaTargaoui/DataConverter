/**
 * Represents the class that generates filename
 * @author Reda TARGAOUI & Ilyass EL MAAIDLI
 * @since 5 october 2023
 */
package com.DataConverter.Model;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.*;

public class FilenameGenerator {

    /**
     * Get patient data from file and generate filename
     * @param filePath path to file
     * @param patientCode patient code
     * @return filename
     */
    public static String getFilename(String filePath, String patientCode) {
        try {
            // Initialize variables to store data for filename :
            String initials = "";
            int age = 0;
            String sex = "";
            String group = "";
            // Flag to track if patient code is found or not ;
            boolean found = false;

            // patient code can't be empty :
            if (!patientCode.isEmpty()) {
                // Open file and get workbook :
                FileInputStream fileInputStream = new FileInputStream(filePath);
                Workbook workbook = WorkbookFactory.create(fileInputStream);
                // DataFormatter to convert data into strings :
                DataFormatter dataFormatter = new DataFormatter();

                Sheet sheet = workbook.getSheetAt(0);

                // Iterate through each row :
                for (Row row : sheet) {
                    // Get first cell which contains patient codes :
                    Cell firstCell = row.getCell(0);

                    if (firstCell != null) {
                        // Get first cell value :
                        String cellValue = dataFormatter.formatCellValue(firstCell);

                        if (cellValue.contains(patientCode)) {
                            // To track column indexes :
                            int columnIndex = 0;
                            // Iterate through each cell of the row :
                            for (Cell cell : row) {
                                // For each cell get the data :
                                if (columnIndex == 2) {
                                    initials = dataFormatter.formatCellValue(cell);
                                }
                                else if (columnIndex == 3) {
                                    // Parse cell value as double :
                                    double ageToDouble = Double.parseDouble(dataFormatter.formatCellValue(cell).replace(",", "."));
                                    age = (int) (ageToDouble * 12);
                                }
                                else if (columnIndex == 4) {
                                    sex = dataFormatter.formatCellValue(cell);
                                }
                                else if (columnIndex == 5) {
                                    group = dataFormatter.formatCellValue(cell);
                                }

                                columnIndex++;
                            }
                            // Set flag to ture when patient code is found :
                            found = true;
                        }
                    }
                }
                // If we didn't find patient code, we throw an exception :
                if (!found) {
                    throw new IllegalArgumentException("Patient code: " + patientCode + " not found in the file!");
                }
            }

            return group + "_" + patientCode + "_" + age + "_" + sex + "_" + initials;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Extract code from filename (ex : SIRCUS 2.0 SIRCUS-T2-007_SIRCUS)
     * @param filename filename
     * @return code
     */
    public static String extractCode(String filename) {
        // Set the pattern for matching the code :
        Pattern pattern = Pattern.compile("SIRCUS-T2-(\\d{3,5})");
        Matcher matcher = pattern.matcher(filename);
        // Find the first match :
        if (matcher.find()) {
            // return the code :
            return matcher.group(1);
        }
        return null;
    }

}
