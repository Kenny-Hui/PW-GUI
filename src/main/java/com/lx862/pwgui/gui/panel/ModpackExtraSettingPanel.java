package com.lx862.pwgui.gui.panel;

import com.lx862.pwgui.core.Modpack;
import com.lx862.pwgui.data.PackComponent;
import com.lx862.pwgui.core.PackFile;
import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.gui.components.kui.KFileChooser;
import com.lx862.pwgui.gui.components.kui.KGridBagLayoutPanel;
import com.lx862.pwgui.gui.dialog.ChangeAcceptableGameVersionDialog;
import com.lx862.pwgui.gui.panel.editing.filetype.FileEntryPaneContext;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

public class ModpackExtraSettingPanel extends KGridBagLayoutPanel {
    private final Path initialDatapackPath;
    private final PackFile existingFile;
    private final Runnable updateSaveState;

    public ModpackExtraSettingPanel(FileEntryPaneContext context, PackFile existingFile, Runnable updateSaveState) {
        super(3, 3);

        this.existingFile = existingFile;
        this.updateSaveState = updateSaveState;
        this.initialDatapackPath = existingFile.getDatapackPath();

        KButton changeVersionRangeButton = new KButton("Change...");
        changeVersionRangeButton.addActionListener(actionEvent -> {
            new ChangeAcceptableGameVersionDialog((JFrame) getTopLevelAncestor(), existingFile.getComponent(PackComponent.MINECRAFT).getVersion(), existingFile.getOptionAcceptableGameVersion(true), () -> context.promptForSave()).setVisible(true);
        });

        addRow(1, 0, new JLabel("Acceptable Minecraft Version: " + String.join(", ", existingFile.getOptionAcceptableGameVersion(true))), changeVersionRangeButton);

        JLabel datapackDirectoryLabel = new JLabel(getDatapackFolderString());
        KButton changeDatapackDirectoryButton = new KButton("Change...");
        changeDatapackDirectoryButton.addActionListener(actionEvent -> changeDatapackDirectory(context.getModpack(), existingFile, datapackDirectoryLabel));
        addRow(1, 0, datapackDirectoryLabel, changeDatapackDirectoryButton);
    }

    private void changeDatapackDirectory(Modpack modpack, PackFile existingFile, JLabel datapackFolderLabel) {
        KFileChooser fileChooser = new KFileChooser("datapack-location", modpack.getRootPath());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if(fileChooser.showOpenDialog(getTopLevelAncestor()) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if(!withinDirectory(modpack.getRootPath(), selectedFile)) {
                JOptionPane.showMessageDialog(getTopLevelAncestor(), "Folder must be inside modpack folder!", Util.withTitlePrefix("Invalid Selected Directory"), JOptionPane.ERROR_MESSAGE);
                changeDatapackDirectory(modpack, existingFile, datapackFolderLabel);
                return;
            }
            existingFile.setDatapackPath(modpack.getRootPath().relativize(selectedFile.toPath()));
            datapackFolderLabel.setText(getDatapackFolderString());
            updateSaveState.run();
        }
    }

    private boolean withinDirectory(Path root, File file) {
        File parent = file;
        boolean isWithinDirectory = false;

        do {
            parent = parent.getParentFile();
            if(parent != null && parent.equals(root.toFile())) {
                isWithinDirectory = true;
                break;
            }
        } while(parent != null);

        return isWithinDirectory;
    }

    private String getDatapackFolderString() {
        return String.format("Datapack Folder: %s", existingFile.getDatapackPath() == null ? "(Not Configured)" : existingFile.getDatapackPath());
    }

    public boolean shouldSave() {
        return !Objects.equals(initialDatapackPath, existingFile.getDatapackPath());
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension size = getPreferredSize();
        size.width = Short.MAX_VALUE;
        return size;
    }
}
