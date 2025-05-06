package com.lx862.pwgui.gui.panel.editing;

import com.lx862.pwgui.Main;
import com.lx862.pwgui.core.Modpack;
import com.lx862.pwgui.data.model.file.*;
import com.lx862.pwgui.gui.components.fstree.FileSystemTree;
import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.gui.components.fstree.FileSystemTreeCellRenderer;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;

class FileBrowserPanel extends JPanel {
    public final FileSystemTree fileBrowserTree;
    private static final Color NEW_FILE_LIST_COLOR = new Color(0x44FFAA00, true);

    public FileBrowserPanel(Modpack modpack) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.gridx = 0;

        fileBrowserTree = new FileSystemTree(modpack.getRootPath(), (file -> getFileModel(modpack, file)));
        fileBrowserTree.setCellRenderer(new FileSystemTreeCellRenderer(NEW_FILE_LIST_COLOR));

        gbc.weighty = 1.0;
        add(new JScrollPane(fileBrowserTree), gbc);

        KButton openDirectoryButton = new KButton("Open Containing Folder...", UIManager.getIcon("FileView.directoryIcon"));
        openDirectoryButton.addActionListener(actionEvent -> {
            fileBrowserTree.repaint();
            Util.tryOpenFile(modpack.getRootPath().toFile());
        });

        gbc.weighty = 0.0;
        add(openDirectoryButton, gbc);
    }

    private static FileSystemEntityModel getFileModel(Modpack modpack, File file) {
        try {
            if (isFileFromModpackRoot(modpack, file, ".packwizignore")) {
                return new PackwizIgnoreFileModel(file);
            } else if (file.getName().equals(".gitignore")) {
                return new GitIgnoreFileModel(file);
            } else if (file.getName().equals("LICENSE") || file.getName().equals("LICENSE.txt")) {
                return new LicenseFileModel(file);
            } else if (isFileFromModpackRoot(modpack, file, "options.txt")) {
                return new MinecraftOptionsFileModel(file);
            } else if (file.isDirectory() && isFileFromModpackRoot(modpack, file, "config")) {
                return new ConfigDirectoryModel(file);
            } else if (file.isDirectory() && (isFileFromModpackRoot(modpack, file, "mods") || isFileFromModpackRoot(modpack, file, "resourcepacks") || isFileFromModpackRoot(modpack, file, "shaderpacks") || isFileFromModpackRoot(modpack, file, "plugins"))) {
                return new ContentDirectoryModel(file);
            } else if (file.toPath().equals(modpack.getPackFilePath())) {
                return new ModpackConfigFileModel(file);
            } else if (file.toPath().equals(modpack.packFile.get().getIndexPath())) {
                return new ModpackIndexFileModel(file);
                /* ----- Start file extension matching ----- */
            } else if (file.getName().endsWith(".pw.toml")) {
                return new PackMetadataFileModel(file);
            } else if (file.getName().endsWith(".md")) {
                return new MarkdownFileModel(file);
            } else if (file.getName().endsWith(".mrpack")) {
                return new ModrinthPackFileModel(file);
            } else if (file.getName().equals(".gitattributes") || file.getName().endsWith(".txt") || file.getName().endsWith(".ini") || file.getName().endsWith(".cfg") || file.getName().endsWith(".json") || file.getName().endsWith(".json5") || file.getName().endsWith(".yml") || file.getName().endsWith(".yaml") || file.getName().endsWith(".toml") || file.getName().endsWith(".mcmeta") || file.getName().endsWith(".properties")) {
                return new PlainTextFileModel(file);
            } else if (file.getName().endsWith(".png") || file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".webp") || file.getName().endsWith(".bmp")) {
                return new ImageFileModel(file);
            } else if (file.isFile()) {
                return new GenericFileModel(file);
            } else {
                return new DirectoryModel(file);
            }
        } catch (Exception e) {
            Main.LOGGER.exception(e);
            return null;
        }
    }

    private static boolean isFileFromModpackRoot(Modpack modpack, File file, String fileNameFromRoot) {
        return modpack.getRootPath().resolve(fileNameFromRoot).equals(file.toPath());
    }
}
