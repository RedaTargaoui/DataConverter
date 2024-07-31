/**
 * Represents the writer to file class
 * @author Ilyass EL MAAIDLI & Reda TARGAOUI
 * @since 18 october 2023
 */
package com.DataConverter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class DataWriter {

    /**
     * Write String data to sheet
     * @param sheet the sheet to write to
     * @param rowNum row number
     * @param colNum column number
     * @param data data to write
     */
    public static void writeData(Sheet sheet, int rowNum, int colNum, String data) {
        // Get row, if it doesn't exist we create one :
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        // Get cell, if it doesn't exist we create one :
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
        }
        // Add data to cell :
        cell.setCellValue(data);
    }

    /**
     * Write float data to sheet
     * @param sheet the sheet to write to
     * @param rowNum row number
     * @param colNum column number
     * @param data data to write
     */
    public static void writeData(Sheet sheet, int rowNum, int colNum, float data) {
        // Get row, if it doesn't exist we create one :
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        // Get cell, if it doesn't exist we create one :
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
        }
        // Add data to cell :
        cell.setCellValue(data);
    }

    /**
     * TO write in STAT sheet
     * @param sheet sheet
     * @param imageName image name
     */
    public static void populateSTATSheet(Sheet sheet,String imageName) {
        // Content to write :
        Object[][] content = {
                {"", "", "", "", "", imageName + " DATA"},
                {"SLIDE METRICS:", "", "", "", "", ""},
                {"Time span shown start (seconds)", "", "", "", "", 0},
                {"Time span shown end (seconds)", "", "", "", "", 0},
                {"Total time shown (seconds)", "", "", "", "", 0},
                {"Total time tracked (seconds)", "", "", "", "", 0},
                {"Total tracking time lost (seconds)", "", "", "", "", 0},
                {"Total fixation duration (seconds)", "", "", "", "", 0},
                {"Total time nonfixated excluding gaps (seconds)", "", "", "", "", 0},
                {"Percent time tracked", "", "", "", "", 0},
                {"Percent tracking time lost", "", "", "", "", 0},
                {"Percent time fixated", "", "", "", "", 0},
                {"Percent time nonfixated excluding gaps", "", "", "", "", 0},
                {"Percent time fixated related to time tracked", "", "", "", "", 0},
                {"Percent time nonfixated related to time tracked", "", "", "", "", 0},
                {"Average pupil x diameter", "", "", "", "", 0},
                {"Average pupil y diameter", "", "", "", "", 0},
                {"Average pupil area", "", "", "", "", 0},
                {"Pupil x diameter std dev", "", "", "", "", 0},
                {"Pupil y diameter std dev", "", "", "", "", 0},
                {"Pupil area std dev", "", "", "", "", 0},
                {"Number of fixations", "", "", "", "", 0},
                {"Fixation count / Total time shown", "", "", "", "", 0},
                {"Fixation count / Total time tracked", "", "", "", "", 0},
                {"Average fixation duration (seconds)", "", "", "", "", 0},
                {"Std dev fixation duration (seconds)", "", "", "", "", 0},
                {"Average pupil x diameter in fixations", "", "", "", "", 0},
                {"Average pupil y diameter in fixations", "", "", "", "", 0},
                {"Average pupil area in fixations", "", "", "", "", 0},
                {"Pupil x diameter std dev in fixations", "", "", "", "", 0},
                {"Pupil y diameter std dev in fixations", "", "", "", "", 0},
                {"Pupil area std dev in fixations", "", "", "", "", 0},
                {"Number of gazepoints", "", "", "", "", 0},
                {"Gazepoint count / Total time shown", "", "", "", "", 0},
                {"Gazepoint count / Total time tracked", "", "", "", "", 0},
                {"Fixation points in zones", "", "", "", "", 0},
                {"Percent fixations in zones", "", "", "", "", 0},
                {"Gazepoints in zones", "", "", "", "", 0},
                {"Percent gazepoints in zones", "", "", "", "", 0},
                {"LOOKZONE METRICS:", "", "", "", "", ""},
                {"", "OUTSIDE OF ALL LOOKZONES", "", "", "", ""},
                {"Number of times zone observed", "", "", "", "", 0},
                {"Number of fixations before first arrival", "", "", "", "", 0},
                {"Duration before first fixation arrival (seconds)", "", "", "", "", 0},
                {"Total time in zone (seconds)", "", "", "", "", 0},
                {"Percentage of total fixations before first arrival", "", "", "", "", 0},
                {"Percentage of total slide time before first arrival", "", "", "", "", 0},
                {"Percent time spent in zone", "", "", "", "", 0},
                {"Average pupil x diameter", "", "", "", "", 0},
                {"Average pupil y diameter", "", "", "", "", 0},
                {"Average pupil area", "", "", "", "", 0},
                {"Pupil x diameter std dev", "", "", "", "", 0},
                {"Pupil y diameter std dev", "", "", "", "", 0},
                {"Pupil area std dev", "", "", "", "", 0},
                {"Fixation count", "", "", "", "", 0},
                {"Percentage of total fixations", "", "", "", "", 0},
                {"Total fixation duration (seconds)", "", "", "", "", 0},
                {"Total time not fixated (seconds)", "", "", "", "", 0},
                {"Percent time fixated related to time in zone", "", "", "", "", 0},
                {"Percent time nonfixated", "", "", "", "", 0},
                {"Percent time fixated related to total fixation duration", "", "", "", "", 0},
                {"Fixation count / Total time in zone", "", "", "", "", 0},
                {"Fixation count / Total fixation duration in zone", "", "", "", "", 0},
                {"Average fixation duration (seconds)", "", "", "", "", 0},
                {"Std dev fixation duration (seconds)", "", "", "", "", 0},
                {"Average pupil x diameter in fixations", "", "", "", "", 0},
                {"Average pupil y diameter in fixations", "", "", "", "", 0},
                {"Average pupil area in fixations", "", "", "", "", 0},
                {"Pupil x diameter std dev in fixations", "", "", "", "", 0},
                {"Pupil y diameter std dev in fixations", "", "", "", "", 0},
                {"Pupil area std dev in fixations", "", "", "", "", 0},
                {"Gazepoint count", "", "", "", "", 0},
                {"Gazepoint count / Total time in zone", "", "", "", "", 0},
                {"Gazepoint count / Total fixation duration in zone", "", "", "", "", 0}
        };

        // Iterate through content table and write in sheet :
        for (int i = 0; i < content.length; i++) {
            // create row :
            Row row = sheet.createRow(i);
            for (int j = 0; j < content[i].length; j++) {
                // Check if its integer or string :
                if (content[i][j] instanceof Integer) {
                    // Add integer value :
                    row.createCell(j).setCellValue((Integer) content[i][j]);
                }
                else {
                    // Add string value :
                    row.createCell(j).setCellValue(content[i][j].toString());
                }
            }
        }
    }

}
