/**
 * Represents the class Main
 * @author Ilyass EL MAAIDLI & Reda TARGAOUI
 * @since 5 october 2023
 */
package com.DataConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {

        // Print an alert message:
        System.out.println("Warning: Always work with copies of the files to prevent any potential damage.\n");
        System.out.println("Note!!! The patient information file should be in the same directory as the other files.\n");
        // Set scanner to get user input :
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the folder path and hit Enter: ");
        // Get folder path :
        String folderPath = scanner.nextLine() + "/";
        System.out.print("\nEnter the patient information file name and press Enter(don't forget .xlsx): ");
        // Get information file :
        String outputFileInfo = scanner.nextLine();
        // Closed immediately after getting input
        scanner.close();

        // Check if the outputFileInfo exists in the directory:
        File checkInfoFile = new File(folderPath + outputFileInfo);
        if (!checkInfoFile.exists() || checkInfoFile.isDirectory()) {
            System.out.println("The specified patient information file does not exist in the directory.");
            return;
        }

        try {
            long startTime = System.currentTimeMillis();
            // To get folder files :
            File folder = new File(folderPath);
            File[] files = folder.listFiles();
            File[] processedFiles = null;
            List<File> tempList = new ArrayList<>();

            // Iterate through files, and verify every file :
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".xlsx") && !file.getName().equals(outputFileInfo)) {
                    tempList.add(file);
                }
            }

            processedFiles = tempList.toArray(new File[0]);


            if (processedFiles != null) {
                //****String outputFolderPath = folderPath + "../Results" + File.separator;
                // Set output folder path :
                String outputFolderPath = folderPath + "Results" + File.separator;
                // Create output folder :
                File outputFolder = new File(outputFolderPath);
                if (!outputFolder.exists()) {
                    outputFolder.mkdirs();
                }



                // Use concurrency :
                // Number of available processors :
                int concurrencyLimit = Runtime.getRuntime().availableProcessors() - 2;
                // Ensure the concurrency limit is at least 1 :
                if (concurrencyLimit < 0) {
                    concurrencyLimit = 1;
                }
                // Create a fixed thread pool with the determined concurrency limit :
                ExecutorService executor = Executors.newFixedThreadPool(concurrencyLimit);
                // Execute file processing tasks in parallel :
                for (File file : processedFiles) {
                    executor.execute(() -> processFile(file, folderPath, outputFileInfo, outputFolderPath));
                }
                // Shut down the executor service after all tasks are submitted :
                executor.shutdown();
                // Wait for the executor to finish all tasks :
                while (!executor.isTerminated()) {
                    // Waiting for tasks to finish...
                }

                System.out.println("\nAll files processed successfully");
            }
            else {
                System.out.println("\nNo files found in the directory.");
            }


            long endTime = System.currentTimeMillis();
            System.out.println("\n\nTotal Execution time : " + ((endTime - startTime) / 1000.0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Process file and verify its xml structure
     * @param file file
     * @param folderPath folder path
     * @param outputFileInfo output file info
     * @param outputFolderPath output folder path
     */
    private static void processFile(File file, String folderPath, String outputFileInfo, String outputFolderPath) {
        try {
            long startTime = System.currentTimeMillis();
            // Get filename :
            String inputFileName = file.getName();
            // Get patient code :
            String patientCode = FilenameGenerator.extractCode(folderPath + inputFileName);
            // Initialize list to store file data :
            List<RowData> rowDataList = new LinkedList<>();
            // Check file's xml structure :
            if (FileTypeChecker.checkType(folderPath + inputFileName)) {
                // If its first xml structure :
                System.out.println("The file has the default structure : " + inputFileName);
                // Open file and get data :
                DataGetter.getData(folderPath + inputFileName, rowDataList, patientCode);
            }
            else {
                // If its second xml structure :
                System.out.println("The file has the second type structure : " + inputFileName);
                // Open file and get data :
                SecondTypeDataGetter.getData(folderPath + inputFileName, rowDataList, patientCode);
            }
            // Generate output filename ;
            String outputFilename = FilenameGenerator.getFilename(folderPath + outputFileInfo, patientCode);
            // Create output file :
            FileCreator.createFile(outputFolderPath + outputFilename + ".xlsx", rowDataList);

            System.out.println("File '" + inputFileName + "' Processed successfully ");
            long endTime = System.currentTimeMillis();
            System.out.println("in Execution time : " + ((endTime - startTime) / 1000.0)+"\n");

        } catch (Exception e) {
            System.out.println(file+"*************");
            e.printStackTrace();
        }
    }

}

