package com.lx862.pwgui.util;

import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.data.ManualModInfo;
import com.lx862.pwgui.executable.ProgramExecution;
import com.lx862.pwgui.gui.dialog.ExecutableProgressDialog;
import com.lx862.pwgui.gui.dialog.ManualDownloadDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Util {
    public static String withTitlePrefix(String str) {
        return String.format("%s - %s", Constants.PROGRAM_NAME, str);
    }

    public static String withBracketPrefix(String str) {
        return String.format("[%s] %s", Constants.PROGRAM_NAME, str);
    }

    public static InputStream getAssets(String path) {
        return Main.class.getResourceAsStream(path);
    }

    public static void copyToClipboard(String str) {
        StringSelection stringSelection = new StringSelection(str);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static void tryOpenFile(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            Main.LOGGER.exception(e);
            JOptionPane.showMessageDialog(null, String.format("Failed to open file \"%s\":\n%s", file.getName(), e.getMessage()), withTitlePrefix("Open External Application"), JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void tryBrowse(String uri) {
        try {
            Desktop.getDesktop().browse(new URI(uri));
        } catch (URISyntaxException | IOException e) {
            Main.LOGGER.exception(e);
            JOptionPane.showMessageDialog(null, String.format("Failed to open link \"%s\":\n%s", uri, e.getMessage()), withTitlePrefix("Open External Link"), JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void addManualDownloadPrompt(JDialog parentDialog, ProgramExecution programExecution, ExecutableProgressDialog executableProgressDialog, Runnable callback) {
        List<ManualModInfo> manualDownloadMod = new ArrayList<>();
        AtomicBoolean captureManualDownloadMod = new AtomicBoolean();
        AtomicReference<String> cachePath = new AtomicReference<>();

        programExecution.whenStdout((line) -> {
            if(line.contains("and must be manually downloaded")) {
                captureManualDownloadMod.set(true);
            } else if(line.contains("Once you have done so, place")) {
                cachePath.set(line.split(", place these files in ")[1].split(" and re-run")[0]);
                new ManualDownloadDialog(parentDialog, manualDownloadMod, (path) -> {
                    moveManualDownloadToCache(parentDialog, manualDownloadMod, path, Paths.get(cachePath.get()));
                    callback.run();
                }).setVisible(true);
            } else if(captureManualDownloadMod.get()) {
                // Format: Mod Display Name (mod_file_name.jar) from https://example.com

                String[] split = line.split(" from http");
                String[] splitForTheRest = split[0].split(" ");
                String fileName = "pwgui_cannot_parse_filename.jar";
                for(String word : splitForTheRest) {
                    if(word.startsWith("(") && word.endsWith(")")) { // We will take a blind shot that jar file don't have spaces...
                        fileName = word.substring(1, word.length()-1);
                        break;
                    }
                }
                String name = line.substring(0, line.indexOf(fileName)-2);
                String url = "http" + split[1];

                manualDownloadMod.add(new ManualModInfo(name, fileName, url));
            }
        });

        if(executableProgressDialog != null) {
            executableProgressDialog.whenProgramErrored(() -> !captureManualDownloadMod.get()); // Mute exit code 1 error pop up if it's about manual download
        }
    }

    private static void moveManualDownloadToCache(Component parent, List<ManualModInfo> modList, Path sourceDirectory, Path cacheDirectory) {
        for(ManualModInfo modInfo : modList) {
            Path sourcePath = sourceDirectory.resolve(modInfo.fileName);
            Path destinationPath = cacheDirectory.resolve(modInfo.fileName);
            try {
                Main.LOGGER.info(String.format("Moving manually downloaded file from \"%s\" to \"%s\"", sourcePath, destinationPath));
                Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, String.format("An error occured while moving manually downloaded files:\n%s\nPlease move %s manually to %s", e.getMessage(), modInfo.fileName, cacheDirectory), Util.withTitlePrefix("Failed to handle manually installed mods"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void copyAssetsToPath(String assetsPath, String outputName, Path path) throws IOException {
        try(InputStream is = getAssets(assetsPath)) {
            Files.copy(is, path.resolve(outputName), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
