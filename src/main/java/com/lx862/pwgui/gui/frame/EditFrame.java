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

import java.awt.*;
import java.awt.event.KeyEvent;

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
        this.jMenuBar.add(getToolMenu());
        this.jMenuBar.add(super.getHelpMenu());
    }

    private KMenu getFileMenu(Modpack modpack) {
        KMenu fileMenu = new KMenu("File");

        KMenuItem saveMenuItem = new KMenuItem("Save selected file");
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

    @Override
    public void dispose() {
        super.dispose();
        editPanel.dispose();
        Main.packwiz.dispose();
        Main.git.dispose();
    }
}
