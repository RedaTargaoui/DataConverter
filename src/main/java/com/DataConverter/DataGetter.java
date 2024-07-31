/**
 * Represents the class that gets data from file
 * @author Reda TARGAOUI & Ilyass EL MAAIDLI
 * @since 5 october 2023
 */
package com.DataConverter;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import java.io.File;
import java.io.InputStream;
import java.util.List;

public class DataGetter {

    /**
     * Extracts data from the specified .xlsx file (with first xml structure) and store data in a list
     * @param filePath File path
     * @param rowDataList The List to store data
     * @param patientCode patient code
     * @throws Exception Throws an exception if any error occurs during the parsing process
     */
    public static void getData(String filePath, List<RowData> rowDataList, String patientCode) throws Exception {
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
        // Obtain the input stream for the first sheet in the workbook :
        InputStream sheet = xssfReader.getSheetsData().next();
        // Create an XML reader for parsing the XML data :
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        // Display patient code :
        System.out.println("Generating file for patient : " + patientCode);


        // The content handler to process XML elements :
        xmlReader.setContentHandler(new DefaultHandler() {
            // Flags to determine type :
            boolean isNumeric = false;
            boolean isString = false;
            // Flag to skip first row :
            boolean skipFirstRow = true;
            boolean isTargetColumn = false;
            // To store current cell value :
            String cellValue = "";
            // To keep track of column index :
            int currentColumnIndex = -1;
            // RowData object to store value in a row :
            RowData rowData = null;

            /**
             * Called at the start of an XML element
             * @param uri The Uniform Resource Identifier of the namespace associated with the element
             * @param localName The local name (without prefix) of the element
             * @param name The qualified name (with prefix) of the element
             * @param attributes The attributes attached to the element
             */
            public void startElement(String uri, String localName, String name, Attributes attributes) {
                if ("x:c".equals(name)) {
                    // Get cell type :
                    String cellType = attributes.getValue("t");
                    // Check type :
                    isNumeric = "n".equals(cellType);
                    isString = cellType != null && "inlineStr".equals(cellType);
                    // Set isTargetColumn flag :
                    isTargetColumn = (currentColumnIndex == RT[0] || currentColumnIndex == GPX[0] ||
                            currentColumnIndex == GPY[0] || currentColumnIndex == PDL[0] || currentColumnIndex == PDR[0] ||
                            currentColumnIndex == PMN[0]);
                    // Initialize cellValue :
                    cellValue = "";
                }
            }

            /**
             * Called when character data is encountered within an element
             * @param ch The array containing the characters
             * @param start The starting position in the character array
             * @param length The number of characters to use from the character array
             */
            public void characters(char[] ch, int start, int length) {

                cellValue += new String(ch, start, length);
            }

            /**
             * Called at the end of an XML element
             * @param uri The Uniform Resource Identifier of the namespace associated with the element
             * @param localName The local name (without prefix) of the element
             * @param name The qualified name (with prefix) of the element
             */
            public void endElement(String uri, String localName, String name) {
                // If it's a numeric value :
                if ("x:v".equals(name)) {
                    if (isNumeric) {
                        setColumnValue(currentColumnIndex, cellValue, rowData);
                    }
                    cellValue = "";
                }
                // If its a cell, increment column index :
                else if ("x:c".equals(name)) {
                    currentColumnIndex++;
                }
                // If it's a string value :
                else if ("x:is".equals(name)) {
                    if (isString) {
                        //If it's the first row, search for the column indexes that we need
                        if(skipFirstRow){
                            updateColumnIndices(cellValue, currentColumnIndex);
                        }else {
                            if (isTargetColumn) {
                                setColumnValue(currentColumnIndex, cellValue, rowData);
                            }
                        }

                    }
                    cellValue = "";
                }
                // If it's the end of row :
                else if ("x:row".equals(name)) {
                    // If skipFirstRow flag is false :
                    if (!skipFirstRow) {
                        // Check if object is not empty and the conditions for image name is met :
                        if (rowData != null && !(rowData.getImage().isEmpty() || rowData.getImage().contains("croix"))) {
                            // Add object to list :
                            rowDataList.add(rowData);
                        }
                    }
                    // If skipFirstRow flag is true, make it false :
                    else {
                        skipFirstRow = false;
                        /*
                        //Just for tests
                        System.out.println("After 1 row: "+filePath);
                        System.out.println(RT[0]+1);
                        System.out.println(GPX[0]+1);
                        System.out.println(GPY[0]+1);
                        System.out.println(PDL[0]+1);
                        System.out.println(PDR[0]+1);
                        System.out.println(PMN[0]+1);
                        System.out.println("\n \n");
                        */

                        //Check if we did find all the columns
                        if(RT[0] ==-2 || GPX[0] ==-2 || GPY[0] ==-2 || PDL[0] ==-2 || PDR[0] ==-2 || PMN[0] ==-2){
                            try {
                                throw new Exception("Error: The required column does not exist in the Excel file. The program has stopped.\n File: " + filePath);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    // Initialize rowData object and column index :
                    rowData = new RowData();
                    currentColumnIndex = -1;
                }
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
