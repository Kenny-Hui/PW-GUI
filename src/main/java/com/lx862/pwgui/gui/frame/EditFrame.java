package com.lx862.pwgui.gui.frame;

import com.lx862.pwgui.core.Modpack;
import com.lx862.pwgui.gui.action.ClearPackwizCacheAction;
import com.lx862.pwgui.gui.action.RefreshPackAction;
import com.lx862.pwgui.gui.action.UpdateAllAction;
import com.lx862.pwgui.gui.components.kui.KMenu;
import com.lx862.pwgui.gui.components.kui.KMenuItem;
import com.lx862.pwgui.gui.popup.ConsoleDialog;
import com.lx862.pwgui.gui.dialog.ExportModpackDialog;
import com.lx862.pwgui.gui.popup.DevServerFrame;
import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.gui.popup.ImportModpackDialog;
import com.lx862.pwgui.gui.panel.editing.EditPanel;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EditFrame extends BaseFrame {
    private final EditPanel editPanel;

    public EditFrame(Component parent, Modpack modpack) {
        super(Util.withTitlePrefix(String.format("Editing %s", modpack.packFile.get().name)));

        setSize(900, 650);
        setLocationRelativeTo(parent);

        Main.packwiz.setPackFileLocation(modpack.getRootPath().relativize(modpack.getPackFilePath()).toString());
        Main.packwiz.changeWorkingDirectory(modpack.getRootPath());

        editPanel = new EditPanel(modpack);
        add(editPanel);

        this.jMenuBar.add(getFileMenu(modpack));
        this.jMenuBar.add(getEditMenu(modpack));
        this.jMenuBar.add(getToolMenu());
        this.jMenuBar.add(super.getHelpMenu());
    }

    private KMenu getFileMenu(Modpack modpack) {
        KMenu fileMenu = new KMenu("File");

        KMenuItem saveMenuItem = new KMenuItem("Save Selected File");
        saveMenuItem.setMnemonic(KeyEvent.VK_S);
        saveMenuItem.addActionListener(actionEvent -> editPanel.fileDetailPanel.saveAllTabs(false));
        fileMenu.add(saveMenuItem);

        KMenuItem importMenuItem = new KMenuItem("Import pack...");
        importMenuItem.setMnemonic(KeyEvent.VK_I);
        importMenuItem.addActionListener(actionEvent -> {
            editPanel.fileDetailPanel.saveAllTabs(false);
            new ImportModpackDialog(this).setVisible(true);
        });
        fileMenu.add(importMenuItem);

        KMenuItem exportMenuItem = new KMenuItem("Export pack...");
        exportMenuItem.setMnemonic(KeyEvent.VK_E);
        exportMenuItem.addActionListener(actionEvent -> {
            editPanel.fileDetailPanel.saveAllTabs(false);
            new ExportModpackDialog(this, modpack).setVisible(true);
        });
        fileMenu.add(exportMenuItem);

        KMenuItem quitMenuItem = new KMenuItem("Quit...");
        quitMenuItem.setMnemonic(KeyEvent.VK_Q);

        quitMenuItem.addActionListener(actionEvent -> {
            WelcomeFrame welcomeFrame = new WelcomeFrame(this);
            welcomeFrame.setVisible(true);
            dispose();
        });
        fileMenu.add(quitMenuItem);

        return fileMenu;
    }

    private KMenu getEditMenu(Modpack modpack) {
        KMenu editMenu = new KMenu("Edit");
        KMenu addMissingMenu = new KMenu("Add Missing...");
        KMenuItem modsDirectoryMenuItem = new KMenuItem("Mods Folder");
        modsDirectoryMenuItem.addActionListener(actionEvent -> makeFolder(modpack.getRootPath(), "mods"));

        KMenuItem resourcePacksDirectoryMenuItem = new KMenuItem("Resource Packs Folder");
        resourcePacksDirectoryMenuItem.addActionListener(actionEvent -> makeFolder(modpack.getRootPath(), "resourcepacks"));

        KMenuItem shaderPacksDirectoryMenuItem = new KMenuItem("Shader Packs Folder");
        shaderPacksDirectoryMenuItem.addActionListener(actionEvent -> makeFolder(modpack.getRootPath(), "shaderpacks"));

        KMenuItem configDirectoryMenuItem = new KMenuItem("Mods Config Folder");
        configDirectoryMenuItem.addActionListener(actionEvent -> makeFolder(modpack.getRootPath(), "config"));

        KMenuItem pluginsDirectoryMenuItem = new KMenuItem("Plugins Folder");
        pluginsDirectoryMenuItem.addActionListener(actionEvent -> makeFolder(modpack.getRootPath(), "plugins"));

        addMissingMenu.add(modsDirectoryMenuItem);
        addMissingMenu.add(resourcePacksDirectoryMenuItem);
        addMissingMenu.add(shaderPacksDirectoryMenuItem);
        addMissingMenu.add(configDirectoryMenuItem);
        addMissingMenu.add(pluginsDirectoryMenuItem);

        editMenu.add(addMissingMenu);

        return editMenu;
    }

    private KMenu getToolMenu() {
        KMenu toolMenu = new KMenu("Tool");

        KMenuItem refreshMenuItem = new KMenuItem(new RefreshPackAction(this));
        toolMenu.add(refreshMenuItem);

        KMenuItem updateAllMenuItem = new KMenuItem(new UpdateAllAction(this));
        toolMenu.add(updateAllMenuItem);

        KMenuItem devServerMenuItem = new KMenuItem("Run Development Server...");
        devServerMenuItem.setMnemonic(KeyEvent.VK_D);
        toolMenu.add(devServerMenuItem);
        devServerMenuItem.addActionListener(actionEvent -> {
            DevServerFrame frame = new DevServerFrame(this);
            frame.setVisible(true);
        });

        KMenuItem pwConsoleMenuItem = new KMenuItem("Open Packwiz Console");
        pwConsoleMenuItem.setMnemonic(KeyEvent.VK_O);
        toolMenu.add(pwConsoleMenuItem);
        pwConsoleMenuItem.addActionListener(actionEvent -> openPackwizConsole());

        return toolMenu;
    }

    private void openPackwizConsole() {
        ConsoleDialog frame = new ConsoleDialog(Main.packwiz, this);
        frame.setVisible(true);
    }

    private void makeFolder(Path path, String dir) {
        Path finalPath = path.resolve(dir);
        if(finalPath.toFile().exists()) {
            JOptionPane.showMessageDialog(this, String.format("Folder \"%s\" already exists!", dir), Util.withTitlePrefix("Folder Already Exists!"), JOptionPane.INFORMATION_MESSAGE);
        } else {
            try {
                Files.createDirectory(finalPath);
                JOptionPane.showMessageDialog(this, String.format("Created folder \"%s\"!", dir), Util.withTitlePrefix("Folder Created!"), JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                Main.LOGGER.exception(e);
                JOptionPane.showMessageDialog(this, String.format("Failed to create folder \"%s\":\n%s", dir, e.getMessage()), Util.withTitlePrefix("Failed to Create Folder"), JOptionPane.ERROR_MESSAGE);
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
