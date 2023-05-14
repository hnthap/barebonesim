package com.example.barebonesim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class CustomWindow extends JFrame implements ActionListener{
    
    public static final boolean RIGHT_TO_LEFT = false;
    public static final int DEFAULT_TIMEOUT_MILISECONDS = 1000;
    public static final String DEFAULT_PROPERTIES_FILE_PATH = 
        "src/main/resources/barebonesim.properties";

    private BorderedLabel inputLabel;
    private JTextArea textArea;
    private BorderedLabel outputLabel;
    private JLabel currentFilePathLabel;
    private CustomButton runButton1;
    private CustomButton runButton2;

    private Optional<String> latestChooserFilePath = Optional.empty();
    private Optional<String> currentFileAbsolutePath = Optional.empty();
    private int timeoutMiliseconds = DEFAULT_TIMEOUT_MILISECONDS;

    private Properties props = new Properties();

    public CustomWindow() {

        setTitle("Barebonesim");
        setMinimumSize(new Dimension(640, 480));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addComponentsToPane(getContentPane());
        
        loadProperties();

        if (currentFileAbsolutePath.isEmpty()) {
            addAndMoveToUntitled();
        } else {
            addAndMoveToCurrent();
        }

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                exit();
                e.getWindow().dispose();
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addComponentsToPane(Container pane) {
        
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }

        if (RIGHT_TO_LEFT) {
            pane.setComponentOrientation(
                java.awt.ComponentOrientation.RIGHT_TO_LEFT);
        }

        pane.add(new CustomMenuBar(this), BorderLayout.PAGE_START);

        inputLabel = new BorderedLabel(Compiler.getInput().toHtml());
        CustomGreaterPanel greaterInputPanel = 
            new CustomGreaterPanel("INPUT", inputLabel);
        JPanel inputSmallButtonPanel = new JPanel();
        inputSmallButtonPanel.setLayout(new BoxLayout(
            inputSmallButtonPanel, BoxLayout.LINE_AXIS));
        inputSmallButtonPanel.add(new SimpleButton(
            "[＋]", this, "Add Input"));
        inputSmallButtonPanel.add(Box.createHorizontalGlue());
        inputSmallButtonPanel.add(new SimpleButton(
            "[✎]", this, "Change Input"));
        inputSmallButtonPanel.add(Box.createHorizontalGlue());
        inputSmallButtonPanel.add(new SimpleButton(
            "[－]", this, "Remove Input"));
        greaterInputPanel.add(inputSmallButtonPanel);
        greaterInputPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        greaterInputPanel.add(new ColoredButton("From CSV", this, 
            "Get Input From CSV"));
        greaterInputPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        greaterInputPanel.add(new ColoredButton("From Result", this, 
            "Get Input From Output"));
        greaterInputPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        pane.add(greaterInputPanel, BorderLayout.LINE_START);

        JPanel textAreaPanel = new JPanel();
        textAreaPanel.setLayout(new BoxLayout(textAreaPanel, 
            BoxLayout.PAGE_AXIS));
        JPanel textAreaButtonPanel = new JPanel();
        textAreaButtonPanel.setLayout(new BoxLayout(textAreaButtonPanel, 
            BoxLayout.LINE_AXIS));
        
        textAreaButtonPanel.add(Box.createHorizontalGlue());
        runButton1 = new SimpleButton(" ▶ ", this, "Run");
        textAreaButtonPanel.add(runButton1);
        textAreaButtonPanel.add(Box.createHorizontalGlue());
        textAreaButtonPanel.add(new SimpleButton(" 📂 ", this, 
            "Open File"));
        textAreaButtonPanel.add(Box.createHorizontalGlue());
        textAreaButtonPanel.add(new SimpleButton(" 💾 ", this, 
            "Save"));
        textAreaButtonPanel.add(Box.createHorizontalGlue());
        textAreaButtonPanel.add(new SimpleButton(" ❀ ", this, 
            "Beautify"));
        textAreaButtonPanel.add(Box.createHorizontalGlue());
        textAreaButtonPanel.add(new SimpleButton(" ☕ ", this, 
            "Snippets"));
        textAreaButtonPanel.add(Box.createHorizontalGlue()) ;
        textAreaButtonPanel.add(new SimpleButton(" ✖ ", this, 
            "Close File"));
        textAreaButtonPanel.add(Box.createHorizontalGlue());
        textAreaPanel.add(textAreaButtonPanel);
        textArea = new JTextArea();
        textArea.setText("");
        textArea.setFont(textArea.getFont().deriveFont(18.0f));
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane editorScrollPane = new JScrollPane(textArea,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        editorScrollPane.setPreferredSize(new Dimension(480, 480));
        textAreaPanel.add(editorScrollPane);
        pane.add(textAreaPanel, BorderLayout.CENTER);
        
        outputLabel = new BorderedLabel("");
        CustomGreaterPanel greaterOutputPanel = 
            new CustomGreaterPanel("OUTPUT", outputLabel);
        JPanel outputSmallButtonPanel = new JPanel();
        outputSmallButtonPanel.setLayout(new BoxLayout(
            outputSmallButtonPanel, BoxLayout.LINE_AXIS));
        runButton2 = new SimpleButton("[▶]", this, "Run");
        outputSmallButtonPanel.add(runButton2);
        outputSmallButtonPanel.add(Box.createHorizontalGlue());
        outputSmallButtonPanel.add(new SimpleButton("[⟳]", this, "Reload"));
        greaterOutputPanel.add(outputSmallButtonPanel);
        greaterOutputPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        greaterOutputPanel.add(new ColoredButton("Extract to CSV", this, 
            "Extract to CSV"));
        greaterOutputPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        greaterOutputPanel.add(new ColoredButton("Reload", this, "Reload"));
        greaterOutputPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        pane.add(greaterOutputPanel, BorderLayout.LINE_END);

        currentFilePathLabel = new JLabel("Current File Path");
        currentFilePathLabel.setFont(
            currentFilePathLabel.getFont().deriveFont(14.0f));
        currentFilePathLabel.setHorizontalAlignment(SwingConstants.LEFT);
        currentFilePathLabel.setVerticalAlignment(SwingConstants.CENTER);
        currentFilePathLabel.setPreferredSize(new Dimension(840, 30));
        currentFilePathLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        pane.add(currentFilePathLabel, BorderLayout.PAGE_END);

    }

    private void addAndMoveToUntitled() {
        currentFilePathLabel.setText("(Unsaved)");
        textArea.setText("");
        outputLabel.setText("");
    }

    private void addAndMoveToCurrent() {
        addAndMoveToFile(currentFileAbsolutePath.get());
    }

    private void addAndMoveToFile(final String absPath) {
        try (BufferedReader reader = new BufferedReader(
                new FileReader(new File(absPath)))
        ) {
            String text = reader.readLine();
            String temp;
            while ((temp = reader.readLine()) != null) {
                text += "\n" + temp;
            }
            textArea.setText(text);

            currentFilePathLabel.setText("Path: " + absPath);
            currentFileAbsolutePath = Optional.of(absPath);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    /**
     * - timeout in miliseconds
     * - current file path
     * - latest chooser file path
     * - input HTML
     * - output HTML
     */
    private void loadProperties() {
        try (FileInputStream input = new FileInputStream(
                DEFAULT_PROPERTIES_FILE_PATH)
        ) {
            props.load(input);
        } catch (FileNotFoundException e) {
            File file = new File(DEFAULT_PROPERTIES_FILE_PATH);
            try {
                file.createNewFile();
            } catch (IOException e1) {
                return ;
            }
        } catch (IOException e) {
            return ;
        }

        try {
            timeoutMiliseconds = Integer.parseInt(
                props.getProperty("timeout_msec"));
        } catch (NumberFormatException e) {
            timeoutMiliseconds = DEFAULT_TIMEOUT_MILISECONDS;
        }
        if (props.containsKey("current_file_path")) {
            currentFileAbsolutePath = Optional.of(
                props.getProperty("current_file_path"));
        }
        if (props.containsKey("latest_chooser_file_path")) {
            latestChooserFilePath = Optional.of(
                props.getProperty("latest_chooser_file_path"));
        }

    }

    private void savePropreties() {
        props.setProperty("timeout_msec",
            Integer.toString(timeoutMiliseconds));
        if (currentFileAbsolutePath.isPresent()) {
            props.setProperty("current_file_path",
                currentFileAbsolutePath.get());
        }
        if (latestChooserFilePath.isPresent()) {
            props.setProperty("latest_chooser_file_path",
                latestChooserFilePath.get());
        }

        try (OutputStream output =
                new FileOutputStream(DEFAULT_PROPERTIES_FILE_PATH)
        ) {
            props.store(output, null);
        } catch (IOException e) {
            // Ignore the exception
        }

        try (OutputStream output =
                new FileOutputStream("barebonesim.properties")
        ) {
            props.store(output, null);
        } catch (IOException e) {
            CustomDialog.STORE_PROP_FAILED.show(this, e);
            return;
        }
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        String s = arg0.getActionCommand();
        
        switch (s) {
        case "Add Input":
            addInput();
            break;

        case "Beautify":
            beautify();
            break;

        case "Change Input":
            changeInput();
            break;

        case "Close File":
            closeFile();
            addAndMoveToUntitled();
            currentFileAbsolutePath = Optional.empty();
            break;

        case "Copy":
            textArea.copy();
            break;

        case "Cut":
            textArea.cut();
            break;

        case "Exit":
            exit();
            break;

        case "Extract to CSV":
            extractToCsv();
            break;

        case "Get Input From CSV":
            getInputFromCsv();
            break;

        case "Get Input From Output":
            getInputFromOutput();
            break;

        case "New File":
            createAndOpenNewFile();
            break;

        case "Open File":
            openFile();
            break;

        case "Paste":
            textArea.paste();
            break;

        case "Remove Input":
            removeInput();
            break;

        case "Reload":
        case "Run":
            runButton1.setText(" ↻ ");
            runButton2.setText("[↻]");
            run();
            runButton1.setText(" ▶ ");
            runButton2.setText("[▶]");
            break;

        case "Save":
            saveFile();
            break;

        case "Save As":
            saveFileAs();
            break;

        case "Set Time Limit":
            setTimeLimit();
            break;

        case "Set Time Limit As Default":
            setTimeLimitAsDefault();
            break;

        case "Snippets":
            showSnippets();
            break;

        default: 
            CustomDialog.UNDER_CONSTRUCTION.show(this, s);
        }
    }

    private void createAndOpenNewFile() {
        CustomFileChooser chooser =
            new CustomFileChooser(latestChooserFilePath);
        int returnValue = chooser.showOpenDialog(this);
        latestChooserFilePath = chooser.getDirectoryAsOptional();
        if (returnValue != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (chooser.getDialogType() == JFileChooser.OPEN_DIALOG) {
            if (file.exists()) {
                returnValue = (int) CustomDialog
                    .OVERWRITE_EXISTING_FILE.show(chooser, null);
                switch (returnValue) {
                case JOptionPane.YES_OPTION:
                    chooser.approveSelection();
                    break;
                case JOptionPane.NO_OPTION:
                case JOptionPane.CLOSED_OPTION:
                    return;
                case JOptionPane.CANCEL_OPTION:
                    chooser.cancelSelection();
                    break;
                }
            } else {
                chooser.approveSelection();
            }
        }
        textArea.setText("");
        currentFilePathLabel.setText("Path: " + file.getAbsolutePath());
        currentFileAbsolutePath = Optional.of(file.getAbsolutePath());

        Compiler.getInput().clear();
    }

    private void openFile() {
        CustomFileChooser chooser =
            new CustomFileChooser(latestChooserFilePath);
        int returnValue = chooser.showOpenDialog(this);
        latestChooserFilePath = chooser.getDirectoryAsOptional();
        if (returnValue != JFileChooser.APPROVE_OPTION) {
            return;
        }

        addAndMoveToFile(chooser.getSelectedFile().getAbsolutePath());

        Compiler.getInput().clear();
    }

    private void saveFile() {
        if (currentFileAbsolutePath.isEmpty()) {
            saveFileAs();
        } else {
            File file = new File(currentFileAbsolutePath.get());
            try {
                FileWriter fwriter = new FileWriter(file, false);
                BufferedWriter writer = new BufferedWriter(fwriter);
                writer.write(textArea.getText());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(rootPane, e.getMessage());
            }
        }
    }

    private void saveFileAs() {
        CustomFileChooser chooser = new CustomFileChooser(
            latestChooserFilePath, "f:");
        int returnValue = chooser.showOpenDialog(this);
        latestChooserFilePath = chooser.getDirectoryAsOptional();
        if (returnValue != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = new File(chooser.getSelectedFile().getAbsolutePath());
        try {
            FileWriter fwriter = new FileWriter(file, false);
            BufferedWriter writer = new BufferedWriter(fwriter);
            writer.write(textArea.getText());
            writer.flush();
            writer.close();

            currentFileAbsolutePath = Optional.of(file.getAbsolutePath());
            currentFilePathLabel.setText("Path: " + file.getAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(chooser, e.getMessage());
        }
    }

    private void closeFile() {
        if (isUnsaved()) {
            if (textArea.getText().isEmpty() && 
                    currentFileAbsolutePath.isEmpty()
            ) {    
                return ;
            }
            int returnValue = (int) CustomDialog
                .CLOSE_UNSAVED_FILE.show(this, null);
            if (returnValue == JOptionPane.NO_OPTION ||
                returnValue == JOptionPane.CLOSED_OPTION) {
                currentFilePathLabel.setText(
                    currentFilePathLabel.getText() + " (Unsaved)");
                return;
            }
            
            assert returnValue == JOptionPane.YES_OPTION;
            saveFile();
        }
    }

    /***
     * Check if the code in textArea is unsaved.
     * @return true if the code is unsaved, otherwise false
     */
    private boolean isUnsaved() {
        if (currentFileAbsolutePath.isEmpty()) {
            return true;
        }
        if (!new File(currentFileAbsolutePath.get()).exists()) {
            return true;
        }
        String absPath = currentFileAbsolutePath.get();
        File file = new File(absPath);
        String text;
        try {
            FileReader freader = new FileReader(file);
            BufferedReader reader = new BufferedReader(freader);
            text = reader.readLine();
            String temp;
            while ((temp = reader.readLine()) != null) {
                text += "\n" + temp;
            }
            reader.close();
        } catch (IOException e) {
            return false;
        }
        if (text == null) {
            return true;
        }
        return !text.equals(textArea.getText());
    }

    private void addInput() {

        // Display dialog

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JLabel label = new JLabel("Variable Name");
        label.setFont(label.getFont().deriveFont(14.0f));
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        JTextField varName = new JTextField(8);
        varName.setFont(varName.getFont().deriveFont(14.0f));
        panel.add(varName);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        label = new JLabel();
        label.setText(
            "<html>Variable names MUST follow these rules<br/>" +
            "1. A name cannot be empty.<br/>" +
            "2. A name starts with an character from A to Z,<br/>" +
            "&nbsp;&nbsp;&nbsp;(uppercase or lowercase), " + 
            "or an underscore.<br/>" +
            "3. A name contains only characters from A to Z<br/>" +
            "&nbsp;&nbsp;&nbsp;(uppercase or lowercase), or digits, " + 
            "or underscore.<br/>" +
            "4. If a name changes to lowercase, it will not be<br/>" +
            "&nbsp;&nbsp;&nbsp;the same as any keyword.<br/>" + 
            "These are all keywords: clear, decr, do, end,<br/>" +
            "&nbsp;&nbsp;incr, not, while.<br/>" +
            "A name MUST have a length less than 32 characters.</html>");
        label.setFont(label.getFont().deriveFont(12.0f));
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        label = new JLabel("Initial Value");
        label.setFont(label.getFont().deriveFont(14.0f));
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        JTextField varValue = new JTextField(8);
        varValue.setFont(varValue.getFont().deriveFont(14.0f));
        panel.add(varValue);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        label = new JLabel();
        label.setText(
            "<html>Each variable can only hold an " +
            "<emph>non-negative integer</emph> value.</html>");
        label.setFont(label.getFont().deriveFont(12.0f));
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        int result = JOptionPane.showConfirmDialog(
            null, panel, "Add New Input",
            JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        } 

        varName.setText(varName.getText().trim());
        varValue.setText(varValue.getText().trim());

        // Check if the information provided is valid

        if (Compiler.getInput().getValue(varName.getText()).isPresent()) {
            CustomDialog.EXISTING_VAR_NAME.show(this, varName.getText());
            return;
        }
        if (!VariableContainer.isValidVarName(varName.getText())) {
            CustomDialog.INVALID_VAR_NAME.show(this, varName.getText());
            return;
        }
        int value;
        try {
            value = Integer.parseInt(varValue.getText());
            if (value < 0) {
                CustomDialog.NEGATIVE_VALUE.show(this, null);
                return;
            }
        } catch (NumberFormatException e) {
            CustomDialog.NON_INTEGER_VALUE.show(this, null);
            return;
        }

        // Add the input

        Compiler.getInput().setValue(varName.getText(), value);

        // Update the input panel

        inputLabel.setText(Compiler.getInput().toHtml());

    }

    private void changeInput() {

        if (Compiler.getInput().isEmpty()) {
            CustomDialog.NO_INPUT.show(this, null);
            return;
        }

        // Display dialog

        Object[] varList = Compiler.getInput().getVarList();
        Object result = JOptionPane.showInputDialog(null,
            "Choose the variable to be changed", "Change Input",
            JOptionPane.PLAIN_MESSAGE,
            null, varList, varList[0]);
        if (result == null) {
            return ;
        }
        String varName = (String)result;
        int value;
        while (true) {
            String valueStr = (String)JOptionPane.showInputDialog(null,
                "Enter new value of " + varName, "Change Input",
                JOptionPane.PLAIN_MESSAGE,
                null, null, 0);
            try {
                value = Integer.parseInt(valueStr);
            } catch (NumberFormatException e) {
                CustomDialog.NON_INTEGER_VALUE.show(this, null);
                continue;
            }
            break;
        }

        // Check if the value is valid

        if (value < 0) {
            CustomDialog.NEGATIVE_VALUE.show(this, null);
            return ;
        }

        // Add the input

        Compiler.getInput().setValue(varName, value);

        // Update the input panel

        inputLabel.setText(Compiler.getInput().toHtml());

    }

    private void removeInput() {

        if (Compiler.getInput().isEmpty()) {
            CustomDialog.NO_INPUT.show(this, null);
            return;
        }

        // Display dialog

        Object[] varList = Compiler.getInput().getVarList();
        String varName = (String)JOptionPane.showInputDialog(null,
            "Choose the variable to be removed", "Remove Input",
            JOptionPane.PLAIN_MESSAGE,
            null, varList, varList[0]);        

        // Update the runner

        Compiler.getInput().removeVar(varName);

        // Update the input label

        inputLabel.setText(Compiler.getInput().toHtml());

    }

    private void beautify() {
        String newCode = Compiler.beautify(textArea.getText());
        if (newCode == null) {
            return ;
        }
        textArea.setText(newCode);
    }

    private void extractToCsv() {
        CustomFileChooser chooser =
            new CustomFileChooser(latestChooserFilePath);
        int result = chooser.showOpenDialog(this);
        latestChooserFilePath = chooser.getDirectoryAsOptional();
        if (result != JFileChooser.APPROVE_OPTION) {
            return ;
        }

        File file = chooser.getSelectedFile();
        try {
            Compiler.getOutput().writeToCsv(file);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                e.getMessage(), e.getClass().getSimpleName(),
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void getInputFromCsv() {

        CustomFileChooser chooser =
            new CustomFileChooser(latestChooserFilePath);
        int returnValue = chooser.showOpenDialog(this);
        latestChooserFilePath = chooser.getDirectoryAsOptional();
        if (returnValue != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();

        try {
            
            Compiler.getInput().readFromCsv(file);

        } catch (InvalidVariableNameException | IOException e) {
            JOptionPane.showMessageDialog(chooser, 
                e.getMessage(), 
                e.getClass().getSimpleName(), 
                JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            CustomDialog.INVALID_CSV.show(chooser, null);
        }

        inputLabel.setText(Compiler.getInput().toHtml());

    }

    private void getInputFromOutput() {
        Compiler.setInputAsOutput();
        inputLabel.setText(Compiler.getInput().toHtml());
    }

    private void run() {
        beautify();
        if (Compiler.compile(timeoutMiliseconds)) {
            outputLabel.setText(Compiler.getOutput().toHtml());
        } else {
            CustomDialog.TIME_LIMIT_EXCEEDED.show(this, timeoutMiliseconds);
        }
    }

    private void setTimeLimit() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setAlignmentX(CENTER_ALIGNMENT);
        JLabel msgLabel = new JLabel("Time Limit (in miliseconds):");
        msgLabel.setFont(msgLabel.getFont().deriveFont(14.0f));
        panel.add(msgLabel);
        panel.add(Box.createRigidArea(new Dimension(5, 5)));
        JTextField inputField = new JTextField(8);
        inputField.setFont(inputField.getFont().deriveFont(14.0f));
        panel.add(inputField);

        int result = JOptionPane.showConfirmDialog(this,
            panel, "Set Time Limit",
            JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return ;
        }

        int value;
        try {
            value = Integer.parseInt(inputField.getText());
        } catch (NumberFormatException e) {
            CustomDialog.NON_INTEGER_VALUE.show(this, null);
            return;
        }

        timeoutMiliseconds = value;

    }

    private void setTimeLimitAsDefault() {

        if (timeoutMiliseconds == DEFAULT_TIMEOUT_MILISECONDS) {
            JOptionPane.showMessageDialog(this,
                "The time limit is set as " + DEFAULT_TIMEOUT_MILISECONDS +
                " which is default.", "Nothing to change",
                JOptionPane.PLAIN_MESSAGE);
            return ;
        }
        
        int result = JOptionPane.showConfirmDialog(this,
            "The default time limit is " + DEFAULT_TIMEOUT_MILISECONDS +
            " ms.\n"
            + "Current time limit is " + timeoutMiliseconds + " ms.\n"
            + "Do you want to set the time limit as default?" ,
            "Set Time Limit As Default",
            JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) {
            return ;
        }
        
        timeoutMiliseconds = DEFAULT_TIMEOUT_MILISECONDS;

    }

    private void showSnippets() {

        JPanel panel;
        JLabel label;

        // Create a dialog for choosing a snippet

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        label = new JLabel(
            "<html>Choose a snippet<br/>&nbsp;</html>");
        label.setFont(label.getFont().deriveFont(14.0f));
        label.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(label);
        String[] options = {
            "Y := X",
            "Z := X + Y",
            "Z := X - Y",
            "Z := X * Y",
            "Z := floor(X / Y)",
            "if X != 0 then X := 0 else X := 1",
            "if X < Y then Z := 0 else Z := 1",
            "if X >= Y then Z := 0 else Z := 1",
            "if X > Y then Z := 0 else Z := 1",
            "if X <= Y then Z := 0 else Z := 1",
            "if X = Y then Z := 0 else Z := 1",
            "if X != Y then Z := 0 else Z := 1",
            "X != -X" };
        JComboBox<String> comboBox = new JComboBox<String>(options);
        comboBox.setFont(new Font("Courier New", Font.PLAIN, 14));
        panel.add(comboBox);
        if (JOptionPane.showConfirmDialog(
                this, panel, "Snippets",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION
        ) {
            return;
        }

        // Create a dialog to rename variables

        String selected = comboBox.getSelectedItem().toString();
        CodeSnippet snippet = CodeSnippet.ofName(selected);
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        label = new JLabel(String.format(
            "✎ %s", selected));
        label.setFont(label.getFont().deriveFont(18.0f));
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        SnippetVarType[] roles = snippet.getRoles();
        String[] defaultVarNames = snippet.getDefaultVarNames();
        JTextField[] varFields = new JTextField[roles.length];
        for (int i = 0; i < roles.length; ++i) {
            label = new JLabel();
            label.setText(snippet.getLabelTextForVarByIndex(i));
            label.setFont(label.getFont().deriveFont(14.0f));
            panel.add(label);
            varFields[i] = new JTextField(8);
            varFields[i].setFont(varFields[i].getFont().deriveFont(14.0f));
            varFields[i].setText(defaultVarNames[i]);
            panel.add(varFields[i]);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        if (JOptionPane.showConfirmDialog(
                null, panel, "Snippet Settings",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION
        ) {
            return;
        }
        String[] newVarNames = new String[roles.length];
        for (int i = 0; i < newVarNames.length; ++i) {
            newVarNames[i] = "" + varFields[i].getText();
        }

        // Check if variables' new names are valid
        // All of these MUST be different

        for (int i = 0; i < newVarNames.length; ++i) {
            if (!VariableContainer.isValidVarName(newVarNames[i])) {
                CustomDialog.INVALID_VAR_NAME.show(this, newVarNames[i]);
                return;
            }
            for (int j = i + 1; j < newVarNames.length; ++j) {
                if (newVarNames[i].equals(newVarNames[j])) {
                    JOptionPane.showMessageDialog(
                        this, "There are at least two variables sharing " +
                        "the same name", "Invalid Name",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        }

        // Display the snippet

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        label = new JLabel("Copy the code below");
        label.setFont(label.getFont().deriveFont(14.0f));
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        JTextArea contentArea = new JTextArea();
        contentArea.setEditable(false);
        contentArea.setText(snippet.getBareBonesCode(newVarNames));
        contentArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(contentArea,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(240, 360));
        panel.add(scrollPane);
        JOptionPane.showConfirmDialog(this, panel, "Here You Have",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    }

    private void exit() {
        closeFile();
        savePropreties();
        setVisible(false);
        System.exit(0);
    }

}
