package com.lx862.pwgui.gui.dialog;

import com.lx862.pwgui.core.Modpack;
import com.lx862.pwgui.data.ManualModInfo;
import com.lx862.pwgui.gui.base.kui.KButton;
import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.data.IconNamePair;
import com.lx862.pwgui.gui.base.kui.KFileChooser;
import com.lx862.pwgui.executable.ProgramExecution;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ExportModpackDialog extends JDialog {
    private final KButton exportButton;

    public ExportModpackDialog(JFrame parentFrame, Modpack modpack) {
        super(parentFrame, Util.withTitlePrefix("Export Modpack"), true);
        setSize(380, 300);
        setLocationRelativeTo(parentFrame);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Export Modpack");
        titleLabel.setFont(UIManager.getFont("h2.font"));
        panel.add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tab = new JTabbedPane();
        tab.setBorder(new EmptyBorder(10, 0, 10, 0));
        tab.addTab(IconNamePair.MODRINTH.name, new ImageIcon(GUIHelper.resizeImage(IconNamePair.MODRINTH.image, 20)), new ModrinthExportPanel());
        tab.addTab(IconNamePair.CURSEFORGE.name, new ImageIcon(GUIHelper.resizeImage(IconNamePair.CURSEFORGE.image, 20)), new CurseforgeExportPanel(this::setExportButtonState));

        tab.addChangeListener(changeEvent -> {
            ExportPanel selectedTab = (ExportPanel)tab.getComponentAt(tab.getSelectedIndex());
            setExportButtonState(selectedTab.exportable());
        });

        exportButton = new KButton("Export!");
        exportButton.addActionListener(actionEvent -> {
            ExportPanel selectedTab = (ExportPanel)tab.getComponentAt(tab.getSelectedIndex());

            KFileChooser fileChooser = new KFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Modpack file", selectedTab.getExtension().substring(1)));
            fileChooser.setDialogTitle("Choose Modpack Saving Location");
            fileChooser.setSelectedFile(new File(modpack.packFile.get().name + selectedTab.getExtension()));
            if(fileChooser.openSaveAsDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                List<String> args = new ArrayList<>();
                args.addAll(selectedTab.getArguments());
                args.add("--output");
                args.add(file.getPath());

                exportModpack(parentFrame, args, file);
            }
        });

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionRow.add(exportButton);

        panel.add(actionRow, BorderLayout.SOUTH);

        panel.add(tab);
        add(panel);
    }

    private void exportModpack(JFrame parentFrame, List<String> args, File destination) {
        ProgramExecution programRefresh = Main.packwiz.buildCommand("refresh");
        programRefresh.whenExit(refreshExitCode -> {
            if(refreshExitCode != 0) return;

            List<ManualModInfo> manualDownloadMod = new ArrayList<>();
            AtomicBoolean captureManualDownloadMod = new AtomicBoolean();
            AtomicReference<String> cachePath = new AtomicReference<>();

            ProgramExecution program = Main.packwiz.buildCommand(args.toArray(new String[0]));
            program.whenStdout((line) -> {
               if(line.contains("and must be manually downloaded")) {
                   captureManualDownloadMod.set(true);
               } else if(line.contains("Once you have done so, place")) {
                   cachePath.set(line.split(", place these files in ")[1].split(" and re-run")[0]);
                   new ManualDownloadDialog(this, manualDownloadMod, (path) -> {
                       moveManualDownloadToCache(manualDownloadMod, path, Paths.get(cachePath.get()));
                       exportModpack(parentFrame, args, destination);
                   }).setVisible(true);
               } else if(captureManualDownloadMod.get()) {
                   // Format: Mod Display Name (mod_file_name.jar) from https://example.com

                   String[] split = line.split(" from http");
                   String[] splitForTheRest = split[0].split(" ");
                   String fileName = "pwgui_cannot_parse_filename.jar";
                   for(String word : splitForTheRest) {
                       if(word.startsWith("(") && word.endsWith(")")) { // We will take a blind shot that jar file don't have spaces...
                           fileName = word.substring(1, word.length()-1);
                           break;
                       }
                   }
                   String name = line.substring(0, line.indexOf(fileName)-2);
                   String url = "http" + split[1];

                   manualDownloadMod.add(new ManualModInfo(name, fileName, url));
               }
            });
            program.whenExit(exitCode -> {
                if(exitCode == 0) {
                    new FileSavedDialog(this, "Modpack exported!", destination).setVisible(true);
                }
            });
            ExecutableProgressDialog dialog = new ExecutableProgressDialog(parentFrame, "Exporting modpack", Constants.REASON_TRIGGERED_BY_USER, program);
            dialog.whenProgramErrored(() -> !captureManualDownloadMod.get()); // Mute exit code 1 error pop up if it's about manual download
            dialog.setVisible(true);
        });
        new ExecutableProgressDialog(parentFrame, "Refreshing modpack...", "Refresh before export to ensure consistency.", programRefresh).setVisible(true);
    }

    private void moveManualDownloadToCache(List<ManualModInfo> modList, Path sourceDirectory, Path cacheDirectory) {
        for(ManualModInfo modInfo : modList) {
            Path sourcePath = sourceDirectory.resolve(modInfo.fileName);
            Path destinationPath = cacheDirectory.resolve(modInfo.fileName);
            try {
                Main.LOGGER.info(String.format("Moving manually downloaded file from \"%s\" to \"%s\"", sourcePath, destinationPath));
                Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, String.format("An error occured while moving manually downloaded files:\n%s\nPlease move %s manually to %s", e.getMessage(), modInfo.fileName, cacheDirectory), Util.withTitlePrefix("Failed to handle manually installed mods"), JOptionPane.ERROR_MESSAGE);
            }
        }
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
    public boolean exportable() {
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

        JLabel sideLabel = new JLabel("Sides:");
        sideLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        sidePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        exportClientCheckBox = new JCheckBox("Client");
        exportClientCheckBox.addActionListener(actionEvent -> updateExportButtonState());
        exportClientCheckBox.setSelected(true);

        exportServerCheckBox = new JCheckBox("Server");
        exportServerCheckBox.addActionListener(actionEvent -> updateExportButtonState());
        exportServerCheckBox.setSelected(false);

        sidePanel.add(sideLabel);
        sidePanel.add(exportClientCheckBox);
        sidePanel.add(exportServerCheckBox);

        add(sidePanel);
    }

    private void updateExportButtonState() {
        setExportButtonState.accept(exportable());
    }

    @Override
    public boolean exportable() {
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
    public abstract boolean exportable();
    public abstract String getExtension();
    public abstract List<String> getArguments();
}