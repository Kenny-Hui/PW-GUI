package com.lx862.pwgui.gui.panel.editing;

import com.lx862.pwgui.core.Modpack;
import com.lx862.pwgui.core.PackFile;
import com.lx862.pwgui.data.fileentry.*;
import com.lx862.pwgui.gui.base.fstree.FileSystemTree;
import com.lx862.pwgui.gui.base.fstree.FileSystemWatcher;
import com.lx862.pwgui.gui.base.kui.KButton;
import com.lx862.pwgui.gui.base.kui.KSplitPane;
import com.lx862.pwgui.gui.base.kui.KTabbedPane;
import com.lx862.pwgui.gui.panel.editing.filetype.FileEntryPaneContext;
import com.lx862.pwgui.gui.panel.editing.filetype.FileTypePanel;
import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.gui.base.NameTabPair;
import com.lx862.pwgui.gui.base.fstree.FileSystemSortedTreeNode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Collections;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.*;

public class EditPanel extends JPanel {
    private final HeaderPanel headerBarPanel;
    private final KTabbedPane fileEntryTab;
    private Thread fileWatcherThread;

    public EditPanel(Modpack modpack) {
        PackFile packFile = modpack.packFile.get();
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(7, 15, 15, 15));

        this.headerBarPanel = new HeaderPanel(packFile);
        add(headerBarPanel, BorderLayout.PAGE_START);

        this.fileEntryTab = new KTabbedPane();
        KButton saveButton = new KButton("Save");
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
            List<NameTabPair> inspectPanels = entry.getInspectPanels(new FileEntryPaneContext(this, modpack, fileBrowserPanel.fileBrowserTree::setIgnorePattern, saveButton::setEnabled));
            Collections.reverse(inspectPanels);
            fileEntryTab.setTabs(inspectPanels);
        });

        JPanel fileDetailPanel = new JPanel(new BorderLayout());
        fileDetailPanel.add(fileEntryTab, BorderLayout.CENTER);

        JPanel fileEntryActionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0 ,0));
        fileEntryActionRow.add(saveButton);
        fileDetailPanel.add(fileEntryActionRow, BorderLayout.SOUTH);

        KSplitPane splitPanel = new KSplitPane(JSplitPane.HORIZONTAL_SPLIT, fileBrowserPanel, fileDetailPanel, 0.5);
        add(splitPanel);

        startWatchFile(modpack, fileBrowserPanel.fileBrowserTree);
    }

    private void startWatchFile(Modpack modpack, FileSystemTree watchTree) {
        this.fileWatcherThread = new Thread(() -> {
            FileSystemWatcher watcher = new FileSystemWatcher(modpack.getRootPath(), true, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            watcher.startWatching((watchKey -> {
                for (WatchEvent<?> e : watchKey.pollEvents())
                {
                    Path directory = (Path)watchKey.watchable();
                    WatchEvent.Kind<?> kind = e.kind();
                    final Path filePath = directory.resolve(((WatchEvent<Path>)e).context());

                    SwingUtilities.invokeLater(() -> {
                        watchTree.onFileChange(kind, filePath);

                        if(kind == ENTRY_CREATE || kind == ENTRY_MODIFY) {
                            if(filePath.equals(modpack.getPackFilePath())) {
                                modpack.packFile.clearCache();
                                headerBarPanel.initialize(modpack.packFile.get()); // Update header with new info
                            } else if(filePath.equals(modpack.packFile.get().getIndexPath())) {
                                modpack.packFile.get().packIndexFile.clearCache();
                            }
                        }
                    });
                }
            }));
        });
        this.fileWatcherThread.start();
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
            if(JOptionPane.showConfirmDialog(this, "You have edited files that are not saved.\nDo you want to save changes?", Util.withTitlePrefix("Unsaved Changes"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
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
        Main.packwiz.buildCommand("refresh").execute("File modified by " + Constants.PROGRAM_NAME);
    }

    private void saveTab(FileTypePanel fileTypePanel, boolean shouldRefresh) {
        if(fileTypePanel.shouldSave()) {
            try {
                fileTypePanel.save(this);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to save file!\n" + e.getMessage(), Util.withTitlePrefix("Save error"), JOptionPane.ERROR_MESSAGE);
            }
            if(shouldRefresh) Main.packwiz.buildCommand("refresh").execute("File modified by " + Constants.PROGRAM_NAME);
        }
    }

    public void dispose() {
        saveAllTabs(false);
        this.fileWatcherThread.interrupt();
    }
}

