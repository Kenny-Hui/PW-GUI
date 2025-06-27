package com.lx862.pwgui.gui.panel.editing;

import com.lx862.pwgui.PWGUI;
import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.executable.Executables;
import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.gui.components.kui.KTabbedPane;
import com.lx862.pwgui.gui.panel.editing.filetype.*;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class FileDetailPanel extends JPanel {
    public final KTabbedPane fileEntryTab;
    public final KButton saveButton;

    public FileDetailPanel() {
        setLayout(new BorderLayout());

        this.fileEntryTab = new KTabbedPane();
        this.saveButton = new KButton("Save");
        saveButton.setVisible(false);

        saveButton.addActionListener(actionEvent -> saveTab((JPanel)this.fileEntryTab.getSelectedComponent(), true));
        fileEntryTab.addChangeListener(changeEvent -> {
            saveButton.setEnabled(fileEntryTab.getSelectedComponent() instanceof FileTypePanel panel && panel.shouldSave());
            saveButton.setVisible(fileEntryTab.getSelectedComponent() instanceof FileTypePanel panel && panel.savable());
        });

        JPanel fileEntryActionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0 ,0));
        fileEntryActionRow.add(saveButton);
        add(fileEntryTab, BorderLayout.CENTER);
        add(fileEntryActionRow, BorderLayout.SOUTH);
    }

    public void saveAllTabs(boolean showUnsavedPrompt) {
        boolean haveUnsaved = false;

        for(int i = 0; i < fileEntryTab.getTabCount(); i++) {
            Component component = fileEntryTab.getComponent(i);
            if(component instanceof FileTypePanel) {
                if(((FileTypePanel) component).shouldSave()) haveUnsaved = true;
            }
        }

        if(!haveUnsaved) return;

        if(showUnsavedPrompt) {
            if(JOptionPane.showConfirmDialog(getTopLevelAncestor(), "You have edited files that are not saved.\nDo you want to save changes?", Util.withTitlePrefix("Unsaved Changes"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
                return;
            }
        }

        for(int i = 0; i < fileEntryTab.getTabCount(); i++) {
            Component component = fileEntryTab.getComponent(i);
            if(component instanceof FileTypePanel filePanel) {
                saveTab(filePanel, false);
            }
        }

        // Refresh packwiz after saving file
        Executables.packwiz.refresh().execute(String.format("File modified by %s", Constants.PROGRAM_NAME));
    }

    private void saveTab(JPanel panel, boolean shouldRefresh) {
        if(panel instanceof FileTypePanel fileTypePanel && fileTypePanel.shouldSave()) {
            try {
                fileTypePanel.save();
            } catch (IOException e) {
                PWGUI.LOGGER.exception(e);
                JOptionPane.showMessageDialog(this, String.format("Failed to save file!\n%s", e.getMessage()), Util.withTitlePrefix("Failed to Save!"), JOptionPane.ERROR_MESSAGE);
            }
            if(shouldRefresh) Executables.packwiz.refresh().execute(String.format("File modified by %s", Constants.PROGRAM_NAME));
        }
    }
}
