package com.lx862.pwgui.gui.panel.editing.filetype.content;

import com.lx862.pwgui.executable.Executables;
import com.lx862.pwgui.gui.dialog.NumericSelectionDialog;
import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.gui.components.DocumentChangedListener;
import com.lx862.pwgui.data.model.file.ContentDirectoryModel;
import com.lx862.pwgui.executable.ProgramExecution;
import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.gui.components.kui.KGridBagLayoutPanel;
import com.lx862.pwgui.gui.components.kui.KTextField;
import com.lx862.pwgui.gui.dialog.ExecutableProgressDialog;
import com.lx862.pwgui.gui.panel.editing.filetype.FileEntryPaneContext;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ModrinthPanel extends JPanel {
    public ModrinthPanel(FileEntryPaneContext context, ContentDirectoryModel fileEntry) {
        setLayout(new BorderLayout());

        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.PAGE_AXIS));

        KGridBagLayoutPanel formPanel = new KGridBagLayoutPanel(3, 2);
        formPanel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel descriptionLabel = new JLabel("You can add file to packwiz with projects from Modrinth");
        descriptionLabel.setAlignmentX(LEFT_ALIGNMENT);
        formPanel.addRow(2, descriptionLabel);

        KTextField contentTextField = new KTextField("Cloth Config API");
        contentTextField.addActionListener(actionEvent -> {
            if(!contentTextField.getText().isEmpty()) addProject(contentTextField.getText());
        });
        formPanel.addRow(1, new JLabel("URL/Search Term: "), contentTextField);

        KButton addButton = new KButton("Add Project");
        addButton.addActionListener(actionEvent -> addProject(contentTextField.getText()));
        addButton.setMnemonic(KeyEvent.VK_A);
        addButton.setAlignmentX(LEFT_ALIGNMENT);
        formPanel.addRow(2, addButton);

        contentTextField.getDocument().addDocumentListener(new DocumentChangedListener(() -> updateAddProjectButtonState(addButton, contentTextField)));

        updateAddProjectButtonState(addButton, contentTextField);

        rootPanel.add(formPanel);
        add(rootPanel, BorderLayout.CENTER);
    }

    private void addProject(String content) {
        ProgramExecution programExecution = Executables.packwiz.buildCommand("modrinth", "add", content);
        ExecutableProgressDialog dialog = new ExecutableProgressDialog((Window)getTopLevelAncestor(), "Adding mod...", Constants.REASON_TRIGGERED_BY_USER, programExecution);

        List<String> recordedOutputs = new ArrayList<>();

        AtomicReference<String> modName = new AtomicReference<>(null);
        AtomicBoolean recordOutput = new AtomicBoolean();
        programExecution.whenStdout((line) -> {
            if(line.startsWith("Searching Modrinth...") || line.startsWith("Dependencies found:")) {
                recordedOutputs.clear();
                recordOutput.set(true);
                return;
            }

            if(line.startsWith("0) Cancel")) { // We have a GUI cancel button, we don't need this entry
                return;
            }

            if(line.startsWith("Choose a number:")) {
                recordOutput.set(false);
                new NumericSelectionDialog(dialog, "Select Mod", recordedOutputs, (selectIdx) -> {
                    if(selectIdx == -1) {
                        programExecution.enterInput("0");
                    } else {
                        programExecution.enterInput(String.valueOf(selectIdx + 1));
                    }
                }).setVisible(true);
            }

            // Dependencies
            if(line.endsWith("Would you like to add them? [Y/n]: ")) {
                String depList = recordedOutputs.stream().map(e -> "• " + e).collect(Collectors.joining("\n"));
                if(JOptionPane.showConfirmDialog(dialog, String.format("The following dependencies are required:\n%s\nDo you want to add them?", depList), Util.withTitlePrefix("Add Dependencies?"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    programExecution.enterInput("Y");
                } else {
                    programExecution.enterInput("N");
                }
            }

            // Mod name
            if(line.contains("Project \"") && line.contains("\" successfully added!")) {
                String croppedModName = line.split("Project ")[1].split(" successfully added!")[0];
                String unquotedModName = croppedModName.substring(1, croppedModName.length()-1);
                modName.set(unquotedModName);
            }

            // Record log
            if(recordOutput.get()) {
                boolean isNumericChoice = line.contains(") ");
                String processedLine =  isNumericChoice ? line.substring(line.indexOf(") ")+2) : line;
                recordedOutputs.add(processedLine);
            }
        });

        programExecution.whenExit((exitCode) -> {
            if(exitCode == 0) {
                JOptionPane.showMessageDialog(getTopLevelAncestor(), String.format("%s has been added to the modpack!", modName.get()), Util.withTitlePrefix("Project Added!"), JOptionPane.INFORMATION_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    private void updateAddProjectButtonState(JButton addButton, KTextField urlTextField) {
        boolean shouldEnable = !urlTextField.getText().isEmpty();

        addButton.setEnabled(shouldEnable);
    }
}
