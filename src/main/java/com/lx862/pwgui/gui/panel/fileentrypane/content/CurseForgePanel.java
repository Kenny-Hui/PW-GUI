package com.lx862.pwgui.gui.panel.fileentrypane.content;

import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.gui.base.DocumentChangedListener;
import com.lx862.pwgui.data.ManualModInfo;
import com.lx862.pwgui.data.fileentry.ContentManagementFolderFileEntry;
import com.lx862.pwgui.executable.ProgramExecution;
import com.lx862.pwgui.gui.base.kui.KButton;
import com.lx862.pwgui.gui.base.kui.KGridBagLayoutPanel;
import com.lx862.pwgui.gui.base.kui.KTextField;
import com.lx862.pwgui.gui.dialog.ExecutableProgressDialog;
import com.lx862.pwgui.gui.dialog.ModSelectionDialog;
import com.lx862.pwgui.gui.panel.fileentrypane.FileEntryPaneContext;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class CurseForgePanel extends JPanel {
    public CurseForgePanel(FileEntryPaneContext context, ContentManagementFolderFileEntry fileEntry) {
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        KGridBagLayoutPanel formPanel = new KGridBagLayoutPanel(3, 2);
        formPanel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel descriptionLabel = new JLabel("You can add file to packwiz with projects from CurseForge");
        descriptionLabel.setAlignmentX(LEFT_ALIGNMENT);
        formPanel.addRow(2, descriptionLabel);

        KTextField contentTextField = new KTextField("Cloth Config API");
        formPanel.addRow(1, new JLabel("URL/Search Term: "), contentTextField);

        KButton addButton = new KButton("Add Project");
        addButton.setMnemonic(KeyEvent.VK_A);
        addButton.setAlignmentX(LEFT_ALIGNMENT);
        formPanel.addRow(2, addButton);

        contentTextField.getDocument().addDocumentListener(new DocumentChangedListener(() -> updateAddProjectButtonState(addButton, contentTextField)));

        updateAddProjectButtonState(addButton, contentTextField);

        addButton.addActionListener(actionEvent -> {
            ProgramExecution programExecution = Main.packwiz.buildCommand("curseforge", "add", contentTextField.getText());
            ExecutableProgressDialog dialog = new ExecutableProgressDialog(null, "Adding mod...", Constants.REASON_TRIGGERED_BY_USER, programExecution);

            List<String> recordedOutputs = new ArrayList<>();

            AtomicReference<String> modName = new AtomicReference<>(null);
            AtomicBoolean recordOutput = new AtomicBoolean();

            List<ManualModInfo> manualModInfos = new ArrayList<>();
            programExecution.whenStdout((line) -> {
                if(line.startsWith("Searching CurseForge...") || line.startsWith("Dependencies found:")) {
                    recordedOutputs.clear();
                    recordOutput.set(true);
                    return;
                }

                if(line.startsWith("0) Cancel")) { // We have a GUI cancel button, we don't need this entry
                    return;
                }

                if(line.startsWith("Choose a number:")) {
                    recordOutput.set(false);
                    new ModSelectionDialog(dialog, "Select mod", recordedOutputs, (selectIdx) -> {
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
                    if(JOptionPane.showConfirmDialog(dialog, String.format("The following dependencies are required:\n%s\nDo you want to add them?", depList), Util.withTitlePrefix("Add dependencies?"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
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
                    String choice = line.substring(line.indexOf(") ")+2);
                    recordedOutputs.add(choice);
                }
            });

            programExecution.whenExit((exitCode) -> {
               if(exitCode == 0) {
                   JOptionPane.showMessageDialog(context.getParent(), String.format("%s has been added to the modpack!", modName.get()), Util.withTitlePrefix("Project added"), JOptionPane.INFORMATION_MESSAGE);
               }
            });

            dialog.setVisible(true);
        });

        panel.add(formPanel);
        add(panel, BorderLayout.CENTER);
    }

    private void updateAddProjectButtonState(JButton addButton, KTextField urlTextField) {
        boolean shouldEnable = !urlTextField.getText().isEmpty();

        addButton.setEnabled(shouldEnable);
    }
}
