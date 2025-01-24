package com.lx862.pwgui.gui.action;

import com.lx862.pwgui.Main;
import com.lx862.pwgui.core.Config;
import com.lx862.pwgui.gui.WelcomeFrame;
import com.lx862.pwgui.gui.dialog.DownloadProgressDialog;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadPackwizAction extends AbstractAction {
    private final JFrame parent;

    public DownloadPackwizAction(JFrame parent) {
        super("Download packwiz");
        this.parent = parent;
        putValue(MNEMONIC_KEY, KeyEvent.VK_D);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Path tempDirectory;
        try {
            tempDirectory = Files.createTempDirectory("pwgui");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, "Failed to create temporary directory!", Util.withTitlePrefix("Download failed"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Path tempDestination = tempDirectory.resolve("packwiz_executable.zip");

        try {
            URL url = new URL(String.format("https://nightly.link/packwiz/packwiz/workflows/go/main/%s.zip", getArtifactName()));
            DownloadProgressDialog downloadProgressDialog = new DownloadProgressDialog(parent, "Downloading packwiz...", "packwiz", url, tempDestination, () -> {
                AtomicReference<Path> packwizBinaryDestination = new AtomicReference<>();

                Path binDirectory = Config.CONFIG_DIR_PATH.resolve("bin");
                binDirectory.toFile().mkdirs();

                byte[] buffer = new byte[1024];
                try(FileInputStream fis = new FileInputStream(tempDestination.toFile()); ZipInputStream zis = new ZipInputStream(fis)) {
                    ZipEntry zipEntry;
                    while((zipEntry = zis.getNextEntry()) != null) {
                        if(zipEntry.getName().equals("packwiz") || zipEntry.getName().equals("packwiz.exe")) {
                            packwizBinaryDestination.set(binDirectory.resolve(zipEntry.getName()));
                            try(FileOutputStream fos = new FileOutputStream(binDirectory.resolve(zipEntry.getName()).toFile())) {
                                int len;
                                while ((len = zis.read(buffer)) > 0) {
                                    fos.write(buffer, 0, len);
                                }
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parent, String.format("Failed to extract packwiz from zip file:\n%s", e.getMessage()), Util.withTitlePrefix("Setup failed"), JOptionPane.ERROR_MESSAGE);
                }

                try {
                    Files.delete(tempDestination);
                    Files.delete(tempDirectory);
                } catch (IOException ignored) { // Not important
                }

                boolean configureSuccessful = configurePackwiz(packwizBinaryDestination.get());
                if(configureSuccessful) {
                    JOptionPane.showMessageDialog(parent, "Download successful!", Util.withTitlePrefix("Download success!"), JOptionPane.INFORMATION_MESSAGE);
                    WelcomeFrame welcomeFrame = new WelcomeFrame(parent);
                    welcomeFrame.setVisible(true);
                    parent.dispose();
                }
            });

            downloadProgressDialog.setVisible(true);
        } catch (MalformedURLException ignored) {
        }
    }

    private static String getArtifactName() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch");
        boolean isARM = arch.equals("aarch64") || arch.equals("arm");
        boolean isWindows = false;

        String artifactName;
        if(os.contains("windows")) {
            isWindows = true;
            artifactName = "Windows 64-bit";
        } else if(os.contains("mac")) {
            artifactName = "macOS 64-bit";
        } else if(os.contains("linux")) {
            artifactName = "Linux 64-bit";
        } else {
            throw new UnsupportedOperationException(String.format("%s is not supported.", os));
        }

        if(isARM) artifactName += " ARM";
        else if(!isWindows) artifactName += " x86";

        artifactName = artifactName.replace(" ", "%20");
        return artifactName;
    }

    private boolean configurePackwiz(Path path) {
        Main.config.packwizExecutablePath = path;

        try {
            try { // We want executable permission on *nix
                Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxr-----"));
            } catch (UnsupportedOperationException ignored) {
            }

            Main.packwiz.locate(null);
            if(!Main.packwiz.usable()) {
                JOptionPane.showMessageDialog(parent, "Packwiz executable is not valid :(\nPlease try manually downloading packwiz and locating it.", Util.withTitlePrefix("Invalid executable"), JOptionPane.ERROR_MESSAGE);
                return false;
            }

            Main.config.write();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Failed to write config file!", Util.withTitlePrefix("Setup failed"), JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
    }
}
