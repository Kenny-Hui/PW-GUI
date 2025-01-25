package com.lx862.pwgui.gui.panel.editing;

import com.lx862.pwgui.core.Modpack;
import com.lx862.pwgui.core.PackFile;
import com.lx862.pwgui.data.fileentry.*;
import com.lx862.pwgui.gui.components.fstree.FileSystemTree;
import com.lx862.pwgui.gui.components.fstree.FileSystemWatcher;
import com.lx862.pwgui.gui.components.kui.KSplitPane;
import com.lx862.pwgui.gui.panel.editing.filetype.FileEntryPaneContext;
import com.lx862.pwgui.gui.components.NameTabPair;
import com.lx862.pwgui.gui.components.fstree.FileSystemSortedTreeNode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Collections;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.*;

public class EditPanel extends JPanel {
    private final HeaderPanel headerBarPanel;
    public final FileDetailPanel fileDetailPanel;
    private Thread fileWatcherThread;

    public EditPanel(Modpack modpack) {
        PackFile packFile = modpack.packFile.get();
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(7, 15, 15, 15));

        this.headerBarPanel = new HeaderPanel(packFile);
        add(headerBarPanel, BorderLayout.PAGE_START);

        this.fileDetailPanel = new FileDetailPanel(modpack);

        FileBrowserPanel fileBrowserPanel = new FileBrowserPanel(modpack);
        fileBrowserPanel.fileBrowserTree.addTreeSelectionListener(treeSelectionEvent -> {
            if(!fileBrowserPanel.fileBrowserTree.fsLock) { // File explorer tree also triggers the same event if our selected file is modified. In this case, we should just let them override our changes, as it's probably external changes.
                this.fileDetailPanel.saveAllTabs(true);
            }

            FileSystemSortedTreeNode node = (FileSystemSortedTreeNode) fileBrowserPanel.fileBrowserTree.getLastSelectedPathComponent();
            if(node == null) return;

            fileBrowserPanel.fileBrowserTree.setIgnorePattern(null);

            FileSystemEntityEntry entry = (FileSystemEntityEntry) node.getUserObject();
            List<NameTabPair> inspectPanels = entry.getInspectPanels(new FileEntryPaneContext(modpack, fileBrowserPanel.fileBrowserTree::setIgnorePattern, fileDetailPanel.saveButton::setEnabled));
            Collections.reverse(inspectPanels);
            fileDetailPanel.fileEntryTab.setTabs(inspectPanels);
        });

        KSplitPane splitPanel = new KSplitPane(JSplitPane.HORIZONTAL_SPLIT, fileBrowserPanel, fileDetailPanel, 0.5);
        add(splitPanel);

        registerKeyboardShortcut();
        startWatchFile(modpack, fileBrowserPanel.fileBrowserTree);
    }

    private void registerKeyboardShortcut() {
        registerKeyboardAction(actionEvent -> {
            fileDetailPanel.saveAllTabs(false);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void startWatchFile(Modpack modpack, FileSystemTree watchTree) {
        this.fileWatcherThread = new Thread(() -> {
            FileSystemWatcher watcher = new FileSystemWatcher(modpack.getRootPath(), true, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            watcher.startWatching((watchKey, e) -> {
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
            });
        });
        this.fileWatcherThread.start();
    }

    public void dispose() {
        this.fileDetailPanel.saveAllTabs(false);
        this.fileWatcherThread.interrupt();
    }
}

