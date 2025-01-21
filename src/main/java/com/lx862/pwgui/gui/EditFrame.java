package com.lx862.pwgui.gui;

import com.lx862.pwgui.core.Modpack;
import com.lx862.pwgui.gui.base.kui.KMenu;
import com.lx862.pwgui.gui.base.kui.KMenuItem;
import com.lx862.pwgui.gui.dialog.ExportModpackDialog;
import com.lx862.pwgui.util.Util;
import org.apache.commons.io.FileUtils;
import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.gui.dialog.ImportModpackDialog;
import com.lx862.pwgui.gui.base.BaseFrame;
import com.lx862.pwgui.gui.panel.EditPanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class EditFrame extends BaseFrame {
    private final EditPanel editPanel;

    public EditFrame(Component parent, Modpack modpack) {
        super(Util.withTitlePrefix(String.format("Editing %s", modpack.packFile.get().name)));
        Main.packwiz.setPackFileLocation(modpack.getRoot().relativize(modpack.packFile.get().getPath()).toString());

        setSize(900, 650);
        setLocationRelativeTo(parent);

        Main.packwiz.changeWorkingDirectory(modpack.getRoot());

        editPanel = new EditPanel(modpack);
        add(editPanel);
        initMenuBar(modpack);
    }

    private void initMenuBar(Modpack modpack) {
        KMenu fileMenu = new KMenu("File");

        KMenuItem saveMenuItem = new KMenuItem("Save selected file");
        saveMenuItem.addActionListener(actionEvent -> editPanel.saveAllTabs());
        fileMenu.add(saveMenuItem);

        KMenuItem importMenuItem = new KMenuItem("Import pack...");
        importMenuItem.addActionListener(actionEvent -> {
            editPanel.saveAllTabs();
            new ImportModpackDialog(this).setVisible(true);
        });
        fileMenu.add(importMenuItem);

        KMenuItem exportMenuItem = new KMenuItem("Export pack...");
        exportMenuItem.addActionListener(actionEvent -> {
            editPanel.saveAllTabs();
            new ExportModpackDialog(this, modpack).setVisible(true);
        });
        fileMenu.add(exportMenuItem);

        KMenuItem quitMenuItem = new KMenuItem("Quit...");
        fileMenu.add(quitMenuItem);

        quitMenuItem.addActionListener(actionEvent -> {
            dispose();
            WelcomeFrame welcomeFrame = new WelcomeFrame(this);
            welcomeFrame.setVisible(true);
        });

        /* ---------- Tool Menu ---------- */

        JMenu toolMenu = new JMenu("Tool");

        KMenuItem refreshMenuItem = new KMenuItem("Refresh Modpack");
        toolMenu.add(refreshMenuItem);
        refreshMenuItem.addActionListener((actionEvent -> {
            AtomicReference<String> lastLine = new AtomicReference<>();

            Main.packwiz.buildCommand("refresh")
                .whenStdout(lastLine::set)
                .whenExit(exitCode -> {
                    if(exitCode == 0) {
                        JOptionPane.showMessageDialog(this, "Modpack index refreshed!", Util.withTitlePrefix("Refresh Modpack Index"), JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, String.format("Packwiz exited with exit code %d:\n%s", exitCode, lastLine.get()), Util.withTitlePrefix("Refresh Modpack Index"), JOptionPane.ERROR_MESSAGE);
                    }
                })
                .execute(Constants.REASON_TRIGGERED_BY_USER);
        }));

        KMenuItem devServerMenuItem = new KMenuItem("Run Development Server...");
        toolMenu.add(devServerMenuItem);
        devServerMenuItem.addActionListener(actionEvent -> {
            DevServerFrame frame = new DevServerFrame(this);
            frame.setVisible(true);
        });

        KMenuItem pwConsoleMenuItem = new KMenuItem("Open Packwiz Console");
        toolMenu.add(pwConsoleMenuItem);
        pwConsoleMenuItem.addActionListener(actionEvent -> {
            ConsoleFrame frame = new ConsoleFrame(Main.packwiz, this);
            frame.setVisible(true);
        });

        /* ---------- Help Menu ---------- */

        KMenu helpMenu = new KMenu("Help");

        KMenuItem pwCacheItem = new KMenuItem("Clear Packwiz Cache");
        pwCacheItem.addActionListener(actionEvent -> clearPackwizCache());
        helpMenu.add(pwCacheItem);

        KMenuItem viewLogMenuItem = new KMenuItem("View Log");
        helpMenu.add(viewLogMenuItem);
        viewLogMenuItem.addActionListener(actionEvent -> {
            ViewLogFrame logViewer = new ViewLogFrame(this);
            logViewer.setVisible(true);
        });

        KMenuItem aboutMenuItem = new KMenuItem("About"); // TODO: Implement about dialog
        helpMenu.add(aboutMenuItem);

        this.jMenuBar.add(fileMenu);
        this.jMenuBar.add(toolMenu);
        this.jMenuBar.add(helpMenu);
    }

    private void clearPackwizCache() {
        if(JOptionPane.showConfirmDialog(this, "Are you sure you want to clear packwiz cache?\nThis is generally not necessary unless you are running out of disk space or encountered some corruption.", Util.withTitlePrefix("Clear Packwiz Cache?"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Path packwizCacheDir = getCacheDir().resolve("packwiz");
            if(!packwizCacheDir.toFile().exists()) {
                JOptionPane.showMessageDialog(this, "There are currently no packwiz cache yet, nothing to clear~", Util.withTitlePrefix("No Packwiz cache found"), JOptionPane.INFORMATION_MESSAGE);
            } else {
                try {
                    FileUtils.deleteDirectory(packwizCacheDir.toFile());
                    JOptionPane.showMessageDialog(this, "Packwiz cache has been cleared!", Util.withTitlePrefix("Packwiz cache cleared"), JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, String.format("Failed to clear packwiz cache:\n%s", e.getMessage()), Util.withTitlePrefix("Error while clearing Packwiz cache"), JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }


    // https://github.com/golang/go/blob/40b3c0e58a0ae8dec4684a009bf3806769e0fc41/src/os/file.go#L474-L485
    /** A reimplementation of Go's getCacheDir, which is used by packwiz to determine the cache location */
    private Path getCacheDir() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if(osName.contains("windows")) {
            return Paths.get(System.getenv("LocalAppData"));
        } else if(osName.contains("mac")) {
            return Paths.get(System.getenv("HOME") + "/Library/Caches");
        } else {
            String cacheDir = System.getenv("XDG_CACHE_HOME");
            if(cacheDir != null) {
                return Paths.get(cacheDir);
            } else {
                String home = System.getenv("HOME");
                return Paths.get(home).resolve(".cache");
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        editPanel.dispose();
        Main.packwiz.dispose();
        Main.git.dispose();
    }
}
