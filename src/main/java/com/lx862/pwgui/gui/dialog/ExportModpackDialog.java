package com.lx862.pwgui.gui.dialog;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.lx862.pwgui.core.Modpack;
import com.lx862.pwgui.executable.Executables;
import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.data.IconNamePair;
import com.lx862.pwgui.gui.components.kui.KFileChooser;
import com.lx862.pwgui.executable.ProgramExecution;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ExportModpackDialog extends JDialog {
    private final KButton exportButton;

    public ExportModpackDialog(JFrame parentFrame, Modpack modpack) {
        super(parentFrame, Util.withTitlePrefix("Export Modpack"), true);

        setSize(380, 300);
        setLocationRelativeTo(parentFrame);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Export Modpack");
        titleLabel.setFont(FlatUIUtils.nonUIResource(UIManager.getFont("h2.font")));
        rootPanel.add(titleLabel, BorderLayout.NORTH);

        JTabbedPane formatTabPane = new JTabbedPane();
        formatTabPane.setBorder(new EmptyBorder(10, 0, 10, 0));
        formatTabPane.addTab(IconNamePair.MODRINTH.name, new ImageIcon(GUIHelper.clampImageSize(IconNamePair.MODRINTH.image, 20)), new ModrinthExportPanel());
        formatTabPane.addTab(IconNamePair.CURSEFORGE.name, new ImageIcon(GUIHelper.clampImageSize(IconNamePair.CURSEFORGE.image, 20)), new CurseforgeExportPanel(this::setExportButtonState));

        formatTabPane.addChangeListener(changeEvent -> {
            ExportPanel selectedTab = (ExportPanel)formatTabPane.getComponentAt(formatTabPane.getSelectedIndex());
            setExportButtonState(selectedTab.canExport());
        });

        exportButton = new KButton("Export!");
        exportButton.addActionListener(actionEvent -> {
            ExportPanel selectedTab = (ExportPanel)formatTabPane.getComponentAt(formatTabPane.getSelectedIndex());

            KFileChooser fileChooser = new KFileChooser("export-modpack");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Modpack file", selectedTab.getExtension().substring(1)));
            fileChooser.setDialogTitle("Choose Modpack Saving Location");
            fileChooser.setSelectedFile(new File(modpack.packFile.get().name + selectedTab.getExtension()));
            if(fileChooser.openSaveAsDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                List<String> args = new ArrayList<>(selectedTab.getArguments());
                args.add("--output");
                args.add(file.getPath());

                exportModpack(args, file);
            }
        });

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionRow.add(exportButton);

        rootPanel.add(actionRow, BorderLayout.SOUTH);

        rootPanel.add(formatTabPane);
        add(rootPanel);
    }

    private void exportModpack(List<String> args, File destination) {
        ProgramExecution programRefresh = Executables.packwiz.buildCommand("refresh");
        programRefresh.whenExit(refreshExitCode -> {
            if(refreshExitCode != 0) return;

            ProgramExecution program = Executables.packwiz.buildCommand(args.toArray(new String[0]));
            ExecutableProgressDialog dialog = new ExecutableProgressDialog(this, "Exporting Modpack...", Constants.REASON_TRIGGERED_BY_USER, program);
            Util.addManualDownloadPrompt(this, program, dialog, () -> {
                exportModpack(args, destination);
            });
            program.whenExit(exitCode -> {
                if(exitCode == 0) {
                    new FileSavedDialog(this, "Modpack Exported!", destination).setVisible(true);
                }
            });
            dialog.setVisible(true);
        });
        new ExecutableProgressDialog(this, "Refreshing Modpack...", "Refresh before export to ensure consistency.", programRefresh).setVisible(true);
    }

    private void setExportButtonState(boolean active) {
        exportButton.setEnabled(active);
    }
}

class ModrinthExportPanel extends ExportPanel {
    private final JCheckBox restrictDomainCheckBox;

    public ModrinthExportPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JLabel formatLabel = new JLabel("<html>Format: <b>" + getExtension() + "</b></html>");
        formatLabel.setBorder(new EmptyBorder(4, 0, 4, 0));
        add(formatLabel);

        restrictDomainCheckBox = new JCheckBox("Restricts domains to those allowed by modrinth.com");
        restrictDomainCheckBox.setSelected(true);

        add(restrictDomainCheckBox);
    }

    @Override
    public List<String> getArguments() {
        List<String> args = new ArrayList<>();
        args.add("modrinth");
        args.add("export");
        args.add("--restrictDomains=" + (restrictDomainCheckBox.isSelected() ? "true" : "false"));
        return args;
    }

    @Override
    public boolean canExport() {
        return true;
    }

    @Override
    public String getExtension() {
        return ".mrpack";
    }
}

class CurseforgeExportPanel extends ExportPanel {
    private final JCheckBox exportClientCheckBox;
    private final JCheckBox exportServerCheckBox;
    private final Consumer<Boolean> setExportButtonState;

    public CurseforgeExportPanel(Consumer<Boolean> setExportButtonState) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setExportButtonState = setExportButtonState;

        JLabel formatLabel = new JLabel("<html>Format: <b>" + getExtension() + "</b></html>");
        formatLabel.setBorder(new EmptyBorder(4, 0, 4, 0));
        formatLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(formatLabel);

        JPanel sidesPanel = new JPanel();
        sidesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        sidesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sidesLabel = new JLabel("Sides:");
        sidesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidesPanel.add(sidesLabel);

        exportClientCheckBox = new JCheckBox("Client");
        exportClientCheckBox.addActionListener(actionEvent -> updateExportButtonState());
        exportClientCheckBox.setSelected(true);
        sidesPanel.add(exportClientCheckBox);

        exportServerCheckBox = new JCheckBox("Server");
        exportServerCheckBox.addActionListener(actionEvent -> updateExportButtonState());
        exportServerCheckBox.setSelected(false);
        sidesPanel.add(exportServerCheckBox);

        add(sidesPanel);
    }

    private void updateExportButtonState() {
        setExportButtonState.accept(canExport());
    }

    @Override
    public boolean canExport() {
        return exportClientCheckBox.isSelected() || exportServerCheckBox.isSelected();
    }

    @Override
    public List<String> getArguments() {
        List<String> args = new ArrayList<>();
        args.add("curseforge");
        args.add("export");

        if(exportServerCheckBox.isSelected() && exportClientCheckBox.isSelected()) {
            args.add("--side=both");
        } else if(exportClientCheckBox.isSelected()) {
            args.add("--side=client");
        } else {
            args.add("--side=server");
        }
        return args;
    }

    @Override
    public String getExtension() {
        return ".zip";
    }
}

abstract class ExportPanel extends JPanel {
    public abstract boolean canExport();
    public abstract String getExtension();
    public abstract List<String> getArguments();
}