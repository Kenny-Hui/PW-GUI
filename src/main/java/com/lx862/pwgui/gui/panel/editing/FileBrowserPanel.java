package com.lx862.pwgui.gui.panel.editing;

import com.lx862.pwgui.core.Modpack;
import com.lx862.pwgui.data.fileentry.*;
import com.lx862.pwgui.gui.base.fstree.FileSystemTree;
import com.lx862.pwgui.gui.base.kui.KButton;
import com.lx862.pwgui.gui.base.kui.KTreeCellRenderer;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.io.File;

class FileBrowserPanel extends JPanel {
    public final FileSystemTree fileBrowserTree;

    public FileBrowserPanel(Modpack modpack) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.gridx = 0;

        fileBrowserTree = new FileSystemTree(modpack.getRootPath(), (file -> getFileType(modpack, file)));
        fileBrowserTree.setCellRenderer(new KTreeCellRenderer());

        gbc.weighty = 1.0;
        add(new JScrollPane(fileBrowserTree), gbc);

        KButton openFolderButton = new KButton("Open Containing Folder...", UIManager.getIcon("FileView.directoryIcon"));
        openFolderButton.addActionListener(actionEvent -> {
            fileBrowserTree.repaint();
            Util.tryOpenFile(modpack.getRootPath().toFile());
        });

        gbc.weighty = 0.0;
        add(openFolderButton, gbc);
    }

    private static FileSystemEntityEntry getFileType(Modpack modpack, File file) {
        if (file.getName().equals(".packwizignore")) {
            return new PackwizIgnoreFileEntry(file);
        } else if (file.getName().equals(".gitignore")) {
            return new GitIgnoreFileEntry(file);
        } else if (file.getName().equals("LICENSE")) {
            return new LicenseFileEntry(file);
        } else if (file.getName().endsWith(".pw.toml")) {
            return new PackMetadataFileEntry(file);
        } else if (file.toPath().equals(modpack.getPackFilePath())) {
            return new ModpackConfigFileEntry(file);
        } else if (file.toPath().equals(modpack.packFile.get().getIndexPath())) {
            return new ModpackIndexFileEntry(file);
        } else if (file.getName().endsWith(".md")) {
            return new MarkdownFileEntry(file);
        } else if (file.getName().endsWith(".mrpack")) {
            return new ModrinthPackFileEntry(file);
        } else if (file.getName().equals("options.txt")) {
            return new MinecraftOptionsFileEntry(file);
        } else if (file.getName().equals(".gitattributes") || file.getName().endsWith(".txt") || file.getName().endsWith(".json") || file.getName().endsWith(".toml") || file.getName().endsWith(".properties")) {
            return new PlainTextFileEntry(file);
        } else if (file.getName().endsWith(".png") || file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")) {
            return new ImageFileEntry(file);
        } else if (modpack.getRootPath().resolve("config").equals(file.toPath())) {
            return new ConfigFolderEntry(file);
        } else if (modpack.getRootPath().resolve("mods").equals(file.toPath()) || modpack.getRootPath().resolve("resourcepacks").equals(file.toPath()) || modpack.getRootPath().resolve("shaderpacks").equals(file.toPath()) || modpack.getRootPath().resolve("plugins").equals(file.toPath())) {
            return new ContentManagementFolderFileEntry(file);
        } else if (file.isFile()) {
            return new GenericFileEntry(file);
        } else {
            return new FileSystemEntityEntry(file); // Directory
        }
    }
}
