package com.lx862.pwgui.gui.popup;

import com.lx862.pwgui.Main;
import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.gui.action.ResetProgramAction;
import com.lx862.pwgui.gui.components.filters.PackwizExecutableFileFilter;
import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.gui.components.kui.KFileChooser;
import com.lx862.pwgui.gui.components.kui.KGridBagLayoutPanel;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Path;

public class SettingsDialog extends JDialog {
    private final JCheckBox relaunchModpackCheckbox;
    private final JLabel packwizLocationLabel;
    private Path packwizLocationPath;

    public SettingsDialog(Window parent) {
        super(parent, Util.withTitlePrefix("Settings"), ModalityType.DOCUMENT_MODAL);

        setSize(400, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        KGridBagLayoutPanel settingsPanel = new KGridBagLayoutPanel(0, 1);

        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(UIManager.getFont("h2.font"));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0)); // Bottom padding to compensate
        rootPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel programPanel = new JPanel();
        programPanel.setLayout(new BoxLayout(programPanel, BoxLayout.PAGE_AXIS));
        programPanel.setBorder(BorderFactory.createTitledBorder(Constants.PROGRAM_NAME));

        this.relaunchModpackCheckbox = new JCheckBox("Open last modpack on launch");
        this.relaunchModpackCheckbox.setSelected(Main.getConfig().openLastModpackOnLaunch());
        programPanel.add(relaunchModpackCheckbox);

        KButton resetButton = new KButton(new ResetProgramAction(this, parent));
        programPanel.add(resetButton);

        JPanel packwizPanel = new JPanel();
        packwizPanel.setLayout(new BoxLayout(packwizPanel, BoxLayout.PAGE_AXIS));
        packwizPanel.setBorder(BorderFactory.createTitledBorder("Packwiz"));

        KGridBagLayoutPanel packwizLocationPanel = new KGridBagLayoutPanel(3, 2);
        this.packwizLocationLabel = new JLabel("Location: ???");

        KButton changePackwizLocationButton = new KButton("Change...");
        changePackwizLocationButton.addActionListener(actionEvent -> {
            KFileChooser fileChooser = new KFileChooser("locate-pw");
            fileChooser.setFileFilter(new PackwizExecutableFileFilter());

            if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                updatePackwizPath(fileChooser.getSelectedFile().toPath());
            }
        });

        packwizLocationPanel.addRow(1, 0, packwizLocationLabel, changePackwizLocationButton);

        packwizPanel.add(packwizLocationPanel);

        settingsPanel.addRow(1, programPanel);
        settingsPanel.addRow(1, packwizPanel);
        settingsPanel.addVerticalFiller();

        rootPanel.add(settingsPanel, BorderLayout.CENTER);

        JPanel actionRowPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        KButton saveButton = new KButton("Save");
        saveButton.setMnemonic(KeyEvent.VK_S);
        saveButton.addActionListener(actionEvent -> save());
        actionRowPanel.add(saveButton);

        rootPanel.add(actionRowPanel, BorderLayout.SOUTH);
        add(rootPanel);

        updatePackwizPath(Main.getConfig().getPackwizExecutablePath());
    }

    private void updatePackwizPath(Path newPath) {
        Path oldPath = Main.getConfig().getPackwizExecutablePath();

        Main.getConfig().setPackwizExecutablePath(newPath);
        boolean located = Main.packwiz.probe(null) != null;
        Main.getConfig().setPackwizExecutablePath(oldPath); // restore

        if(!located) {
            JOptionPane.showMessageDialog(this, "The specified packwiz executable is not valid!", Util.withTitlePrefix("Invalid Executable"), JOptionPane.ERROR_MESSAGE);
        } else {
            packwizLocationLabel.setText(String.format("Location: %s", newPath.toString()));
            packwizLocationLabel.setToolTipText(newPath.toString());
            this.packwizLocationPath = newPath;
        }
    }

    private void save() {
        Main.getConfig().setOpenLastModpackOnLaunch(relaunchModpackCheckbox.isSelected());
        Main.getConfig().setPackwizExecutablePath(packwizLocationPath);
        Main.packwiz.updateExecutableLocation(null);

        try {
            Main.getConfig().write(Constants.REASON_TRIGGERED_BY_USER);
            dispose();
        } catch (IOException e) {
            Main.LOGGER.exception(e);
            JOptionPane.showMessageDialog(this, String.format("Failed to save config:\n%s", e.getMessage()), Util.withTitlePrefix("Config Saving Failed!"), JOptionPane.ERROR_MESSAGE);
        }
    }
}
