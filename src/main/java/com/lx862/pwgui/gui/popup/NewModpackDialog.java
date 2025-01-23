package com.lx862.pwgui.gui.popup;

import com.lx862.pwgui.gui.base.kui.KButton;
import com.lx862.pwgui.gui.dialog.ExecutableProgressDialog;
import com.lx862.pwgui.gui.panel.ModpackInfoPanel;
import com.lx862.pwgui.gui.panel.ModpackVersionPanel;
import com.lx862.pwgui.util.Util;
import org.apache.commons.io.FileUtils;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.gui.base.kui.KSeparator;
import com.lx862.pwgui.executable.ProgramExecution;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class NewModpackDialog extends JDialog {
    public NewModpackDialog(JFrame frame, Consumer<Path> packCreatedCallback) {
        super(frame, Util.withTitlePrefix("New Modpack"), true);

        setSize(400, 525);
        setLocationRelativeTo(frame);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());
        getRootPane().setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("New Modpack...");
        titleLabel.setFont(UIManager.getFont("h1.font"));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0)); // Bottom padding to compensate
        add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tab = new JTabbedPane();

        JPanel createPanel = new JPanel(new BorderLayout());
        JPanel createFormPanel = new JPanel();
        createFormPanel.setLayout(new BoxLayout(createFormPanel, BoxLayout.Y_AXIS));

        JLabel desc = new JLabel("Please fill the basic info about your (soon to be!) modpack.");
        desc.setBorder(new EmptyBorder(8, 8, 8, 8));
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        createFormPanel.add(desc);

        ModpackInfoPanel modpackInfoPanel = new ModpackInfoPanel(null);
        ModpackVersionPanel modpackVersionPanel = new ModpackVersionPanel(null);
        createFormPanel.add(modpackInfoPanel);
        createFormPanel.add(new KSeparator());
        createFormPanel.add(modpackVersionPanel);
        createFormPanel.add(Box.createVerticalGlue());
        createFormPanel.add(new KSeparator());
        createPanel.add(createFormPanel, BorderLayout.CENTER);

        JPanel actionRowPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        KButton saveButton = new KButton("Create & Save!");
        saveButton.addActionListener(actionEvent -> {
            try {
                createModpack(modpackInfoPanel, modpackVersionPanel, (path) -> {
                    doAftermath(path, modpackVersionPanel.getModloader() != null);
                    packCreatedCallback.accept(path.resolve("pack.toml"));
                });
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to create modpack:\n" + e.getMessage(), Util.withTitlePrefix("Create Modpack"), JOptionPane.ERROR_MESSAGE);
            }
        });
        actionRowPanel.add(saveButton);
        createPanel.add(actionRowPanel, BorderLayout.SOUTH);

        tab.add("Create", createPanel);
        tab.add("Import", new ImportModpackDialog.ImportModpackPanel(true, packCreatedCallback));
        add(tab, BorderLayout.CENTER);
    }

    private void createModpack(ModpackInfoPanel modpackInfoPanel, ModpackVersionPanel modpackVersionPanel, Consumer<Path> finishCallback) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Choose a folder to store your modpack in...");

        if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File directory = fileChooser.getSelectedFile();
            String cleanFilesystemName = modpackInfoPanel.getModpackName().replaceAll("(?U)[^\\w\\._]+", "_"); // https://stackoverflow.com/questions/1155107/is-there-a-cross-platform-java-method-to-remove-filename-special-chars
            File modpackDirectory = directory.toPath().resolve(cleanFilesystemName).toFile();
            if(Files.exists(modpackDirectory.toPath())) {
                if(modpackDirectory.isFile()) {
                    JOptionPane.showMessageDialog(this, "A file with name \"" + modpackDirectory.getName() + "\" already exists!\nConsider removing/renaming the file.", Util.withTitlePrefix("Create Modpack"), JOptionPane.ERROR_MESSAGE);
                    createModpack(modpackInfoPanel, modpackVersionPanel, finishCallback);
                    return;
                } else if(modpackDirectory.list().length > 0) {
                    // Note: We use "folder already exists" because it's easier to get by. If a user have an empty folder, this won't prompt because nothing the user do will be loss.
                    if(JOptionPane.showConfirmDialog(this, "\"" + modpackDirectory.getName() + "\" folder already exists!\nDo you want to use that folder for your Modpack anyway?\nWARNING: This would remove EVERYTHING within the folder!", Util.withTitlePrefix("Create Modpack"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        FileUtils.deleteDirectory(modpackDirectory);
                    } else {
                        createModpack(modpackInfoPanel, modpackVersionPanel, finishCallback);
                        return;
                    }
                }
            }
            modpackDirectory.mkdir();

            // Build arguments
            final List<String> arguments = new ArrayList<>();
            arguments.add(0, "init"); // Prepend our action
            arguments.addAll(modpackInfoPanel.getInitArguments());
            arguments.addAll(modpackVersionPanel.getInitArguments());

            String[] argsStr = arguments.toArray(new String[0]);

            Main.packwiz.changeWorkingDirectory(modpackDirectory.toPath());

            ProgramExecution processExecution = Main.packwiz.buildCommand(argsStr).whenExit(exitCode -> {
                if(exitCode == 0) {
                    dispose();
                    if(finishCallback != null) finishCallback.accept(modpackDirectory.toPath());
                }
            });
            new ExecutableProgressDialog(null, "Creating Modpack...", "Requested by user", processExecution).setVisible(true);
        }
    }

    /* We also add several files/folder on top of packwiz by default */
    private void doAftermath(Path path, boolean isModded) {
        if(isModded) {
            path.resolve("config").toFile().mkdir();
            path.resolve("mods").toFile().mkdir();
        }

        try {
            copyToPath("README.md", path);
            copyToPath("gitattributes", ".gitattributes", path);
            copyToPath("gitignore", ".gitignore", path);
            copyToPath(".packwizignore", path);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to copy additional files to the modpack.\nYou may need to manually create the \"mods\" and \"config\" folder if you want to install mods.", Util.withTitlePrefix("Create Modpack"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void copyToPath(String filename, Path path) throws IOException {
        copyToPath(filename, filename, path);
    }

    private void copyToPath(String filename, String outputName, Path path) throws IOException {
        try(InputStream is = Util.getAssets("/new_modpack_structure/" + filename)) {
            Files.copy(is, path.resolve(outputName), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
