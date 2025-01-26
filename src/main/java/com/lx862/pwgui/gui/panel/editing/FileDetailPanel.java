package com.lx862.pwgui.gui.panel.editing;

import com.lx862.pwgui.Main;
import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.core.Modpack;
import com.lx862.pwgui.data.fileentry.FileSystemEntityEntry;
import com.lx862.pwgui.gui.components.NameTabPair;
import com.lx862.pwgui.gui.components.fstree.FileSystemSortedTreeNode;
import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.gui.components.kui.KTabbedPane;
import com.lx862.pwgui.gui.panel.editing.filetype.FileEntryPaneContext;
import com.lx862.pwgui.gui.panel.editing.filetype.FileTypePanel;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class FileDetailPanel extends JPanel {
    public final KTabbedPane fileEntryTab;
    public final KButton saveButton;

    public FileDetailPanel(Modpack modpack) {
        setLayout(new BorderLayout());

        this.fileEntryTab = new KTabbedPane();
        this.saveButton = new KButton("Save");
        saveButton.setVisible(false);

        saveButton.addActionListener(actionEvent -> saveTab((FileTypePanel) this.fileEntryTab.getSelectedComponent(), true));
        fileEntryTab.addChangeListener(changeEvent -> {
            saveButton.setEnabled(fileEntryTab.getSelectedComponent() != null && ((FileTypePanel)fileEntryTab.getSelectedComponent()).shouldSave());
            saveButton.setVisible(fileEntryTab.getSelectedComponent() != null && ((FileTypePanel)fileEntryTab.getSelectedComponent()).savable());
        });

        FileBrowserPanel fileBrowserPanel = new FileBrowserPanel(modpack);
        fileBrowserPanel.fileBrowserTree.addTreeSelectionListener(treeSelectionEvent -> {
            if(!fileBrowserPanel.fileBrowserTree.fsLock) { // File explorer tree also triggers the same event if our selected file is modified. In this case, we should just let them override our changes, as it's probably external changes.
                saveAllTabs(true);
            }

            FileSystemSortedTreeNode node = (FileSystemSortedTreeNode) fileBrowserPanel.fileBrowserTree.getLastSelectedPathComponent();
            if(node == null) return;

            fileBrowserPanel.fileBrowserTree.setIgnorePattern(null);

            FileSystemEntityEntry entry = (FileSystemEntityEntry) node.getUserObject();
            List<NameTabPair> inspectPanels = entry.getInspectPanels(new FileEntryPaneContext(modpack, fileBrowserPanel.fileBrowserTree::setIgnorePattern, saveButton::setEnabled));
            Collections.reverse(inspectPanels);
            fileEntryTab.setTabs(inspectPanels);
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
            if(component instanceof FileTypePanel) {
                saveTab((FileTypePanel) component, false);
            }
        }

        // Refresh packwiz after saving file
        Main.packwiz.buildCommand("refresh").execute(String.format("File modified by %s", Constants.PROGRAM_NAME));
    }

    private void saveTab(FileTypePanel fileTypePanel, boolean shouldRefresh) {
        if(fileTypePanel.shouldSave()) {
            try {
                fileTypePanel.save();
            } catch (IOException e) {
                Main.LOGGER.exception(e);
                JOptionPane.showMessageDialog(this, String.format("Failed to save file!\n%s", e.getMessage()), Util.withTitlePrefix("Failed to Save!"), JOptionPane.ERROR_MESSAGE);
            }
            if(shouldRefresh) Main.packwiz.buildCommand("refresh").execute(String.format("File modified by %s", Constants.PROGRAM_NAME));
        }
    }
}
