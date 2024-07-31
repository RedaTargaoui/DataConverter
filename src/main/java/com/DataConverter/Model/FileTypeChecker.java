/**
 * Represents the class that verifies the type of the .xlsx file
 * @author Ilyass EL MAAIDLI & Reda TARGAOUI
 * @since 05 december 2023
 */
package com.DataConverter.Model;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import java.io.File;

public class FileTypeChecker {

    /**
     * Check file type
     * @param filePath file path
     * @return true if its first xml structure, false otherwise
     */
    public static boolean checkType(String filePath) {
        // Flag to check if file has SharedStringsTable :
        boolean hasSharedStringsTable = false;

        try {
            // Open file :
            OPCPackage opcPackage = OPCPackage.open(new File(filePath));
            XSSFReader xssfReader = new XSSFReader(opcPackage);

            // Check for the presence of SharedStringsTable :
            SharedStringsTable sharedStringsTable = (SharedStringsTable) xssfReader.getSharedStringsTable();
            if (sharedStringsTable != null) {
                // Set flag to true :
                hasSharedStringsTable = true;
            }

            opcPackage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return !hasSharedStringsTable;
    }
}
