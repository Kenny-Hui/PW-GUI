package com.lx862.pwgui.gui.panel;

import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.executable.ProgramExecution;
import com.lx862.pwgui.gui.base.kui.KButton;
import com.lx862.pwgui.gui.base.kui.KFileChooser;
import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.gui.dialog.ExecutableProgressDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

public class ImportModpackPanel extends JPanel {
    private final Consumer<Path> importCallback;

    public ImportModpackPanel(boolean isCreate, Consumer<Path> importCallback) {
        this.importCallback = importCallback;

        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JLabel title = new JLabel("Import from CurseForge", SwingConstants.CENTER);
        title.setFont(UIManager.getFont("h2.font"));
        title.setAlignmentX(CENTER_ALIGNMENT);
        add(title);

        JLabel description;
        if(isCreate) {
             description = new JLabel("<html>" +
                    "<div style='text-align: center;'>" +
                    "<br>" +
                    "<p>You can click the import button below to create a new packwiz modpack from a pre-existing CurseForge modpack</p>" +
                    "<br>" +
                    "<br>" +
                    "</div>" +
                    "</html>", SwingConstants.CENTER);
        } else {
            description = new JLabel("<html>" +
                    "<div style='text-align: center;'>" +
                    "<hr>" +
                    "<br>" +
                    "<p>You can click the import button below to import a pre-existing CurseForge modpack to the current modpack.</p>" +
                    "<br>" +
                    "<br>" +
                    "</div>" +
                    "</html>", SwingConstants.CENTER);
        }
        description.setAlignmentX(CENTER_ALIGNMENT);
        add(description);

        KButton importButton = new KButton("Import...");
        importButton.addActionListener(actionEvent -> importModpack(isCreate));
        importButton.setAlignmentX(CENTER_ALIGNMENT);
        add(importButton);
    }

    private void importModpack(boolean isCreate) {
        KFileChooser cfModpackFileChooser = new KFileChooser();
        cfModpackFileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().endsWith(".zip") || file.getName().equals("manifest.json");
            }

            @Override
            public String getDescription() {
                return "CurseForge Modpack (.zip / manifest.json)";
            }
        });

        if(cfModpackFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = cfModpackFileChooser.getSelectedFile();

            if(isCreate) {
                String[] selection = new String[]{"Continue"};
                JOptionPane.showOptionDialog(this, "Good! Now let's create a folder you want your modpack to be saved to.", Util.withTitlePrefix("Choose Save Folder"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, selection, selection[0]);

                KFileChooser modpackFileChooser = new KFileChooser();
                modpackFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if(modpackFileChooser.openOpenDialog(this, true) == JFileChooser.APPROVE_OPTION) {
                    File destinationPath = modpackFileChooser.getSelectedFile();
                    Main.packwiz.changeWorkingDirectory(destinationPath.toPath()); // Change directory to new folder

                    runImportCommand(file, () -> {
                        importCallback.accept(destinationPath.toPath().resolve("pack.toml"));
                    });
                }
            } else {
                runImportCommand(file, () -> importCallback.accept(null));
            }
        }
    }

    private void runImportCommand(File sourceFile, Runnable callback) {
        ProgramExecution programExecution = Main.packwiz.buildCommand("curseforge", "import", sourceFile.toString());
        programExecution.whenExit(exitCode -> {
            if(exitCode == 0) {
                callback.run();
            }
        });

        new ExecutableProgressDialog(null, "Importing Modpack...", Constants.REASON_TRIGGERED_BY_USER, programExecution).setVisible(true);
    }
}
