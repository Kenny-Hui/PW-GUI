package com.lx862.pwgui.gui.panel.editing;

import com.lx862.pwgui.Main;
import com.lx862.pwgui.core.Modpack;
import com.lx862.pwgui.data.model.file.*;
import com.lx862.pwgui.gui.components.kui.KSplitPane;
import com.lx862.pwgui.gui.panel.editing.filetype.*;
import com.lx862.pwgui.gui.components.NameTabPair;
import com.lx862.pwgui.gui.components.fstree.FileSystemSortedTreeNode;
import com.lx862.pwgui.gui.panel.editing.filetype.content.AddContentPanel;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditPanel extends JPanel {
    private final FileBrowserPanel fileBrowserPanel;
    private final FileDetailPanel fileDetailPanel;

    public EditPanel(Modpack modpack) {
        setLayout(new BorderLayout());

        this.fileDetailPanel = new FileDetailPanel();
        this.fileBrowserPanel = new FileBrowserPanel(modpack);

        fileBrowserPanel.fileBrowserTree.addTreeSelectionListener(treeSelectionEvent -> {
            if(!fileBrowserPanel.fileBrowserTree.fsLock) { // File browser tree also triggers the same event if our selected file is modified externally. In this case, we should just let them override our changes, as it's probably external changes.
                saveChanges(true);
            }

            FileSystemSortedTreeNode node = (FileSystemSortedTreeNode) fileBrowserPanel.fileBrowserTree.getLastSelectedPathComponent();
            if(node == null) return;

            fileBrowserPanel.fileBrowserTree.setIgnorePattern(null);

            FileSystemEntityModel entry = (FileSystemEntityModel) node.getUserObject();
            List<NameTabPair> inspectPanels = getViews(new FileEntryPaneContext(modpack, fileBrowserPanel.fileBrowserTree::setIgnorePattern, fileDetailPanel.saveButton::setEnabled), entry);
            Collections.reverse(inspectPanels);
            fileDetailPanel.fileEntryTab.setTabs(inspectPanels);
        });

        KSplitPane splitPane = new KSplitPane(JSplitPane.HORIZONTAL_SPLIT, fileBrowserPanel, fileDetailPanel, 0.5);
        add(splitPane);
    }

    public void onFileChange(WatchEvent.Kind<?> kind, Path path) {
        fileBrowserPanel.fileBrowserTree.onFileChange(kind, path);
    }

    public void saveChanges(boolean notify) {
        fileDetailPanel.saveAllTabs(notify);
    }

    private static List<NameTabPair> getViews(FileEntryPaneContext context, FileSystemEntityModel node) {
        List<NameTabPair> panels = new ArrayList<>();
        try {
            if(node instanceof DirectoryModel) panels.add(new NameTabPair("Folder", new DirectoryPanel(context, (DirectoryModel)node)));
            if(node instanceof GenericFileModel) panels.add(new NameTabPair("File", new FilePanel(context, (GenericFileModel)node)));
            if(node instanceof ContentDirectoryModel) panels.add(new NameTabPair(String.format("Add new %s", node.getDisplayName()), new AddContentPanel(context, (ContentDirectoryModel) node)));
            if(node instanceof PlainTextFileModel) panels.add(new NameTabPair("Plain Text", new PlainTextPanel(context, (PlainTextFileModel) node)));
            if(node instanceof ConfigDirectoryModel) panels.add(new NameTabPair("Config Folder", new ConfigDirectoryPanel(context, (DirectoryModel)node)));
            if(node instanceof GitIgnoreFileModel) panels.add(new NameTabPair("Git Ignore", new GitIgnorePanel(context, (GitIgnoreFileModel) node)));
            if(node instanceof ImageFileModel) panels.add(new NameTabPair("Image Panel", new ImagePanel(context, (ImageFileModel) node)));
            if(node instanceof LicenseFileModel) panels.add(new NameTabPair("License", new LicenseFilePanel(context, (LicenseFileModel) node)));
            if(node instanceof MarkdownFileModel) panels.add(new NameTabPair("Markdown", new MarkdownPanel(context, (MarkdownFileModel) node)));
            if(node instanceof MinecraftOptionsFileModel) panels.add(new NameTabPair("Minecraft Options", new MinecraftOptionPanel(context, (MinecraftOptionsFileModel) node)));
            if(node instanceof ModpackConfigFileModel) panels.add(new NameTabPair("Modpack Config", new ModpackConfigPanel(context, (ModpackConfigFileModel) node)));
            if(node instanceof ModpackIndexFileModel) panels.add(new NameTabPair("Packwiz Index", new ModpackIndexFilePanel(context, (ModpackIndexFileModel) node)));
            if(node instanceof PackwizIgnoreFileModel) panels.add(new NameTabPair("Packwiz Ignore", new PackwizIgnorePanel(context, (PackwizIgnoreFileModel) node)));
            if(node instanceof PackMetadataFileModel) panels.add(new NameTabPair("Packwiz Meta File", new PackwizMetaPanel(context, (PackMetadataFileModel) node)));
        } catch (Exception e) {
            Main.LOGGER.exception(e);
        }

        return panels;
    }
}

