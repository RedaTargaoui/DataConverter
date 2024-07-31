/**
 * Represents the file creator class
 * @author Ilyass EL MAAIDLI & Reda TARGAOUI
 * @since 18 october 2023
 */
package com.DataConverter;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public class FileCreator {

    /**
     * Generates an Excel file with sheets named according to the items in the image list
     * @param filePath the path where the Excel file will be generated
     * @param dataList the list that contains data
     */
    public static void createFile(String filePath, List<RowData> dataList) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // To store created images sheets :
            HashSet<String> createdImages = new HashSet<>();

            // For every image we create 3 sheets '- G', '- F' and '- STAT' :
            Sheet sheetG = null;
            // Flag to check if it's the first time we write in this sheet :
            boolean isFirst = false;
            // To keep track of current row number :
            int rowNum = 2;
            // Iterate through list  :
            for (RowData rowData : dataList) {
                //Check if Gaze points are valid :
                boolean isValidGazePoint = !(rowData.getX() < 0 || rowData.getY() < 0 || rowData.getX() > 1919 || rowData.getY() > 1079);
                // Check if row data is valid :
                boolean isValidData = rowData.getX() != 0 || rowData.getY() != 0 || rowData.getLeftDiam() != 0 || rowData.getRightDiam() != 0;
                // Check if we already have this images name in HashSet :
                if (createdImages.contains(rowData.getImage())) {
                    // Get the existing sheet for this image:
                    sheetG = workbook.getSheet(rowData.getImage() + " - G");
                    // Get the last row number in the existing sheet:
                    rowNum = sheetG.getLastRowNum() + 1; // Increment by 1 to append after the last row
                    if (rowNum == 2) isFirst = true;

                } else {
                    // Set flag to true :
                    isFirst = true;
                    // Add image :
                    createdImages.add(rowData.getImage());
                    // Create sheet :
                    sheetG = workbook.createSheet(rowData.getImage() +" - G");
                    workbook.createSheet(rowData.getImage() + " - F");
                    Sheet sheetSTAT = workbook.createSheet(rowData.getImage() + " - STAT");

                    // Write image name :
                    DataWriter.writeData(sheetG, 0, 0, rowData.getImage() + " - G");
                    DataWriter.writeData(sheetG, 1, 0, "No data!");

                    // Write to STAT sheet :
                    DataWriter.populateSTATSheet(sheetSTAT, rowData.getImage());

                    rowNum = 2;
                }

                // If row data is valid we write data to sheetG :
                if (isValidData && isValidGazePoint){
                    // If it's the first time we write in this sheet, we write column titles :
                    if (isFirst){
                        // Write column titles :
                        DataWriter.writeData(sheetG, 1, 0, "Slide");
                        DataWriter.writeData(sheetG, 1, 1, "Number");
                        DataWriter.writeData(sheetG, 1, 2, "x");
                        DataWriter.writeData(sheetG, 1, 3, "y");
                        DataWriter.writeData(sheetG, 1, 4, "Left Diam");
                        DataWriter.writeData(sheetG, 1, 5, "Right Diam");
                        DataWriter.writeData(sheetG, 1, 6, "Time");
                        DataWriter.writeData(sheetG, 1, 7, "LZ Name");
                        // Set isFirst flag to false :
                        isFirst = false;
                    }

                    // Write data to sheet :
                    DataWriter.writeData(sheetG, rowNum, 0, rowData.getImage());
                    DataWriter.writeData(sheetG, rowNum, 1, rowNum - 1);
                    DataWriter.writeData(sheetG, rowNum, 2, rowData.getX());
                    DataWriter.writeData(sheetG, rowNum, 3, rowData.getY());
                    DataWriter.writeData(sheetG, rowNum, 4, rowData.getLeftDiam());
                    DataWriter.writeData(sheetG, rowNum, 5, rowData.getRightDiam());
                    DataWriter.writeData(sheetG, rowNum, 6, rowData.getTime());

                    rowNum++;
                }
            }

            // write the contents of the workbook to the file :
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error while creating file!");
        }
    }

}

