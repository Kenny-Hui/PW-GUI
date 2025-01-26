package com.lx862.pwgui.gui.action;

import com.lx862.pwgui.Main;
import com.lx862.pwgui.gui.components.filters.PackwizExecutableFileFilter;
import com.lx862.pwgui.gui.frame.WelcomeFrame;
import com.lx862.pwgui.gui.components.kui.KFileChooser;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class LocatePackwizAction extends AbstractAction {
    private final Window parent;

    public LocatePackwizAction(Window parent) {
        super("Locate packwiz...");
        this.parent = parent;
        putValue(MNEMONIC_KEY, KeyEvent.VK_L);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        KFileChooser fileChooser = new KFileChooser("locate-pw");
        fileChooser.setFileFilter(new PackwizExecutableFileFilter());

        if(fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if(!selectedFile.canExecute()) {
                JOptionPane.showMessageDialog(parent, "The selected file is not executable!\nConsider adding the executable (x) permission to the file.", Util.withTitlePrefix("File Not Excutable!"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            Main.getConfig().setPackwizExecutablePath(selectedFile.toPath());
            String newProbedPath = Main.packwiz.probe(null);
            if(newProbedPath == null) {
                JOptionPane.showMessageDialog(parent, "The selected executable is not valid!\nAre you sure you can run the executable?", Util.withTitlePrefix("Invalid Executable"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Main.getConfig().write("Update packwiz executable path");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, String.format("Failed to write configuration file:\n%s", e.getMessage()), Util.withTitlePrefix("Failed to Write Config"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            WelcomeFrame welcomeFrame = new WelcomeFrame(parent);
            welcomeFrame.setVisible(true);
            parent.dispose();
        }
    }
}
