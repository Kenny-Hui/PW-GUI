package com.lx862.pwgui.gui.frame;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.lx862.pwgui.core.Modpack;
import com.lx862.pwgui.gui.action.SettingsAction;
import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.gui.components.kui.KFileChooser;
import com.lx862.pwgui.gui.components.kui.KLinkButton;
import com.lx862.pwgui.gui.popup.NewModpackDialog;
import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/** The welcome splash screen after packwiz executable is found */
public class WelcomeFrame extends BaseFrame {
    public WelcomeFrame(Component parent) {
        super(String.format("Welcome to %s!", Constants.PROGRAM_NAME));
        Main.packwiz.setPackFileLocation(null);

        setSize(400, 525);
        setLocationRelativeTo(parent);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.add(Box.createVerticalGlue());

        JLabel logoLabel;
        try {
            logoLabel = new JLabel(new ImageIcon(GUIHelper.convertImage(Util.getAssets("/logo.png"), 250)));
        } catch (Exception e) {
            logoLabel = new JLabel(Constants.PROGRAM_NAME);
            logoLabel.setFont(FlatUIUtils.nonUIResource(UIManager.getFont("h1.font")));
            Main.LOGGER.exception(e);
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(logoLabel);

        JLabel versionLabel = new JLabel(String.format("Version %s", Constants.VERSION));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(versionLabel);

        mainPanel.add(GUIHelper.createVerticalPadding(16));

        KButton openPackButton = new KButton("Open modpack...");
        openPackButton.setMnemonic(KeyEvent.VK_O);
        openPackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(openPackButton);

        openPackButton.addActionListener(actionEvent -> {
            KFileChooser fileChooser = new KFileChooser("open-modpack");
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
                openModpack(fileChooser.getSelectedFile().toPath());
            }
        });

        mainPanel.add(GUIHelper.createVerticalPadding(8));

        KButton createPackButton = new KButton("Create new modpack...");
        createPackButton.setMnemonic(KeyEvent.VK_C);
        createPackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createPackButton.addActionListener(actionEvent -> showNewModpackDialog());
        mainPanel.add(createPackButton);

        mainPanel.add(GUIHelper.createVerticalPadding(8));

        KButton settingsButton = new KButton(new SettingsAction(this));
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(settingsButton);

        mainPanel.add(Box.createVerticalGlue());

        rootPanel.add(mainPanel, BorderLayout.CENTER);

        JPanel actionRowPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionRowPanel.add(new KLinkButton("Discord", Constants.LINK_DISCORD));
        actionRowPanel.add(new KLinkButton("GitHub", Constants.LINK_GITHUB));
        rootPanel.add(actionRowPanel, BorderLayout.SOUTH);

        add(rootPanel);

        this.jMenuBar.add(super.getHelpMenu());

        Main.getConfig().setLastModpackPath(null);
    }

    private void showNewModpackDialog() {
        new NewModpackDialog(this, this::openModpack).setVisible(true);
    }

    private void openModpack(Path path) {
        try {
            Modpack modpack = new Modpack(path);
            EditFrame editFrame = new EditFrame(this, modpack);
            editFrame.setVisible(true);
            dispose();
        } catch (Exception e) {
            Main.LOGGER.exception(e);
            JOptionPane.showMessageDialog(this, String.format("Failed to open modpack:\n%s", e.getMessage()), Util.withTitlePrefix("Failed to open Modpack"), JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        Main.packwiz.dispose();
        Main.git.dispose();
    }
}
