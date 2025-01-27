package com.lx862.pwgui.gui.components.kui;

import com.lx862.pwgui.Main;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class KFileChooser extends JFileChooser {
    private final String context;

    @SuppressWarnings("unused")
    public KFileChooser() {
        this(null);
    }

    public KFileChooser(String context) {
        this(context, null);
    }

    public KFileChooser(String context, Path defaultPath) {
        this.context = context;
        if(context != null && Main.getConfig().fileChooserLastPath.containsKey(context)) {
            setCurrentDirectory(Main.getConfig().fileChooserLastPath.get(context).toFile());
        } else {
            setCurrentDirectory(defaultPath == null ? Paths.get(System.getProperty("user.dir")).toFile() : defaultPath.toFile());
        }
    }

    /** Open the Save As dialog. User will be prompted if the file would get overwritten */
    public int openSaveAsDialog(Component component) {
        int showDialogResult = showSaveDialog(component);
        if(showDialogResult == APPROVE_OPTION) {
            if(Files.exists(getSelectedFile().toPath())) {
                int replaceResult = JOptionPane.showConfirmDialog(component, String.format("File \"%s\" already exist,\nAre you sure you want to replace the file?", getSelectedFile().getName()), Util.withTitlePrefix("Replace File?"), JOptionPane.YES_NO_OPTION);
                if(replaceResult == JOptionPane.YES_OPTION) {
                    return APPROVE_OPTION;
                } else {
                    return openSaveAsDialog(component);
                }
            } else {
                return APPROVE_OPTION;
            }
        } else {
            return showDialogResult;
        }
    }

    /** Open the Save As dialog. User will be prompted if the folder is not empty */
    public int openSaveDirectoryDialog(Component component) {
        int showDialogResult = showSaveDialog(component);
        if(showDialogResult == APPROVE_OPTION) {
            try(Stream<Path> files = Files.list(getSelectedFile().toPath())) {
                if(files.findAny().isPresent()){
                    int replaceResult = JOptionPane.showConfirmDialog(component, "Hmm folder is not empty, are you sure this is what you want?\nAll operations that follows will be performed directly in the folder you chose.\nIf that's not your intent, click \"No\" and create a new folder.", Util.withTitlePrefix("Folder Not Empty"), JOptionPane.YES_NO_OPTION);
                    if (replaceResult != JOptionPane.YES_OPTION) {
                        return openSaveDirectoryDialog(component);
                    }
                }
            } catch (IOException e) {
                Main.LOGGER.exception(e);
            }
        }
        return showDialogResult;
    }

    @Override
    public int showOpenDialog(Component parent) throws HeadlessException {
        int result = super.showOpenDialog(parent);
        if(context != null && result == APPROVE_OPTION) {
            saveLastOpenRecord(getSelectedFile());
        }
        return result;
    }

    @Override
    public int showSaveDialog(Component parent) throws HeadlessException {
        int result = super.showSaveDialog(parent);
        if(context != null && result == APPROVE_OPTION) {
            saveLastOpenRecord(getSelectedFile());
        }
        return result;
    }

    private void saveLastOpenRecord(File file) {
        File directory = file.isDirectory() ? file : file.getParentFile();
        Path existingRecord = Main.getConfig().fileChooserLastPath.get(context);
        if(existingRecord == null || !existingRecord.equals(directory.toPath())) { // Changed
            try {
                Main.getConfig().fileChooserLastPath.put(context, directory.toPath());
                Main.getConfig().write("Save file picker location");
            } catch (IOException e) {
                Main.LOGGER.exception(e);
            }
        }
    }
}
