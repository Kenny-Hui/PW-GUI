package com.lx862.pwgui.gui;

import com.lx862.pwgui.core.Modpack;
import com.lx862.pwgui.gui.base.kui.KButton;
import com.lx862.pwgui.gui.popup.NewModpackDialog;
import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.gui.base.BaseFrame;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class WelcomeFrame extends BaseFrame {
    public WelcomeFrame(Component parent) {
        super(String.format("Welcome to %s!", Constants.PROGRAM_NAME));
        Main.packwiz.setPackFileLocation(null);

        setSize(400, 525);
        setLocationRelativeTo(parent);

        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.PAGE_AXIS));

        JLabel logoLabel;
        try {
            logoLabel = new JLabel(new ImageIcon(GUIHelper.convertImage(Util.getAssets("/logo.png"), 250)));
        } catch (Exception e) {
            logoLabel = new JLabel(Constants.PROGRAM_NAME);
            logoLabel.setFont(UIManager.getFont("h1.font"));
            e.printStackTrace();
        }

        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rootPanel.add(Box.createVerticalGlue());
        rootPanel.add(logoLabel);

        rootPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        JLabel versionLabel = new JLabel(String.format("Version %s", Constants.VERSION));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rootPanel.add(versionLabel);
        rootPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        KButton openPackButton = new KButton("Open modpack...");
        openPackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rootPanel.add(openPackButton);

        openPackButton.addActionListener(actionEvent -> {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if(file.isDirectory()) return true;
                    if(file.getName().endsWith(".pw.toml")) return false;

                    // index.toml, but we also have pack.toml in the same directory, so we hide it to avoid confusion, as it's very likely it's pack.toml in this case.
                    if(file.getName().equals("index.toml") && Files.exists(file.toPath().getParent().resolve("pack.toml"))) return false;
                    if(file.getName().endsWith(".toml")) return true; // Packwiz supports filename other than pack.toml, so we don't only whitelist pack.toml.

                    return false;
                }

                @Override
                public String getDescription() {
                    return "Packwiz Pack File (pack.toml by default)";
                }
            });
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                openModpack(file.toPath());
            }
        });

        rootPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        KButton createPackButton = new KButton("Create new modpack...");
        createPackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createPackButton.addActionListener(actionEvent -> {
            new NewModpackDialog(this, this::openModpack).setVisible(true);
        });
        rootPanel.add(createPackButton);

        rootPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        KButton settingsButton = new KButton("Settings");
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rootPanel.add(settingsButton);

        rootPanel.add(Box.createVerticalGlue());

        add(rootPanel);
    }

    private void openModpack(Path path) {
        try {
            Modpack modpack = new Modpack(path);
            dispose();
            EditFrame editFrame = new EditFrame(this, modpack);
            editFrame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, String.format("Failed to open modpack: %s", e.getMessage()), Util.withTitlePrefix("Open modpack"), JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        Main.packwiz.dispose();
        Main.git.dispose();
    }
}
