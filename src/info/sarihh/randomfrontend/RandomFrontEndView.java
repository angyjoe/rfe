package info.sarihh.randomfrontend;

import java.awt.CardLayout;
import java.awt.Desktop;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.jdesktop.application.Task;
/*
 * Author: Sari Haj Hussein
 */
public class RandomFrontEndView extends FrameView {

    public RandomFrontEndView(SingleFrameApplication app) {
        super(app);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = RandomFrontEndApp.getApplication().getMainFrame();
            aboutBox = new RandomFrontEndAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        RandomFrontEndApp.getApplication().show(aboutBox);
    }
    
    @Action
    public void showENTBatteryPanel() {
        entBatteryMenuItem.setSelected(true);
        diehardBatteryMenuItem.setSelected(false);
        nistBatteryMenuItem.setSelected(false);
        ((CardLayout)mainPanel.getLayout()).show(mainPanel, "card1");
    }
    
    @Action
    public void showDIEHARDBatteryPanel() {
        entBatteryMenuItem.setSelected(false);
        diehardBatteryMenuItem.setSelected(true);
        nistBatteryMenuItem.setSelected(false);
        ((CardLayout)mainPanel.getLayout()).show(mainPanel, "card2");
    }
    
    @Action
    public void showNISTBatteryPanel() {
        entBatteryMenuItem.setSelected(false);
        diehardBatteryMenuItem.setSelected(false);
        nistBatteryMenuItem.setSelected(true);
        ((CardLayout)mainPanel.getLayout()).show(mainPanel, "card3");
    }
    
    @Action
    public void chooseInputFile() {
        JFrame mainFrame = RandomFrontEndApp.getApplication().getMainFrame();
        if (fileChooser.showOpenDialog(mainFrame) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File f = fileChooser.getSelectedFile();
        if ((f == null) || !f.isFile()) {
            return;
        }
        inputFileTextField.setText(f.getAbsolutePath());
    }
    
    @Action
    public void chooseInputFile1() {
        JFrame mainFrame = RandomFrontEndApp.getApplication().getMainFrame();
        if (fileChooser.showOpenDialog(mainFrame) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File f = fileChooser.getSelectedFile();
        if ((f == null) || !f.isFile()) {
            return;
        }
        inputFileTextField1.setText(f.getAbsolutePath());
    }
    
    @Action
    public void chooseInputFile2() {
        JFrame mainFrame = RandomFrontEndApp.getApplication().getMainFrame();
        if (fileChooser.showOpenDialog(mainFrame) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File f = fileChooser.getSelectedFile();
        if ((f == null) || !f.isFile()) {
            return;
        }
        inputFileTextField2.setText(f.getAbsolutePath());
    }
    
    @Action
    public void showENTTestsDocumentation() throws Exception {
        Desktop.getDesktop().open(new File("documentation//Pseudorandom Number Sequence Test Program.htm"));
    }
    
    @Action
    public void showDIEHARDTestsDocumentation() throws Exception {
        Desktop.getDesktop().open(new File("documentation//TESTS.TXT"));
    }
    
    @Action
    public void showNISTTestsDocumentation() throws Exception {
        Desktop.getDesktop().open(new File("documentation//SP800-22rev1.pdf"));
    }
    
    @Action
    public void clearOutput() throws Exception {
        outputTextArea.setText("");
    }
    
    @Action
    public void clearOutput1() throws Exception {
        outputTextArea1.setText("");
    }
    
    @Action
    public void clearOutput2() throws Exception {
        outputTextArea2.setText("");
    }
    
    @Action
    public Task processENTInputFile() {
       return new ProcessENTInputFileTask(getApplication());
    }
    
    private class ProcessENTInputFileTask extends Task {
        ProcessENTInputFileTask(org.jdesktop.application.Application app) {
            super(app);
        }
        @Override
        protected Void doInBackground() {
            JFrame mainFrame = RandomFrontEndApp.getApplication().getMainFrame();
            if (inputFileTextField.getText().length() < 1) {
                JOptionPane.showMessageDialog(mainFrame, "Missing input file!", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            try {
                setMessage("Constructing ENT Battery process...");
                ProcessBuilder pb = new ProcessBuilder("ent.exe",
                        (bOptionCheckBox.isSelected() == false) ? "" : "-b",
                        (cOptionCheckBox.isSelected() == false) ? "" : "-c",
                        (fOptionCheckBox.isSelected() == false) ? "" : "-f",
                        (tOptionCheckBox.isSelected() == false) ? "" : "-t",
                        inputFileTextField.getText());
                pb.directory(new File("."));
                pb.redirectErrorStream(true);

                setMessage("Starting ENT Battery process...");
                Process process = pb.start();

                setMessage("Getting ENT Battery process output...");
                RandomFrontEndApp.getApplication().getMainFrame().validate();
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    outputTextArea.append(line + "\n");
                    outputTextArea.setCaretPosition(outputTextArea.getText().length());
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void finished() {
            setMessage("Done.");
        }
    }
    
    @Action
    public Task processDIEHARDInputFile() {
       return new ProcessDIEHARDInputFileTask(getApplication());
    }
    
    private class ProcessDIEHARDInputFileTask extends Task {
        ProcessDIEHARDInputFileTask(org.jdesktop.application.Application app) {
            super(app);
        }
        @Override
        protected Void doInBackground() {
            JFrame mainFrame = RandomFrontEndApp.getApplication().getMainFrame();
            if (inputFileTextField1.getText().length() < 1) {
                JOptionPane.showMessageDialog(mainFrame, "Missing input file!", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            try {
                setMessage("Constructing DIEHARD Battery process...");
                ProcessBuilder pb = new ProcessBuilder("diehard.exe",
                        (test1CheckBox.isSelected() == false) ? "-1" : "",
                        (test2CheckBox.isSelected() == false) ? "-2" : "",
                        (test3CheckBox.isSelected() == false) ? "-3" : "",
                        (test4CheckBox.isSelected() == false) ? "-4" : "",
                        (test5CheckBox.isSelected() == false) ? "-5" : "",
                        (test6CheckBox.isSelected() == false) ? "-6" : "",
                        (test7CheckBox.isSelected() == false) ? "-7" : "",
                        (test8CheckBox.isSelected() == false) ? "-8" : "",
                        (test9CheckBox.isSelected() == false) ? "-9" : "",
                        (test10CheckBox.isSelected() == false) ? "-10" : "",
                        (test11CheckBox.isSelected() == false) ? "-11" : "",
                        (test12CheckBox.isSelected() == false) ? "-12" : "",
                        (test13CheckBox.isSelected() == false) ? "-13" : "",
                        (test14CheckBox.isSelected() == false) ? "-14" : "",
                        (test15CheckBox.isSelected() == false) ? "-15" : "",
                        (test16CheckBox.isSelected() == false) ? "-16" : "",
                        (test17CheckBox.isSelected() == false) ? "-17" : "",
                        inputFileTextField1.getText());
                pb.directory(new File("."));
                pb.redirectErrorStream(true);

                setMessage("Starting DIEHARD Battery process...");
                Process process = pb.start();

                setMessage("Getting DIEHARD Battery process output...");
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    outputTextArea1.append(line + "\n");
                    outputTextArea1.setCaretPosition(outputTextArea1.getText().length());
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void finished() {
            setMessage("Done.");
        }
    }
    
    @Action
    public Task processNISTInputFile() {
       return new ProcessNISTInputFileTask(getApplication());
    }
    
    private class ProcessNISTInputFileTask extends Task {
        ProcessNISTInputFileTask(org.jdesktop.application.Application app) {
            super(app);
        }
        @Override
        protected Void doInBackground() {
            JFrame mainFrame = RandomFrontEndApp.getApplication().getMainFrame();
            if (inputFileTextField2.getText().length() < 1) {
                JOptionPane.showMessageDialog(mainFrame, "Missing input file!", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            if (bitStreamLengthTextField.getText().length() < 1) {
                JOptionPane.showMessageDialog(mainFrame, "Missing bit stream length!", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            if (numberOfBitStreamsTextField.getText().length() < 1) {
                JOptionPane.showMessageDialog(mainFrame, "Missing number of bit streams!", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            try {
                setMessage("Constructing NIST Battery process...");
                String testToApply = "";
                testToApply += (test1CheckBox1.isSelected() == true) ? "1" : "0";
                testToApply += (test2CheckBox1.isSelected() == true) ? "1" : "0";
                testToApply += (test3CheckBox1.isSelected() == true) ? "1" : "0";
                testToApply += (test4CheckBox1.isSelected() == true) ? "1" : "0";
                testToApply += (test5CheckBox1.isSelected() == true) ? "1" : "0";
                testToApply += (test6CheckBox1.isSelected() == true) ? "1" : "0";
                testToApply += (test7CheckBox1.isSelected() == true) ? "1" : "0";
                testToApply += (test8CheckBox1.isSelected() == true) ? "1" : "0";
                testToApply += (test9CheckBox1.isSelected() == true) ? "1" : "0";
                testToApply += (test10CheckBox1.isSelected() == true) ? "1" : "0";
                testToApply += (test11CheckBox1.isSelected() == true) ? "1" : "0";
                testToApply += (test12CheckBox1.isSelected() == true) ? "1" : "0";
                testToApply += (test13CheckBox1.isSelected() == true) ? "1" : "0";
                testToApply += (test14CheckBox1.isSelected() == true) ? "1" : "0";
                testToApply += (test15CheckBox1.isSelected() == true) ? "1" : "0";
                ProcessBuilder pb = new ProcessBuilder("NIST.exe",
                        bitStreamLengthTextField.getText(),
                        inputFileTextField2.getText(),
                        "0",
                        testToApply,
                        numberOfBitStreamsTextField.getText(),
                        (asciiFileFormatRadioButton.isSelected() == true) ? "0" : "1");
                pb.directory(new File("."));
                pb.redirectErrorStream(true);

                setMessage("Starting NIST Battery process...");
                Process process = pb.start();

                setMessage("Getting NIST Battery process output...");
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    outputTextArea2.append(line + "\n");
                    outputTextArea2.setCaretPosition(outputTextArea2.getText().length());
                }
                br.close();
                
                setMessage("Displaying NIST Battery analysis report...");
                br = new BufferedReader(new FileReader("finalAnalysisReport"));
                while ((line = br.readLine()) != null) {
                    outputTextArea2.append(line + "\n");
                    outputTextArea2.setCaretPosition(outputTextArea2.getText().length());
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void finished() {
            setMessage("Done.");
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        entBatteryPanel = new javax.swing.JPanel();
        inputPanel = new javax.swing.JPanel();
        inputFileLabel = new javax.swing.JLabel();
        inputFileTextField = new javax.swing.JTextField();
        chooseInputFileButton = new javax.swing.JButton();
        bOptionCheckBox = new javax.swing.JCheckBox();
        cOptionCheckBox = new javax.swing.JCheckBox();
        fOptionCheckBox = new javax.swing.JCheckBox();
        tOptionCheckBox = new javax.swing.JCheckBox();
        processButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        entTestsDocumentationButton = new javax.swing.JButton();
        outputPanel = new javax.swing.JPanel();
        outputScrollPane = new javax.swing.JScrollPane();
        outputTextArea = new javax.swing.JTextArea();
        diehardBatteryPanel = new javax.swing.JPanel();
        inputPanel1 = new javax.swing.JPanel();
        inputFileLabel1 = new javax.swing.JLabel();
        inputFileTextField1 = new javax.swing.JTextField();
        chooseInputFileButton1 = new javax.swing.JButton();
        test1CheckBox = new javax.swing.JCheckBox();
        test2CheckBox = new javax.swing.JCheckBox();
        test3CheckBox = new javax.swing.JCheckBox();
        test4CheckBox = new javax.swing.JCheckBox();
        test5CheckBox = new javax.swing.JCheckBox();
        test6CheckBox = new javax.swing.JCheckBox();
        test7CheckBox = new javax.swing.JCheckBox();
        test8CheckBox = new javax.swing.JCheckBox();
        test9CheckBox = new javax.swing.JCheckBox();
        test10CheckBox = new javax.swing.JCheckBox();
        test11CheckBox = new javax.swing.JCheckBox();
        test12CheckBox = new javax.swing.JCheckBox();
        test13CheckBox = new javax.swing.JCheckBox();
        test14CheckBox = new javax.swing.JCheckBox();
        test15CheckBox = new javax.swing.JCheckBox();
        test16CheckBox = new javax.swing.JCheckBox();
        test17CheckBox = new javax.swing.JCheckBox();
        processButton1 = new javax.swing.JButton();
        diehardTestsDocumentationButton = new javax.swing.JButton();
        clearButton1 = new javax.swing.JButton();
        outputPanel1 = new javax.swing.JPanel();
        outputScrollPane1 = new javax.swing.JScrollPane();
        outputTextArea1 = new javax.swing.JTextArea();
        nistBatteryPanel = new javax.swing.JPanel();
        inputPanel2 = new javax.swing.JPanel();
        inputFileLabel2 = new javax.swing.JLabel();
        inputFileTextField2 = new javax.swing.JTextField();
        chooseInputFileButton2 = new javax.swing.JButton();
        bitStreamLengthLabel = new javax.swing.JLabel();
        bitStreamLengthTextField = new javax.swing.JTextField();
        numberOfBitStreamsLabel = new javax.swing.JLabel();
        numberOfBitStreamsTextField = new javax.swing.JTextField();
        asciiFileFormatRadioButton = new javax.swing.JRadioButton();
        binaryFileFormatRadioButton = new javax.swing.JRadioButton();
        test1CheckBox1 = new javax.swing.JCheckBox();
        test2CheckBox1 = new javax.swing.JCheckBox();
        test3CheckBox1 = new javax.swing.JCheckBox();
        test4CheckBox1 = new javax.swing.JCheckBox();
        test5CheckBox1 = new javax.swing.JCheckBox();
        test6CheckBox1 = new javax.swing.JCheckBox();
        test7CheckBox1 = new javax.swing.JCheckBox();
        test8CheckBox1 = new javax.swing.JCheckBox();
        test9CheckBox1 = new javax.swing.JCheckBox();
        test10CheckBox1 = new javax.swing.JCheckBox();
        test11CheckBox1 = new javax.swing.JCheckBox();
        test12CheckBox1 = new javax.swing.JCheckBox();
        test13CheckBox1 = new javax.swing.JCheckBox();
        test14CheckBox1 = new javax.swing.JCheckBox();
        test15CheckBox1 = new javax.swing.JCheckBox();
        processButton2 = new javax.swing.JButton();
        clearButton2 = new javax.swing.JButton();
        nistTestsDocumentationButton = new javax.swing.JButton();
        outputPanel2 = new javax.swing.JPanel();
        outputScrollPane2 = new javax.swing.JScrollPane();
        outputTextArea2 = new javax.swing.JTextArea();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu batteryMenu = new javax.swing.JMenu();
        entBatteryMenuItem = new javax.swing.JRadioButtonMenuItem();
        diehardBatteryMenuItem = new javax.swing.JRadioButtonMenuItem();
        nistBatteryMenuItem = new javax.swing.JRadioButtonMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        batteryButtonsGroup = new javax.swing.ButtonGroup();
        fileChooser = new javax.swing.JFileChooser();
        fileFormatButtonGroup = new javax.swing.ButtonGroup();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.CardLayout());

        entBatteryPanel.setBackground(new java.awt.Color(255, 255, 255));
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(info.sarihh.randomfrontend.RandomFrontEndApp.class).getContext().getResourceMap(RandomFrontEndView.class);
        entBatteryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("entBatteryPanel.border.title"))); // NOI18N
        entBatteryPanel.setName("entBatteryPanel"); // NOI18N
        entBatteryPanel.setLayout(new java.awt.BorderLayout());

        inputPanel.setBackground(new java.awt.Color(255, 255, 255));
        inputPanel.setName("inputPanel"); // NOI18N
        inputPanel.setPreferredSize(new java.awt.Dimension(856, 290));
        inputPanel.setLayout(new java.awt.GridBagLayout());

        inputFileLabel.setText(resourceMap.getString("inputFileLabel.text")); // NOI18N
        inputFileLabel.setName("inputFileLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 15, 5);
        inputPanel.add(inputFileLabel, gridBagConstraints);

        inputFileTextField.setFont(new java.awt.Font("Lucida Console", 0, 11));
        inputFileTextField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        inputFileTextField.setMinimumSize(new java.awt.Dimension(300, 20));
        inputFileTextField.setName("inputFileTextField"); // NOI18N
        inputFileTextField.setPreferredSize(new java.awt.Dimension(400, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 15, 5);
        inputPanel.add(inputFileTextField, gridBagConstraints);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(info.sarihh.randomfrontend.RandomFrontEndApp.class).getContext().getActionMap(RandomFrontEndView.class, this);
        chooseInputFileButton.setAction(actionMap.get("chooseInputFile")); // NOI18N
        chooseInputFileButton.setBackground(new java.awt.Color(255, 255, 255));
        chooseInputFileButton.setText(resourceMap.getString("chooseInputFileButton.text")); // NOI18N
        chooseInputFileButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        chooseInputFileButton.setMinimumSize(new java.awt.Dimension(30, 23));
        chooseInputFileButton.setName("chooseInputFileButton"); // NOI18N
        chooseInputFileButton.setPreferredSize(new java.awt.Dimension(30, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 15, 5);
        inputPanel.add(chooseInputFileButton, gridBagConstraints);

        bOptionCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        bOptionCheckBox.setSelected(true);
        bOptionCheckBox.setText(resourceMap.getString("bOptionCheckBox.text")); // NOI18N
        bOptionCheckBox.setBorder(null);
        bOptionCheckBox.setBorderPaintedFlat(true);
        bOptionCheckBox.setName("bOptionCheckBox"); // NOI18N
        bOptionCheckBox.setPreferredSize(new java.awt.Dimension(171, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 15, 5);
        inputPanel.add(bOptionCheckBox, gridBagConstraints);

        cOptionCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        cOptionCheckBox.setSelected(true);
        cOptionCheckBox.setText(resourceMap.getString("cOptionCheckBox.text")); // NOI18N
        cOptionCheckBox.setBorder(null);
        cOptionCheckBox.setBorderPaintedFlat(true);
        cOptionCheckBox.setName("cOptionCheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 15, 5);
        inputPanel.add(cOptionCheckBox, gridBagConstraints);

        fOptionCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        fOptionCheckBox.setSelected(true);
        fOptionCheckBox.setText(resourceMap.getString("fOptionCheckBox.text")); // NOI18N
        fOptionCheckBox.setBorder(null);
        fOptionCheckBox.setBorderPaintedFlat(true);
        fOptionCheckBox.setName("fOptionCheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 15, 5);
        inputPanel.add(fOptionCheckBox, gridBagConstraints);

        tOptionCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        tOptionCheckBox.setSelected(true);
        tOptionCheckBox.setText(resourceMap.getString("tOptionCheckBox.text")); // NOI18N
        tOptionCheckBox.setBorder(null);
        tOptionCheckBox.setBorderPaintedFlat(true);
        tOptionCheckBox.setName("tOptionCheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 15, 5);
        inputPanel.add(tOptionCheckBox, gridBagConstraints);

        processButton.setAction(actionMap.get("processENTInputFile")); // NOI18N
        processButton.setBackground(new java.awt.Color(255, 255, 255));
        processButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        processButton.setName("processButton"); // NOI18N
        processButton.setPreferredSize(new java.awt.Dimension(100, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 15, 5);
        inputPanel.add(processButton, gridBagConstraints);

        clearButton.setAction(actionMap.get("clearOutput")); // NOI18N
        clearButton.setBackground(new java.awt.Color(255, 255, 255));
        clearButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        clearButton.setName("clearButton"); // NOI18N
        clearButton.setPreferredSize(new java.awt.Dimension(100, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 15, 5);
        inputPanel.add(clearButton, gridBagConstraints);

        entTestsDocumentationButton.setAction(actionMap.get("showENTTestsDocumentation")); // NOI18N
        entTestsDocumentationButton.setBackground(new java.awt.Color(255, 255, 255));
        entTestsDocumentationButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        entTestsDocumentationButton.setEnabled(Desktop.getDesktop().isSupported(Desktop.Action.OPEN));
        entTestsDocumentationButton.setName("entTestsDocumentationButton"); // NOI18N
        entTestsDocumentationButton.setPreferredSize(new java.awt.Dimension(100, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 15, 5);
        inputPanel.add(entTestsDocumentationButton, gridBagConstraints);

        entBatteryPanel.add(inputPanel, java.awt.BorderLayout.NORTH);

        outputPanel.setName("outputPanel"); // NOI18N
        outputPanel.setLayout(new java.awt.BorderLayout());

        outputScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        outputScrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        outputScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outputScrollPane.setName("outputScrollPane"); // NOI18N
        outputScrollPane.setPreferredSize(new java.awt.Dimension(600, 240));

        outputTextArea.setEditable(false);
        outputTextArea.setFont(new java.awt.Font("Lucida Console", 0, 11));
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        outputTextArea.setBorder(null);
        outputTextArea.setName("outputTextArea"); // NOI18N
        outputScrollPane.setViewportView(outputTextArea);

        outputPanel.add(outputScrollPane, java.awt.BorderLayout.CENTER);

        entBatteryPanel.add(outputPanel, java.awt.BorderLayout.CENTER);

        mainPanel.add(entBatteryPanel, "card1");

        diehardBatteryPanel.setBackground(new java.awt.Color(255, 255, 255));
        diehardBatteryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("diehardBatteryPanel.border.title"))); // NOI18N
        diehardBatteryPanel.setName("diehardBatteryPanel"); // NOI18N
        diehardBatteryPanel.setLayout(new java.awt.BorderLayout());

        inputPanel1.setBackground(new java.awt.Color(255, 255, 255));
        inputPanel1.setName("inputPanel1"); // NOI18N
        inputPanel1.setPreferredSize(new java.awt.Dimension(856, 290));
        inputPanel1.setLayout(new java.awt.GridBagLayout());

        inputFileLabel1.setBackground(new java.awt.Color(255, 255, 255));
        inputFileLabel1.setText(resourceMap.getString("inputFileLabel1.text")); // NOI18N
        inputFileLabel1.setName("inputFileLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        inputPanel1.add(inputFileLabel1, gridBagConstraints);

        inputFileTextField1.setFont(new java.awt.Font("Lucida Console", 0, 11));
        inputFileTextField1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        inputFileTextField1.setMinimumSize(new java.awt.Dimension(300, 20));
        inputFileTextField1.setName("inputFileTextField1"); // NOI18N
        inputFileTextField1.setPreferredSize(new java.awt.Dimension(400, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        inputPanel1.add(inputFileTextField1, gridBagConstraints);

        chooseInputFileButton1.setAction(actionMap.get("chooseInputFile1")); // NOI18N
        chooseInputFileButton1.setBackground(new java.awt.Color(255, 255, 255));
        chooseInputFileButton1.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        chooseInputFileButton1.setMinimumSize(new java.awt.Dimension(30, 23));
        chooseInputFileButton1.setName("chooseInputFileButton1"); // NOI18N
        chooseInputFileButton1.setPreferredSize(new java.awt.Dimension(30, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        inputPanel1.add(chooseInputFileButton1, gridBagConstraints);

        test1CheckBox.setBackground(new java.awt.Color(255, 255, 255));
        test1CheckBox.setSelected(true);
        test1CheckBox.setText(resourceMap.getString("test1CheckBox.text")); // NOI18N
        test1CheckBox.setBorder(null);
        test1CheckBox.setBorderPaintedFlat(true);
        test1CheckBox.setName("test1CheckBox"); // NOI18N
        test1CheckBox.setPreferredSize(new java.awt.Dimension(171, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(test1CheckBox, gridBagConstraints);

        test2CheckBox.setBackground(new java.awt.Color(255, 255, 255));
        test2CheckBox.setSelected(true);
        test2CheckBox.setText(resourceMap.getString("test2CheckBox.text")); // NOI18N
        test2CheckBox.setBorder(null);
        test2CheckBox.setBorderPaintedFlat(true);
        test2CheckBox.setName("test2CheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(test2CheckBox, gridBagConstraints);

        test3CheckBox.setBackground(new java.awt.Color(255, 255, 255));
        test3CheckBox.setSelected(true);
        test3CheckBox.setText(resourceMap.getString("test3CheckBox.text")); // NOI18N
        test3CheckBox.setBorder(null);
        test3CheckBox.setBorderPaintedFlat(true);
        test3CheckBox.setName("test3CheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(test3CheckBox, gridBagConstraints);

        test4CheckBox.setBackground(new java.awt.Color(255, 255, 255));
        test4CheckBox.setSelected(true);
        test4CheckBox.setText(resourceMap.getString("test4CheckBox.text")); // NOI18N
        test4CheckBox.setBorder(null);
        test4CheckBox.setBorderPaintedFlat(true);
        test4CheckBox.setName("test4CheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(test4CheckBox, gridBagConstraints);

        test5CheckBox.setBackground(new java.awt.Color(255, 255, 255));
        test5CheckBox.setSelected(true);
        test5CheckBox.setText(resourceMap.getString("test5CheckBox.text")); // NOI18N
        test5CheckBox.setBorder(null);
        test5CheckBox.setBorderPaintedFlat(true);
        test5CheckBox.setName("test5CheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(test5CheckBox, gridBagConstraints);

        test6CheckBox.setBackground(new java.awt.Color(255, 255, 255));
        test6CheckBox.setSelected(true);
        test6CheckBox.setText(resourceMap.getString("test6CheckBox.text")); // NOI18N
        test6CheckBox.setBorder(null);
        test6CheckBox.setBorderPaintedFlat(true);
        test6CheckBox.setName("test6CheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(test6CheckBox, gridBagConstraints);

        test7CheckBox.setBackground(new java.awt.Color(255, 255, 255));
        test7CheckBox.setSelected(true);
        test7CheckBox.setText(resourceMap.getString("test7CheckBox.text")); // NOI18N
        test7CheckBox.setBorder(null);
        test7CheckBox.setBorderPaintedFlat(true);
        test7CheckBox.setName("test7CheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(test7CheckBox, gridBagConstraints);

        test8CheckBox.setBackground(new java.awt.Color(255, 255, 255));
        test8CheckBox.setSelected(true);
        test8CheckBox.setText(resourceMap.getString("test8CheckBox.text")); // NOI18N
        test8CheckBox.setBorder(null);
        test8CheckBox.setBorderPaintedFlat(true);
        test8CheckBox.setName("test8CheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(test8CheckBox, gridBagConstraints);

        test9CheckBox.setBackground(new java.awt.Color(255, 255, 255));
        test9CheckBox.setSelected(true);
        test9CheckBox.setText(resourceMap.getString("test9CheckBox.text")); // NOI18N
        test9CheckBox.setBorder(null);
        test9CheckBox.setBorderPaintedFlat(true);
        test9CheckBox.setName("test9CheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(test9CheckBox, gridBagConstraints);

        test10CheckBox.setBackground(new java.awt.Color(255, 255, 255));
        test10CheckBox.setSelected(true);
        test10CheckBox.setText(resourceMap.getString("test10CheckBox.text")); // NOI18N
        test10CheckBox.setBorder(null);
        test10CheckBox.setBorderPaintedFlat(true);
        test10CheckBox.setName("test10CheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(test10CheckBox, gridBagConstraints);

        test11CheckBox.setBackground(new java.awt.Color(255, 255, 255));
        test11CheckBox.setSelected(true);
        test11CheckBox.setText(resourceMap.getString("test11CheckBox.text")); // NOI18N
        test11CheckBox.setBorder(null);
        test11CheckBox.setBorderPaintedFlat(true);
        test11CheckBox.setName("test11CheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(test11CheckBox, gridBagConstraints);

        test12CheckBox.setBackground(new java.awt.Color(255, 255, 255));
        test12CheckBox.setSelected(true);
        test12CheckBox.setText(resourceMap.getString("test12CheckBox.text")); // NOI18N
        test12CheckBox.setBorder(null);
        test12CheckBox.setBorderPaintedFlat(true);
        test12CheckBox.setName("test12CheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(test12CheckBox, gridBagConstraints);

        test13CheckBox.setBackground(new java.awt.Color(255, 255, 255));
        test13CheckBox.setSelected(true);
        test13CheckBox.setText(resourceMap.getString("test13CheckBox.text")); // NOI18N
        test13CheckBox.setBorder(null);
        test13CheckBox.setBorderPaintedFlat(true);
        test13CheckBox.setName("test13CheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(test13CheckBox, gridBagConstraints);

        test14CheckBox.setBackground(new java.awt.Color(255, 255, 255));
        test14CheckBox.setSelected(true);
        test14CheckBox.setText(resourceMap.getString("test14CheckBox.text")); // NOI18N
        test14CheckBox.setBorder(null);
        test14CheckBox.setBorderPaintedFlat(true);
        test14CheckBox.setName("test14CheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(test14CheckBox, gridBagConstraints);

        test15CheckBox.setBackground(new java.awt.Color(255, 255, 255));
        test15CheckBox.setSelected(true);
        test15CheckBox.setText(resourceMap.getString("test15CheckBox.text")); // NOI18N
        test15CheckBox.setBorder(null);
        test15CheckBox.setBorderPaintedFlat(true);
        test15CheckBox.setName("test15CheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(test15CheckBox, gridBagConstraints);

        test16CheckBox.setBackground(new java.awt.Color(255, 255, 255));
        test16CheckBox.setSelected(true);
        test16CheckBox.setText(resourceMap.getString("test16CheckBox.text")); // NOI18N
        test16CheckBox.setBorder(null);
        test16CheckBox.setBorderPaintedFlat(true);
        test16CheckBox.setName("test16CheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(test16CheckBox, gridBagConstraints);

        test17CheckBox.setBackground(new java.awt.Color(255, 255, 255));
        test17CheckBox.setSelected(true);
        test17CheckBox.setText(resourceMap.getString("test17CheckBox.text")); // NOI18N
        test17CheckBox.setBorder(null);
        test17CheckBox.setBorderPaintedFlat(true);
        test17CheckBox.setName("test17CheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(test17CheckBox, gridBagConstraints);

        processButton1.setAction(actionMap.get("processDIEHARDInputFile")); // NOI18N
        processButton1.setBackground(new java.awt.Color(255, 255, 255));
        processButton1.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        processButton1.setName("processButton1"); // NOI18N
        processButton1.setPreferredSize(new java.awt.Dimension(100, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(processButton1, gridBagConstraints);

        diehardTestsDocumentationButton.setAction(actionMap.get("showDIEHARDTestsDocumentation")); // NOI18N
        diehardTestsDocumentationButton.setBackground(new java.awt.Color(255, 255, 255));
        diehardTestsDocumentationButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        diehardTestsDocumentationButton.setEnabled(Desktop.getDesktop().isSupported(Desktop.Action.OPEN));
        diehardTestsDocumentationButton.setName("diehardTestsDocumentationButton"); // NOI18N
        diehardTestsDocumentationButton.setPreferredSize(new java.awt.Dimension(100, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(diehardTestsDocumentationButton, gridBagConstraints);

        clearButton1.setAction(actionMap.get("clearOutput1")); // NOI18N
        clearButton1.setBackground(new java.awt.Color(255, 255, 255));
        clearButton1.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        clearButton1.setName("clearButton1"); // NOI18N
        clearButton1.setPreferredSize(new java.awt.Dimension(100, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel1.add(clearButton1, gridBagConstraints);

        diehardBatteryPanel.add(inputPanel1, java.awt.BorderLayout.NORTH);

        outputPanel1.setBackground(new java.awt.Color(255, 255, 255));
        outputPanel1.setName("outputPanel1"); // NOI18N
        outputPanel1.setLayout(new java.awt.BorderLayout());

        outputScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        outputScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        outputScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outputScrollPane1.setName("outputScrollPane1"); // NOI18N
        outputScrollPane1.setPreferredSize(new java.awt.Dimension(600, 240));

        outputTextArea1.setEditable(false);
        outputTextArea1.setFont(new java.awt.Font("Lucida Console", 0, 11));
        outputTextArea1.setLineWrap(true);
        outputTextArea1.setWrapStyleWord(true);
        outputTextArea1.setBorder(null);
        outputTextArea1.setName("outputTextArea1"); // NOI18N
        outputScrollPane1.setViewportView(outputTextArea1);

        outputPanel1.add(outputScrollPane1, java.awt.BorderLayout.CENTER);

        diehardBatteryPanel.add(outputPanel1, java.awt.BorderLayout.CENTER);

        mainPanel.add(diehardBatteryPanel, "card2");

        nistBatteryPanel.setBackground(new java.awt.Color(255, 255, 255));
        nistBatteryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("nistBatteryPanel.border.title"))); // NOI18N
        nistBatteryPanel.setName("nistBatteryPanel"); // NOI18N
        nistBatteryPanel.setLayout(new java.awt.BorderLayout());

        inputPanel2.setBackground(new java.awt.Color(255, 255, 255));
        inputPanel2.setName("inputPanel2"); // NOI18N
        inputPanel2.setLayout(new java.awt.GridBagLayout());

        inputFileLabel2.setBackground(new java.awt.Color(255, 255, 255));
        inputFileLabel2.setText(resourceMap.getString("inputFileLabel2.text")); // NOI18N
        inputFileLabel2.setName("inputFileLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(inputFileLabel2, gridBagConstraints);

        inputFileTextField2.setFont(new java.awt.Font("Lucida Console", 0, 11));
        inputFileTextField2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        inputFileTextField2.setMinimumSize(new java.awt.Dimension(300, 20));
        inputFileTextField2.setName("inputFileTextField2"); // NOI18N
        inputFileTextField2.setPreferredSize(new java.awt.Dimension(400, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(inputFileTextField2, gridBagConstraints);

        chooseInputFileButton2.setAction(actionMap.get("chooseInputFile2")); // NOI18N
        chooseInputFileButton2.setBackground(new java.awt.Color(255, 255, 255));
        chooseInputFileButton2.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        chooseInputFileButton2.setMinimumSize(new java.awt.Dimension(30, 23));
        chooseInputFileButton2.setName("chooseInputFileButton2"); // NOI18N
        chooseInputFileButton2.setPreferredSize(new java.awt.Dimension(30, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(chooseInputFileButton2, gridBagConstraints);

        bitStreamLengthLabel.setBackground(new java.awt.Color(255, 255, 255));
        bitStreamLengthLabel.setText(resourceMap.getString("bitStreamLengthLabel.text")); // NOI18N
        bitStreamLengthLabel.setName("bitStreamLengthLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(bitStreamLengthLabel, gridBagConstraints);

        bitStreamLengthTextField.setFont(new java.awt.Font("Lucida Console", 0, 11));
        bitStreamLengthTextField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        bitStreamLengthTextField.setMinimumSize(new java.awt.Dimension(300, 20));
        bitStreamLengthTextField.setName("bitStreamLengthTextField"); // NOI18N
        bitStreamLengthTextField.setPreferredSize(new java.awt.Dimension(400, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(bitStreamLengthTextField, gridBagConstraints);

        numberOfBitStreamsLabel.setText(resourceMap.getString("numberOfBitStreamsLabel.text")); // NOI18N
        numberOfBitStreamsLabel.setName("numberOfBitStreamsLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(numberOfBitStreamsLabel, gridBagConstraints);

        numberOfBitStreamsTextField.setFont(new java.awt.Font("Lucida Console", 0, 11));
        numberOfBitStreamsTextField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        numberOfBitStreamsTextField.setMinimumSize(new java.awt.Dimension(300, 20));
        numberOfBitStreamsTextField.setName("numberOfBitStreamsTextField"); // NOI18N
        numberOfBitStreamsTextField.setPreferredSize(new java.awt.Dimension(400, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(numberOfBitStreamsTextField, gridBagConstraints);

        asciiFileFormatRadioButton.setBackground(new java.awt.Color(255, 255, 255));
        fileFormatButtonGroup.add(asciiFileFormatRadioButton);
        asciiFileFormatRadioButton.setSelected(true);
        asciiFileFormatRadioButton.setText(resourceMap.getString("asciiFileFormatRadioButton.text")); // NOI18N
        asciiFileFormatRadioButton.setName("asciiFileFormatRadioButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(asciiFileFormatRadioButton, gridBagConstraints);

        binaryFileFormatRadioButton.setBackground(new java.awt.Color(255, 255, 255));
        fileFormatButtonGroup.add(binaryFileFormatRadioButton);
        binaryFileFormatRadioButton.setText(resourceMap.getString("binaryFileFormatRadioButton.text")); // NOI18N
        binaryFileFormatRadioButton.setName("binaryFileFormatRadioButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(binaryFileFormatRadioButton, gridBagConstraints);

        test1CheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        test1CheckBox1.setSelected(true);
        test1CheckBox1.setText(resourceMap.getString("test1CheckBox1.text")); // NOI18N
        test1CheckBox1.setBorder(null);
        test1CheckBox1.setBorderPaintedFlat(true);
        test1CheckBox1.setName("test1CheckBox1"); // NOI18N
        test1CheckBox1.setPreferredSize(new java.awt.Dimension(171, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(test1CheckBox1, gridBagConstraints);

        test2CheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        test2CheckBox1.setSelected(true);
        test2CheckBox1.setText(resourceMap.getString("test2CheckBox1.text")); // NOI18N
        test2CheckBox1.setBorder(null);
        test2CheckBox1.setBorderPaintedFlat(true);
        test2CheckBox1.setName("test2CheckBox1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(test2CheckBox1, gridBagConstraints);

        test3CheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        test3CheckBox1.setSelected(true);
        test3CheckBox1.setText(resourceMap.getString("test3CheckBox1.text")); // NOI18N
        test3CheckBox1.setBorder(null);
        test3CheckBox1.setBorderPaintedFlat(true);
        test3CheckBox1.setName("test3CheckBox1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(test3CheckBox1, gridBagConstraints);

        test4CheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        test4CheckBox1.setSelected(true);
        test4CheckBox1.setText(resourceMap.getString("test4CheckBox1.text")); // NOI18N
        test4CheckBox1.setBorder(null);
        test4CheckBox1.setBorderPaintedFlat(true);
        test4CheckBox1.setName("test4CheckBox1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(test4CheckBox1, gridBagConstraints);

        test5CheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        test5CheckBox1.setSelected(true);
        test5CheckBox1.setText(resourceMap.getString("test5CheckBox1.text")); // NOI18N
        test5CheckBox1.setBorder(null);
        test5CheckBox1.setBorderPaintedFlat(true);
        test5CheckBox1.setName("test5CheckBox1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(test5CheckBox1, gridBagConstraints);

        test6CheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        test6CheckBox1.setSelected(true);
        test6CheckBox1.setText(resourceMap.getString("test6CheckBox1.text")); // NOI18N
        test6CheckBox1.setBorder(null);
        test6CheckBox1.setBorderPaintedFlat(true);
        test6CheckBox1.setName("test6CheckBox1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(test6CheckBox1, gridBagConstraints);

        test7CheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        test7CheckBox1.setSelected(true);
        test7CheckBox1.setText(resourceMap.getString("test7CheckBox1.text")); // NOI18N
        test7CheckBox1.setBorder(null);
        test7CheckBox1.setBorderPaintedFlat(true);
        test7CheckBox1.setName("test7CheckBox1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(test7CheckBox1, gridBagConstraints);

        test8CheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        test8CheckBox1.setSelected(true);
        test8CheckBox1.setText(resourceMap.getString("test8CheckBox1.text")); // NOI18N
        test8CheckBox1.setBorder(null);
        test8CheckBox1.setBorderPaintedFlat(true);
        test8CheckBox1.setName("test8CheckBox1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(test8CheckBox1, gridBagConstraints);

        test9CheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        test9CheckBox1.setSelected(true);
        test9CheckBox1.setText(resourceMap.getString("test9CheckBox1.text")); // NOI18N
        test9CheckBox1.setBorder(null);
        test9CheckBox1.setBorderPaintedFlat(true);
        test9CheckBox1.setName("test9CheckBox1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(test9CheckBox1, gridBagConstraints);

        test10CheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        test10CheckBox1.setSelected(true);
        test10CheckBox1.setText(resourceMap.getString("test10CheckBox1.text")); // NOI18N
        test10CheckBox1.setBorder(null);
        test10CheckBox1.setBorderPaintedFlat(true);
        test10CheckBox1.setName("test10CheckBox1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(test10CheckBox1, gridBagConstraints);

        test11CheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        test11CheckBox1.setSelected(true);
        test11CheckBox1.setText(resourceMap.getString("test11CheckBox1.text")); // NOI18N
        test11CheckBox1.setBorder(null);
        test11CheckBox1.setBorderPaintedFlat(true);
        test11CheckBox1.setName("test11CheckBox1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(test11CheckBox1, gridBagConstraints);

        test12CheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        test12CheckBox1.setSelected(true);
        test12CheckBox1.setText(resourceMap.getString("test12CheckBox1.text")); // NOI18N
        test12CheckBox1.setBorder(null);
        test12CheckBox1.setBorderPaintedFlat(true);
        test12CheckBox1.setName("test12CheckBox1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(test12CheckBox1, gridBagConstraints);

        test13CheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        test13CheckBox1.setSelected(true);
        test13CheckBox1.setText(resourceMap.getString("test13CheckBox1.text")); // NOI18N
        test13CheckBox1.setBorder(null);
        test13CheckBox1.setBorderPaintedFlat(true);
        test13CheckBox1.setName("test13CheckBox1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(test13CheckBox1, gridBagConstraints);

        test14CheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        test14CheckBox1.setSelected(true);
        test14CheckBox1.setText(resourceMap.getString("test14CheckBox1.text")); // NOI18N
        test14CheckBox1.setBorder(null);
        test14CheckBox1.setBorderPaintedFlat(true);
        test14CheckBox1.setName("test14CheckBox1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(test14CheckBox1, gridBagConstraints);

        test15CheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        test15CheckBox1.setSelected(true);
        test15CheckBox1.setText(resourceMap.getString("test15CheckBox1.text")); // NOI18N
        test15CheckBox1.setBorder(null);
        test15CheckBox1.setBorderPaintedFlat(true);
        test15CheckBox1.setName("test15CheckBox1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(test15CheckBox1, gridBagConstraints);

        processButton2.setAction(actionMap.get("processNISTInputFile")); // NOI18N
        processButton2.setBackground(new java.awt.Color(255, 255, 255));
        processButton2.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        processButton2.setName("processButton2"); // NOI18N
        processButton2.setPreferredSize(new java.awt.Dimension(100, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(processButton2, gridBagConstraints);

        clearButton2.setAction(actionMap.get("clearOutput2")); // NOI18N
        clearButton2.setBackground(new java.awt.Color(255, 255, 255));
        clearButton2.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        clearButton2.setName("clearButton2"); // NOI18N
        clearButton2.setPreferredSize(new java.awt.Dimension(100, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(clearButton2, gridBagConstraints);

        nistTestsDocumentationButton.setAction(actionMap.get("showNISTTestsDocumentation")); // NOI18N
        nistTestsDocumentationButton.setBackground(new java.awt.Color(255, 255, 255));
        nistTestsDocumentationButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        nistTestsDocumentationButton.setEnabled(Desktop.getDesktop().isSupported(Desktop.Action.OPEN));
        nistTestsDocumentationButton.setName("nistTestsDocumentationButton"); // NOI18N
        nistTestsDocumentationButton.setPreferredSize(new java.awt.Dimension(100, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        inputPanel2.add(nistTestsDocumentationButton, gridBagConstraints);

        nistBatteryPanel.add(inputPanel2, java.awt.BorderLayout.NORTH);

        outputPanel2.setBackground(new java.awt.Color(255, 255, 255));
        outputPanel2.setName("outputPanel2"); // NOI18N
        outputPanel2.setLayout(new java.awt.BorderLayout());

        outputScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        outputScrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        outputScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outputScrollPane2.setName("outputScrollPane2"); // NOI18N
        outputScrollPane2.setPreferredSize(new java.awt.Dimension(600, 240));

        outputTextArea2.setEditable(false);
        outputTextArea2.setFont(new java.awt.Font("Lucida Console", 0, 11));
        outputTextArea2.setLineWrap(true);
        outputTextArea2.setWrapStyleWord(true);
        outputTextArea2.setBorder(null);
        outputTextArea2.setName("outputTextArea2"); // NOI18N
        outputScrollPane2.setViewportView(outputTextArea2);

        outputPanel2.add(outputScrollPane2, java.awt.BorderLayout.CENTER);

        nistBatteryPanel.add(outputPanel2, java.awt.BorderLayout.CENTER);

        mainPanel.add(nistBatteryPanel, "card3");

        menuBar.setBackground(new java.awt.Color(255, 255, 255));
        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setBackground(new java.awt.Color(255, 255, 255));
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setBackground(new java.awt.Color(255, 255, 255));
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        batteryMenu.setBackground(new java.awt.Color(255, 255, 255));
        batteryMenu.setText(resourceMap.getString("batteryMenu.text")); // NOI18N
        batteryMenu.setName("batteryMenu"); // NOI18N

        entBatteryMenuItem.setAction(actionMap.get("showENTBatteryPanel")); // NOI18N
        entBatteryMenuItem.setBackground(new java.awt.Color(255, 255, 255));
        batteryButtonsGroup.add(entBatteryMenuItem);
        entBatteryMenuItem.setSelected(true);
        entBatteryMenuItem.setName("entBatteryMenuItem"); // NOI18N
        batteryMenu.add(entBatteryMenuItem);

        diehardBatteryMenuItem.setAction(actionMap.get("showDIEHARDBatteryPanel")); // NOI18N
        diehardBatteryMenuItem.setBackground(new java.awt.Color(255, 255, 255));
        batteryButtonsGroup.add(diehardBatteryMenuItem);
        diehardBatteryMenuItem.setName("diehardBatteryMenuItem"); // NOI18N
        batteryMenu.add(diehardBatteryMenuItem);

        nistBatteryMenuItem.setAction(actionMap.get("showNISTBatteryPanel")); // NOI18N
        nistBatteryMenuItem.setBackground(new java.awt.Color(255, 255, 255));
        batteryButtonsGroup.add(nistBatteryMenuItem);
        nistBatteryMenuItem.setName("nistBatteryMenuItem"); // NOI18N
        batteryMenu.add(nistBatteryMenuItem);

        menuBar.add(batteryMenu);

        helpMenu.setBackground(new java.awt.Color(255, 255, 255));
        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setBackground(new java.awt.Color(255, 255, 255));
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setBackground(new java.awt.Color(255, 255, 255));
        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setBackground(new java.awt.Color(255, 255, 255));
        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 768, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 594, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setDialogTitle(resourceMap.getString("fileChooser.dialogTitle")); // NOI18N
        fileChooser.setMinimumSize(new java.awt.Dimension(491, 245));
        fileChooser.setName("fileChooser"); // NOI18N

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton asciiFileFormatRadioButton;
    private javax.swing.JCheckBox bOptionCheckBox;
    private javax.swing.ButtonGroup batteryButtonsGroup;
    private javax.swing.JRadioButton binaryFileFormatRadioButton;
    private javax.swing.JLabel bitStreamLengthLabel;
    private javax.swing.JTextField bitStreamLengthTextField;
    private javax.swing.JCheckBox cOptionCheckBox;
    private javax.swing.JButton chooseInputFileButton;
    private javax.swing.JButton chooseInputFileButton1;
    private javax.swing.JButton chooseInputFileButton2;
    private javax.swing.JButton clearButton;
    private javax.swing.JButton clearButton1;
    private javax.swing.JButton clearButton2;
    private javax.swing.JRadioButtonMenuItem diehardBatteryMenuItem;
    private javax.swing.JPanel diehardBatteryPanel;
    private javax.swing.JButton diehardTestsDocumentationButton;
    private javax.swing.JRadioButtonMenuItem entBatteryMenuItem;
    private javax.swing.JPanel entBatteryPanel;
    private javax.swing.JButton entTestsDocumentationButton;
    private javax.swing.JCheckBox fOptionCheckBox;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.ButtonGroup fileFormatButtonGroup;
    private javax.swing.JLabel inputFileLabel;
    private javax.swing.JLabel inputFileLabel1;
    private javax.swing.JLabel inputFileLabel2;
    private javax.swing.JTextField inputFileTextField;
    private javax.swing.JTextField inputFileTextField1;
    private javax.swing.JTextField inputFileTextField2;
    private javax.swing.JPanel inputPanel;
    private javax.swing.JPanel inputPanel1;
    private javax.swing.JPanel inputPanel2;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JRadioButtonMenuItem nistBatteryMenuItem;
    private javax.swing.JPanel nistBatteryPanel;
    private javax.swing.JButton nistTestsDocumentationButton;
    private javax.swing.JLabel numberOfBitStreamsLabel;
    private javax.swing.JTextField numberOfBitStreamsTextField;
    private javax.swing.JPanel outputPanel;
    private javax.swing.JPanel outputPanel1;
    private javax.swing.JPanel outputPanel2;
    private javax.swing.JScrollPane outputScrollPane;
    private javax.swing.JScrollPane outputScrollPane1;
    private javax.swing.JScrollPane outputScrollPane2;
    private javax.swing.JTextArea outputTextArea;
    private javax.swing.JTextArea outputTextArea1;
    private javax.swing.JTextArea outputTextArea2;
    private javax.swing.JButton processButton;
    private javax.swing.JButton processButton1;
    private javax.swing.JButton processButton2;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JCheckBox tOptionCheckBox;
    private javax.swing.JCheckBox test10CheckBox;
    private javax.swing.JCheckBox test10CheckBox1;
    private javax.swing.JCheckBox test11CheckBox;
    private javax.swing.JCheckBox test11CheckBox1;
    private javax.swing.JCheckBox test12CheckBox;
    private javax.swing.JCheckBox test12CheckBox1;
    private javax.swing.JCheckBox test13CheckBox;
    private javax.swing.JCheckBox test13CheckBox1;
    private javax.swing.JCheckBox test14CheckBox;
    private javax.swing.JCheckBox test14CheckBox1;
    private javax.swing.JCheckBox test15CheckBox;
    private javax.swing.JCheckBox test15CheckBox1;
    private javax.swing.JCheckBox test16CheckBox;
    private javax.swing.JCheckBox test17CheckBox;
    private javax.swing.JCheckBox test1CheckBox;
    private javax.swing.JCheckBox test1CheckBox1;
    private javax.swing.JCheckBox test2CheckBox;
    private javax.swing.JCheckBox test2CheckBox1;
    private javax.swing.JCheckBox test3CheckBox;
    private javax.swing.JCheckBox test3CheckBox1;
    private javax.swing.JCheckBox test4CheckBox;
    private javax.swing.JCheckBox test4CheckBox1;
    private javax.swing.JCheckBox test5CheckBox;
    private javax.swing.JCheckBox test5CheckBox1;
    private javax.swing.JCheckBox test6CheckBox;
    private javax.swing.JCheckBox test6CheckBox1;
    private javax.swing.JCheckBox test7CheckBox;
    private javax.swing.JCheckBox test7CheckBox1;
    private javax.swing.JCheckBox test8CheckBox;
    private javax.swing.JCheckBox test8CheckBox1;
    private javax.swing.JCheckBox test9CheckBox;
    private javax.swing.JCheckBox test9CheckBox1;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
}
