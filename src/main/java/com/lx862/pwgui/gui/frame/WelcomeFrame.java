package com.lx862.pwgui.gui.frame;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.lx862.pwgui.PWGUI;
import com.lx862.pwgui.pwcore.Modpack;
import com.lx862.pwgui.executable.Executables;
import com.lx862.pwgui.gui.action.SettingsAction;
import com.lx862.pwgui.gui.components.filter.PackFileFilter;
import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.gui.components.kui.KFileChooser;
import com.lx862.pwgui.gui.popup.NewModpackDialog;
import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.core.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.nio.file.Path;

/** The welcome splash screen after packwiz executable is found */
public class WelcomeFrame extends BaseFrame {
    private static final int LOGO_SIZE = 250;

    public WelcomeFrame(Component parent) {
        super(String.format("Welcome to %s!", Constants.PROGRAM_NAME));
        Executables.packwiz.setPackFileLocation(null);

        setSize(400, 525);
        setLocationRelativeTo(parent);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.add(Box.createVerticalGlue());

        JLabel logoLabel;
        try {
            logoLabel = new JLabel(new ImageIcon(GUIHelper.convertImage(Util.getAssets("/assets/logo.png"), LOGO_SIZE), String.format("%s Logo", Constants.PROGRAM_NAME)));
        } catch (Exception e) {
            logoLabel = new JLabel(Constants.PROGRAM_NAME);
            logoLabel.setFont(FlatUIUtils.nonUIResource(UIManager.getFont("h1.font")));
            PWGUI.LOGGER.exception(e);
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
            fileChooser.setFileFilter(new PackFileFilter());
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

        add(rootPanel);

        this.jMenuBar.add(super.getHelpMenu());

        PWGUI.getConfig().setLastModpackPath(null);
    }

    private void showNewModpackDialog() {
        new NewModpackDialog(this, this::openModpack).setVisible(true);
    }

    private void openModpack(Path path) {
        try {
            Modpack modpack = new Modpack(path);
            EditFrame editFrame = new EditFrame(this, modpack);
            dispose();
            editFrame.setVisible(true);
        } catch (Exception e) {
            PWGUI.LOGGER.exception(e);
            JOptionPane.showMessageDialog(this, String.format("Failed to open modpack:\n%s", e.getMessage()), Util.withTitlePrefix("Failed to open Modpack"), JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        Executables.packwiz.dispose();
        Executables.git.dispose();
    }
}
