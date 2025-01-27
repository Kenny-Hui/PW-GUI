package com.lx862.pwgui.gui.popup;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.data.ApplicationTheme;
import com.lx862.pwgui.executable.Executables;
import com.lx862.pwgui.gui.action.ResetProgramAction;
import com.lx862.pwgui.gui.components.filter.PackwizExecutableFileFilter;
import com.lx862.pwgui.gui.components.kui.*;
import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Path;

public class SettingsDialog extends JDialog {
    private final JCheckBox relaunchModpackCheckbox;
    private final JCheckBox debugModeCheckBox;
    private final KComboBox<ApplicationTheme> themeComboBox;
    private final JLabel packwizLocationLabel;

    private final ApplicationTheme initialTheme;
    private Path packwizLocationPath;
    private boolean saved;

    public SettingsDialog(Window parent) {
        super(parent, Util.withTitlePrefix("Settings"), ModalityType.DOCUMENT_MODAL);

        this.initialTheme = Main.getConfig().getApplicationTheme();

        setSize(400, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        KGridBagLayoutPanel settingsPanel = new KGridBagLayoutPanel(0, 1);

        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(FlatUIUtils.nonUIResource(UIManager.getFont("h2.font")));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0)); // Bottom padding to compensate
        rootPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel programPanel = new JPanel();
        programPanel.setLayout(new BoxLayout(programPanel, BoxLayout.PAGE_AXIS));
        programPanel.setBorder(BorderFactory.createTitledBorder(Constants.PROGRAM_NAME));

        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        themePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        themePanel.add(new JLabel("Theme:"));


        this.themeComboBox = new KComboBox<>();
        this.themeComboBox.setRenderer(new KListCellRenderer());
        for(ApplicationTheme applicationTheme : ApplicationTheme.values()) {
            themeComboBox.addItem(applicationTheme);
        }
        themeComboBox.setSelectedItem(initialTheme);

        themeComboBox.addItemListener(actionEvent -> {
            ApplicationTheme t = (ApplicationTheme)themeComboBox.getSelectedItem();
            GUIHelper.setupApplicationTheme(t, this);
        });
        themePanel.add(themeComboBox);
        programPanel.add(themePanel);

        programPanel.add(GUIHelper.createVerticalPadding(4));

        relaunchModpackCheckbox = new JCheckBox("Open last modpack on launch");
        relaunchModpackCheckbox.setSelected(Main.getConfig().openLastModpackOnLaunch());
        this.relaunchModpackCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        programPanel.add(relaunchModpackCheckbox);

        this.debugModeCheckBox = new JCheckBox("Enable Debug Log");
        debugModeCheckBox.setSelected(Main.getConfig().getDebugMode());
        debugModeCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        programPanel.add(debugModeCheckBox);

        programPanel.add(GUIHelper.createVerticalPadding(4));

        KButton resetButton = new KButton(new ResetProgramAction(this, parent));
        resetButton.setAlignmentX(Component.LEFT_ALIGNMENT);
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
        boolean located = Executables.packwiz.probe(null) != null;
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
        this.saved = true;
        Main.getConfig().setApplicationTheme((ApplicationTheme) themeComboBox.getSelectedItem());
        Main.getConfig().setOpenLastModpackOnLaunch(relaunchModpackCheckbox.isSelected());
        Main.getConfig().setDebugMode(debugModeCheckBox.isSelected());
        Main.getConfig().setPackwizExecutablePath(packwizLocationPath);
        Executables.packwiz.updateExecutableLocation(null);

        try {
            Main.getConfig().write(Constants.REASON_TRIGGERED_BY_USER);
            dispose();
        } catch (IOException e) {
            Main.LOGGER.exception(e);
            JOptionPane.showMessageDialog(this, String.format("Failed to save config:\n%s", e.getMessage()), Util.withTitlePrefix("Config Saving Failed!"), JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void dispose() {
        if(!saved) {
            GUIHelper.setupApplicationTheme(initialTheme, null);
        } else {
            GUIHelper.setupApplicationTheme((ApplicationTheme) themeComboBox.getSelectedItem(), null);
        }
        super.dispose();
    }
}
