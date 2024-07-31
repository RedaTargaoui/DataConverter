package com.DataConverter.View;

import com.DataConverter.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeView {
    // Swing Components:
    private JPanel HomePanel;
    private JTextField folderField;
    private JTextField dataFileField;
    private JButton getFolderButton;
    private JButton getFileButton;
    private JLabel titleLabel;
    private JLabel fieldLabel1;
    private JLabel fieldLabel2;
    private JButton launchButton;
    private JLabel waitingLabel;

    /**
     * Constructor
     */
    public HomeView() {
        // Add action listeners to buttons:
        getFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFolderChooser();
            }
        });

        getFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFileChooser();
            }
        });

        launchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launch();
            }
        });
    }

    /**
     * Open folder chooser
     */
    private void openFolderChooser() {
        // Set the JFileChooser to select entry folder:
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setDialogTitle("Select entry files folder");
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int userSelection = folderChooser.showOpenDialog(HomePanel);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File folder = folderChooser.getSelectedFile();
            folderField.setText(folder.getAbsolutePath());
        }
    }

    /**
     * Open file chooser
     */
    private void openFileChooser() {
        // Set the JFileChooser to select patients data file:
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select patients data file");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int userSelection = fileChooser.showOpenDialog(HomePanel);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            dataFileField.setText(file.getAbsolutePath());
        }
    }

    /**
     * Launch button action
     */
    private void launch() {
        // Get fields content:
        String folderPath = folderField.getText();
        String dataFilePath = dataFileField.getText();
        // Check if empty:
        if (folderPath.isEmpty() || dataFilePath.isEmpty()) {
            JOptionPane.showMessageDialog(HomePanel, "Please select folder and data file before launching");
        } else {
            // Get dataFile name without path:
            File dataFile = new File(dataFilePath);
            String patientsDataFile = dataFile.getName();
            // Start data conversion process:
            if (dataConversionProcess(folderPath, patientsDataFile)) {
                JOptionPane.showMessageDialog(HomePanel, "All files processed successfully");
            } else {
                JOptionPane.showMessageDialog(HomePanel, "No files found in the directory");
            }
        }
    }

    /**
     * Get panel
     * @return JPanel
     */
    public JPanel getHomePanel() {
        return HomePanel;
    }

    /**
     * Launch data conversion process
     * @param folderPath entry files folder path
     * @param patientsDataFile patients data file
     * @return true in case of success, false otherwise
     */
    private boolean dataConversionProcess(String folderPath, String patientsDataFile) {
        long startTime = System.currentTimeMillis();
        // To get folder files :
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        File[] processedFiles = null;
        List<File> tempList = new ArrayList<>();

        // Iterate through files, and verify every file :
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".xlsx") && !file.getName().equals(patientsDataFile)) {
                tempList.add(file);
            }
        }

        if (tempList.isEmpty()) {
            return false;
        }

        processedFiles = tempList.toArray(new File[0]);

        if (processedFiles != null) {
            //****String outputFolderPath = folderPath + "../Results" + File.separator;
            // Set output folder path :
            String outputFolderPath = folderPath + File.separator + "Results" + File.separator;
            // Create output folder :
            File outputFolder = new File(outputFolderPath);
            if (!outputFolder.exists()) {
                outputFolder.mkdirs();
            }

            // Use concurrency, number of available processors :
            int concurrencyLimit = Runtime.getRuntime().availableProcessors() - 2;
            // Ensure the concurrency limit is at least 1 :
            if (concurrencyLimit < 0) {
                concurrencyLimit = 1;
            }
            // Create a fixed thread pool with the determined concurrency limit :
            ExecutorService executor = Executors.newFixedThreadPool(concurrencyLimit);
            // Execute file processing tasks in parallel :
            for (File file : processedFiles) {
                executor.execute(() -> processFile(file, folderPath + File.separator, patientsDataFile, outputFolderPath));
            }
            // Shut down the executor service after all tasks are submitted :
            executor.shutdown();
            // Wait for the executor to finish all tasks :
            while (!executor.isTerminated()) {
                // Waiting for tasks to finish...
            }

            //System.out.println("\nAll files processed successfully");
            long endTime = System.currentTimeMillis();
            System.out.println("\n\nTotal Execution time : " + ((endTime - startTime) / 1000.0));
            return true;
        }
        else {
            //System.out.println("\nNo files found in the directory.");
            return false;
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("Data Converter");
        frame.setContentPane(new HomeView().getHomePanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
