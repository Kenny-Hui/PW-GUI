package com.lx862.pwgui.gui.action;

import com.lx862.pwgui.Main;
import com.lx862.pwgui.gui.frame.WelcomeFrame;
import com.lx862.pwgui.gui.components.kui.KFileChooser;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class LocatePackwizAction extends AbstractAction {
    private final JFrame parent;

    public LocatePackwizAction(JFrame parent) {
        super("Locate packwiz...");
        this.parent = parent;
        putValue(MNEMONIC_KEY, KeyEvent.VK_L);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        KFileChooser fileChooser = new KFileChooser("locate-pw");
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().equals("packwiz") || f.getName().equals("packwiz.exe");
            }

            @Override
            public String getDescription() {
                return "Packwiz Executable (packwiz/packwiz.exe)";
            }
        });

        if(fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if(!selectedFile.canExecute()) {
                JOptionPane.showMessageDialog(parent, "The selected file is not executable!\nConsider adding the executable (x) permission to the file.", Util.withTitlePrefix("File non-excutable"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            Main.getConfig().setPackwizExecutablePath(selectedFile.toPath());
            Main.packwiz.locate(null);
            if(!Main.packwiz.usable()) {
                JOptionPane.showMessageDialog(parent, "The selected executable is not valid!\nAre you sure you can run the executable?", Util.withTitlePrefix("Invalid executable"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Main.getConfig().write();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, String.format("Failed to write configuration file:\n%s", e.getMessage()), Util.withTitlePrefix("Failed to write config"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            WelcomeFrame welcomeFrame = new WelcomeFrame(parent);
            welcomeFrame.setVisible(true);
            parent.dispose();
        }
    }
}
