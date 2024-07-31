/**
 * Represents the class Home view
 * @author Ilyass EL MAAIDLI & Reda TARGAOUI
 * @since 9 January 2024
 */
package com.DataConverter.View;

import com.DataConverter.Controller.DataGetter;
import com.DataConverter.Controller.SecondTypeDataGetter;
import com.DataConverter.Model.FileCreator;
import com.DataConverter.Model.FileTypeChecker;
import com.DataConverter.Model.FilenameGenerator;
import com.DataConverter.Model.RowData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
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
    private JLabel messageLabel;
    private JButton seeResultsButton;
    private JDialog waitingDialog;

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
            JOptionPane.showMessageDialog(HomePanel, "Please select folder and data file before launching",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            messageLabel.setText("");
            // Start progress bar:
            startProgressBar();
            // Get dataFile name without path:
            File dataFile = new File(dataFilePath);
            String patientsDataFile = dataFile.getName();
            // Start data conversion process:
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return dataConversionProcess(folderPath, patientsDataFile);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            waitingDialog.dispose();
                            // Set to display results:
                            seeResultsButton.setVisible(true);
                            openResultsFolder(folderPath + File.separator + "Results");
                            JOptionPane.showMessageDialog(HomePanel, "All files processed successfully");
                        } else {
                            waitingDialog.dispose();
                            JOptionPane.showMessageDialog(HomePanel, "No files found in the directory");
                            messageLabel.setText("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }

    /**
     * Open results folder
     */
    private void openResultsFolder(String folderPath) {
        seeResultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File folder = new File(folderPath);
                    Desktop.getDesktop().open(folder);
                } catch (IOException exception) {
                    JOptionPane.showMessageDialog(HomePanel, "Failed to open folder", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Get panel
     * @return JPanel
     */
    public JPanel getHomePanel() {
        return HomePanel;
    }

    /**
     * Create and start progress bar
     */
    private void startProgressBar() {
        // Set waiting dialog:
        waitingDialog = new JDialog((JFrame)null ,"");
        waitingDialog.setUndecorated(true);
        waitingDialog.setLayout(new FlowLayout(FlowLayout.CENTER));
        waitingDialog.setSize(300, 60);
        waitingDialog.setLocationRelativeTo(HomePanel);
        // Set progress bar:
        JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL);
        progressBar.setIndeterminate(true);
        progressBar.setForeground(Color.GREEN);
        // Set message label:
        JLabel message = new JLabel("Conversion process is running...");
        message.setFont(new Font("JetBrains Mono", Font.ITALIC, 16));
        // Add message and progress bar:
        waitingDialog.add(message);
        waitingDialog.add(progressBar);
        // Display waiting dialog
        waitingDialog.setVisible(true);
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

            long endTime = System.currentTimeMillis();
            messageLabel.setText("Process finished, Execution time : " + ((endTime - startTime) / 1000.0));
            return true;
        }
        else {
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

        } catch (Exception e) {
            System.out.println(file+"*************");
            e.printStackTrace();
        }
    }
}
