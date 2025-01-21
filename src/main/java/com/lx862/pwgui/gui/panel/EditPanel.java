package com.lx862.pwgui.gui.panel;

import com.lx862.pwgui.core.Modpack;
import com.lx862.pwgui.core.PackFile;
import com.lx862.pwgui.data.PackComponentVersion;
import com.lx862.pwgui.data.fileentry.*;
import com.lx862.pwgui.gui.base.fstree.FileSystemTree;
import com.lx862.pwgui.gui.base.fstree.FileSystemWatcher;
import com.lx862.pwgui.gui.base.kui.KButton;
import com.lx862.pwgui.gui.base.kui.KTreeCellRenderer;
import com.lx862.pwgui.gui.panel.fileentrypane.FileEntryPaneContext;
import com.lx862.pwgui.gui.panel.fileentrypane.FileTypePanel;
import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.gui.base.NameTabPair;
import com.lx862.pwgui.gui.base.fstree.FileSystemSortedTreeNode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.*;

public class EditPanel extends JPanel {
    private final Thread fileWatcherThread;
    private final FileSystemTree fileBrowserTree;
    private final JPanel headerBarPanel;
    private final JTabbedPane entryTab;

    public EditPanel(Modpack modpack) {
        PackFile packFile = modpack.packFile.get();
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(7, 15, 15, 15));
        headerBarPanel = new JPanel();
        headerBarPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        headerBarPanel.setLayout(new BoxLayout(headerBarPanel, BoxLayout.LINE_AXIS));

        initHeaderBar(packFile);
        add(headerBarPanel, BorderLayout.PAGE_START);

        JPanel sideBySidePanel = new JPanel(new GridLayout(1, 2));

        entryTab = new JTabbedPane();
        entryTab.setBorder(new EmptyBorder(0, 4, 0, 4));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;

        JPanel fileBrowserPanel = new JPanel();
        fileBrowserPanel.setLayout(new GridBagLayout());

        fileBrowserTree = new FileSystemTree(modpack.getRoot(), (file -> getFileType(modpack, file)));
        fileBrowserTree.setCellRenderer(new KTreeCellRenderer());
        fileBrowserTree.addTreeSelectionListener(treeSelectionEvent -> {
            if(!fileBrowserTree.fsLock) { // File explorer tree also triggers the same event if our selected file is modified. In this case, we should just let them override our changes, as it's probably external changes.
                saveAllTabs();
            }

            final List<String> oldTabTitles = new ArrayList<>();
            for(int i = 0; i < entryTab.getTabCount(); i++) {
                oldTabTitles.add(entryTab.getTitleAt(i));
            }

            int oldSelected = entryTab.getSelectedIndex();

            entryTab.removeAll();
            FileSystemSortedTreeNode node = (FileSystemSortedTreeNode) fileBrowserTree.getLastSelectedPathComponent();
            if(node == null) return;

            FileSystemEntityEntry entry = (FileSystemEntityEntry) node.getUserObject();
            List<NameTabPair> inspectPanels = entry.getInspectPanels(new FileEntryPaneContext(this, modpack, fileBrowserTree::setIgnorePattern));
            Collections.reverse(inspectPanels);

            for(NameTabPair nameTabPair : inspectPanels) {
                entryTab.add(nameTabPair.title, nameTabPair.component);
            }

            final List<String> newTitles = new ArrayList<>();
            for(int i = 0; i < entryTab.getTabCount(); i++) {
                newTitles.add(entryTab.getTitleAt(i));
            }

            if(oldTabTitles.equals(newTitles) && oldSelected != -1) {
                entryTab.setSelectedIndex(oldSelected);
            }
        });

        c.weighty = 1.0;
        fileBrowserPanel.add(new JScrollPane(fileBrowserTree), c);

        KButton openFolderButton = new KButton("Open Containing Folder...", UIManager.getIcon("FileView.directoryIcon"));
        openFolderButton.addActionListener(actionEvent -> {
            fileBrowserTree.repaint();
            Util.tryOpenFile(modpack.getRoot().toFile());
        });

        c.weighty = 0.0;
        fileBrowserPanel.add(openFolderButton, c);
        sideBySidePanel.add(fileBrowserPanel);
        sideBySidePanel.add(entryTab);

        add(sideBySidePanel, BorderLayout.CENTER);

        this.fileWatcherThread = new Thread(() -> {
            FileSystemWatcher watcher = new FileSystemWatcher(modpack.getRoot(), true, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            watcher.startWatching((watchKey -> {
                for (WatchEvent<?> e : watchKey.pollEvents())
                {
                    Path directory = (Path)watchKey.watchable();
                    WatchEvent.Kind<?> kind = e.kind();
                    final Path filePath = directory.resolve(((WatchEvent<Path>)e).context());

                    SwingUtilities.invokeLater(() -> {
                        fileBrowserTree.onFileChange(kind, filePath);

                        if(kind == ENTRY_CREATE || kind == ENTRY_MODIFY) {
                            if(filePath.equals(modpack.packFile.get().getPath())) {
                                modpack.packFile.clearCache();
                                initHeaderBar(modpack.packFile.get()); // Update header with new info
                            } else if(filePath.equals(modpack.packFile.get().packIndexFile.get().getPath())) {
                                modpack.packFile.get().packIndexFile.clearCache();
                            }
                        }
                    });
                }
            }));
        });
        this.fileWatcherThread.start();
    }

    public void initHeaderBar(PackFile packFile) {
        headerBarPanel.removeAll();

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        JLabel modpackNameLabel = new JLabel(packFile.name);
        modpackNameLabel.setFont(UIManager.getFont("h2.font"));
        leftPanel.add(modpackNameLabel);

        JLabel modpackVersionAuthorLabel = new JLabel();
        if(packFile.author != null) {
            modpackVersionAuthorLabel.setText(packFile.version + " by " + packFile.author);
        } else {
            modpackVersionAuthorLabel.setText(packFile.version);
        }
        leftPanel.add(modpackVersionAuthorLabel);

        headerBarPanel.add(leftPanel);
        headerBarPanel.add(Box.createHorizontalGlue());

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        for(PackComponentVersion packComponentVersion : packFile.getComponents()) {
            final JLabel componentLabel = new JLabel(packComponentVersion.component.iconName.name + " version: " + packComponentVersion.version, new ImageIcon(GUIHelper.resizeImage(packComponentVersion.component.iconName.image, 20)), SwingConstants.LEFT);
            componentLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            rightPanel.add(componentLabel);
        }

        headerBarPanel.add(rightPanel);
        headerBarPanel.revalidate();
        headerBarPanel.repaint();
    }

    public void saveAllTabs() {
        boolean saveOccured = false;
        for(int i = 0; i < entryTab.getTabCount(); i++) {
            Component component = entryTab.getComponent(i);
            if(component instanceof FileTypePanel) {
                FileTypePanel fileTypePanel = (FileTypePanel) component;
                if(fileTypePanel.shouldSave()) {
                    boolean shouldSave;
                    if(!fileTypePanel.autoSaveOnExit()) {
                        shouldSave = JOptionPane.showConfirmDialog(this, "You have unsaved changes.\nDo you want to save?", Util.withTitlePrefix("Unsaved changes"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
                    } else {
                        shouldSave = true;
                    }

                    if(shouldSave) {
                        try {
                            fileTypePanel.save(this);
                            saveOccured = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(this, "Failed to save file!\n" + e.getMessage(), Util.withTitlePrefix("Save error"), JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                fileTypePanel.unload();
            }
        }

        // Refresh packwiz after saving file
        if(saveOccured) {
            Main.packwiz.buildCommand("refresh").execute("File modified by " + Constants.PROGRAM_NAME);
        }
    }

    public void dispose() {
        saveAllTabs();
        this.fileWatcherThread.interrupt();
    }

    private static FileSystemEntityEntry getFileType(Modpack modpack, File file) {
        if(file.getName().equals(".packwizignore")) {
            return new PackwizIgnoreFileEntry(file);
        } else if(file.getName().equals(".gitignore")) {
            return new GitIgnoreFileEntry(file);
        } else if(file.getName().equals("LICENSE")) {
            return new LicenseFileEntry(file);
        } else if(file.getName().endsWith(".pw.toml")) {
            return new PackMetadataFileEntry(file);
        } else if(file.toPath().equals(modpack.packFile.get().getPath())) {
            return new ModpackConfigFileEntry(file);
        } else if(file.toPath().equals(modpack.packFile.get().getIndexPath())) {
            return new ModpackIndexFileEntry(file);
        } else if(file.getName().endsWith(".md")) {
            return new MarkdownFileEntry(file);
        } else if(file.getName().endsWith(".mrpack")) {
            return new ModrinthPackFileEntry(file);
        } else if(file.getName().equals("options.txt")) {
            return new MinecraftOptionsFileEntry(file);
        } else if(file.getName().equals(".gitattributes") || file.getName().endsWith(".txt") || file.getName().endsWith(".json") || file.getName().endsWith(".toml") || file.getName().endsWith(".properties")) {
            return new PlainTextFileEntry(file);
        } else if(file.getName().endsWith(".png") || file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")) {
            return new ImageFileEntry(file);
        } else if(modpack.getRoot().resolve("config").equals(file.toPath())) {
            return new ConfigFolderEntry(file);
        } else if(modpack.getRoot().resolve("mods").equals(file.toPath()) || modpack.getRoot().resolve("resourcepacks").equals(file.toPath()) || modpack.getRoot().resolve("shaderpacks").equals(file.toPath()) || modpack.getRoot().resolve("plugins").equals(file.toPath())) {
            return new ContentManagementFolderFileEntry(file);
        } else if(file.isFile()) {
            return new GenericFileEntry(file);
        } else {
            return new FileSystemEntityEntry(file); // Directory
        }
    }
}
