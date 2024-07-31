/**
 * Represents the class that gets data from file
 * @author Ilyass EL MAAIDLI & Reda TARGAOUI
 * @since 05 december 2023
 */
package com.DataConverter;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import java.io.File;
import java.io.InputStream;
import java.util.List;

public class SecondTypeDataGetter {

    /**
     * Extracts data from the specified .xlsx file (with second xml structure) and store data in a list
     * @param filePath File path
     * @param rowDataList The List to store data
     * @param patientCode patient code
     * @throws Exception Throws an exception if any error occurs during the parsing process
     */
    public static void getData(String filePath, List<RowData> rowDataList, String patientCode) throws Exception{
        // needed column indices
        final int[] RT = {-2};
        final int[] GPX = {-2};
        final int[] GPY = {-2};
        final int[] PDL = {-2};
        final int[] PDR = {-2};
        final int[] PMN = {-2};
        //To prevent zipBomb because we are working with large files
        ZipSecureFile.setMinInflateRatio(0.005);
        // Open Packaging Convention for managing XML-based documents :
        OPCPackage opcPackage = OPCPackage.open(new File(filePath));
        // To open XML documents :
        XSSFReader xssfReader = new XSSFReader(opcPackage);
        // Retrieve the SharedStringsTable from the XSSFReader instance
        // The SharedStringsTable stores unique strings used in the Excel workbook
        SharedStringsTable sharedStringsTable = (SharedStringsTable) xssfReader.getSharedStringsTable();
        // Obtain the input stream for the first sheet in the workbook:
        InputStream sheet = xssfReader.getSheetsData().next();
        // Create an XML reader for parsing the XML data:
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        // Display patient code:
        System.out.println("Generating file for patient : " + patientCode);
        // Set the content handler to process XML elements:
        xmlReader.setContentHandler(new DefaultHandler() {
            // Flags to determine the target column and string type:
            boolean isTargetColumn = false;
            boolean isStringType = false;
            // Variable to store the cell value :
            String cellValue = "";
            // To keep track of column index :
            int currentColumnIndex = -1;
            // RowData object to store value in a row :
            RowData rowData = null;
            // Flag to skip first row (contains column titles) :
            boolean skipFirstRow = true;
            int  nbligne=0;

            /**
             * Receive notification of the start of an XML element during the parsing process
             * @param uri The Uniform Resource Identifier of the namespace associated with the element
             * @param localName The local name (without prefix) of the element
             * @param name The qualified name (with prefix) of the element
             * @param attributes The attributes attached to the element, if any
             */
            public void startElement(String uri, String localName, String name, Attributes attributes) {
                // Check if the current element is a cell :
                if (name.equals("c")) {

                    // Get 'r' attribute :
                    String column = attributes.getValue("r");
                    // Get current column index :
                    currentColumnIndex = getColIndexFromCellReference(column);
                    // Set isTargetColumn flag :
                    isTargetColumn = (currentColumnIndex == RT[0] || currentColumnIndex == GPX[0] ||
                            currentColumnIndex == GPY[0] || currentColumnIndex == PDL[0] || currentColumnIndex == PDR[0] ||
                            currentColumnIndex == PMN[0]);
                    // Get 't' attribute :
                    String cellType = attributes.getValue("t");
                    // Set isStringType flag :
                    isStringType = cellType != null && cellType.equals("s");


                    if (isStringType && skipFirstRow &&  !cellValue.isEmpty()) {
                        int idx = Integer.parseInt(cellValue);
                        cellValue = sharedStringsTable.getItemAt(idx).toString();
                        updateColumnIndices(cellValue, currentColumnIndex-1);
                    }
                    // Initialize cellValue :
                    cellValue = "";
                }
                // If it's the beginning of a row, and we already skipped first row :
                else if ("row".equals(name) && !skipFirstRow) {
                        // Initialize a new RowData for the next row :
                        rowData = new RowData();
                }
            }

            /**
             * Receive notification of character data within an element.
             * @param ch The array containing the characters
             * @param start The starting position in the character array
             * @param length The number of characters to use from the character array
             */
            public void characters(char[] ch, int start, int length) {
                cellValue += new String(ch, start, length);
            }

            /**
             * Receive notification of the end of an XML element
             * @param uri The Uniform Resource Identifier of the namespace associated with the element
             * @param localName The local name (without prefix) of the element
             * @param name The qualified name (with prefix) of the element
             */
            public void endElement(String uri, String localName, String name) {
                // Check if we reached the end of a row (end of the "row" element) :
                if ("row".equals(name)) {
                    // After Processing first row :
                    if(skipFirstRow){
                        skipFirstRow = false;
                        /*
                        //Just for tests
                        System.out.println("After 1 row: "+filePath);
                        System.out.println(RT[0]);
                        System.out.println(GPX[0]);
                        System.out.println(GPY[0]);
                        System.out.println(PDL[0]);
                        System.out.println(PDR[0]);
                        System.out.println(PMN[0]);
                        */

                        //Check if we did find  all the columns
                        if(RT[0] ==-2 || GPX[0] ==-2 || GPY[0] ==-2 || PDL[0] ==-2 || PDR[0] ==-2 || PMN[0] ==-2){
                            try {
                                throw new Exception("Error: The required column does not exist in the Excel file. The program has stopped.\n File: " + filePath);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }else {
                        // If the rowData is not empty and the condition for image name is met, add it to the rowDataList :
                        if (rowData != null && !(rowData.getImage().isEmpty() || rowData.getImage().contains("croix"))) {
                            rowDataList.add(rowData);
                        }

                        // Initialize rowData for the next row :
                        rowData = new RowData();
                    }
                }
                // If we reached one of our target columns :
                else if (isTargetColumn && rowData != null) {
                    // Get cell value :
                    if (isStringType) {
                        int idx = Integer.parseInt(cellValue);
                        cellValue = sharedStringsTable.getItemAt(idx).toString();
                    }

                    // Set the value in the correct attribute of rowData object :
                    setColumnValue(currentColumnIndex, cellValue, rowData);
                }

                // Reset flag isTargetColumn and currentColumnIndex :
                isTargetColumn = false;
                currentColumnIndex = -1;
            }

            /**
             * To set the value in the correct attribute of RowData based on the column index
             * @param columnIndex column index
             * @param value cell value
             * @param rowData rowData object
             */
            private void setColumnValue(int columnIndex, String value, RowData rowData) {
                // If object is not null and value is not empty :
                if (rowData != null && !value.isEmpty()) {
                    // Insert in the corresponding attribute :
                    if (columnIndex == RT[0]) {
                        rowData.setTime(Float.parseFloat(value) / 1000);
                    } else if (columnIndex == GPX[0]) {
                        rowData.setX(Float.parseFloat(value));
                    } else if (columnIndex == GPY[0]) {
                        rowData.setY(Float.parseFloat(value));
                    } else if (columnIndex == PDL[0]) {
                        rowData.setLeftDiam(Float.parseFloat(value));
                    } else if (columnIndex == PDR[0]) {
                        rowData.setRightDiam(Float.parseFloat(value));
                    } else if (columnIndex == PMN[0]) {
                        rowData.setImage(value);
                    }
                }
            }

            /**
             * Convert the Excel cell reference string to the corresponding column index
             * @param cellReference The cell reference string, e.g., "A1" or "B2"
             * @return The index of the column corresponding to the cell reference
             */
            private int getColIndexFromCellReference(String cellReference) {
                int sum = 0;
                for (int i = 0; i < cellReference.length(); i++) {
                    if (Character.isLetter(cellReference.charAt(i))) {
                        sum = sum * 26 + cellReference.charAt(i) - 'A' + 1;
                    }
                }
                return sum - 1;
            }

            /**
             * Updates the column indices based on cell values.
             *
             * @param cellValue            Cell value indicating column information
             * @param currentColumnIndex   Current column index being processed
             */
            private void updateColumnIndices(String cellValue, int currentColumnIndex) {
                if (cellValue.equals("Recording timestamp") || cellValue.equals("RecordingTimestamp_ms_") ) {
                    RT[0] = currentColumnIndex;
                } else if (cellValue.equals("Gaze point X") || cellValue.equals("GazePointX_DACSPx_")) {
                    GPX[0] = currentColumnIndex;
                } else if (cellValue.equals("Gaze point Y") || cellValue.equals("GazePointY_DACSPx_")) {
                    GPY[0] = currentColumnIndex;
                } else if (cellValue.equals("Pupil diameter left") || cellValue.equals("PupilDiameterLeft_mm_")) {
                    PDL[0] = currentColumnIndex;
                } else if (cellValue.equals("Pupil diameter right") || cellValue.equals("PupilDiameterRight_mm_")) {
                    PDR[0] = currentColumnIndex;
                } else if (cellValue.equals("Presented Media name") || cellValue.equals("PresentedMediaName")) {
                    PMN[0] = currentColumnIndex;
                }
            }

        });

        // Initiate the parsing of the XML data :
        InputSource inputSource = new InputSource(sheet);
        xmlReader.parse(inputSource);
        // Close the input stream for the sheet and close opcPackage :
        sheet.close();
        opcPackage.close();

    }
}
