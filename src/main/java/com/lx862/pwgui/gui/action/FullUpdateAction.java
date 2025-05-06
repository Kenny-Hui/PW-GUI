package com.lx862.pwgui.gui.action;

import com.lx862.pwgui.PWGUI;
import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.pwcore.*;
import com.lx862.pwgui.executable.BatchedProgramExecution;
import com.lx862.pwgui.executable.Executables;
import com.lx862.pwgui.executable.ProgramExecution;
import com.lx862.pwgui.gui.dialog.BatchedExecutionProgressDialog;
import com.lx862.pwgui.gui.dialog.ExecutableProgressDialog;
import com.lx862.pwgui.gui.dialog.IncompatibleSummaryDialog;
import com.lx862.pwgui.util.Util;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FullUpdateAction extends UpdateAction {
    private final PackFile packFile;

    public FullUpdateAction(Window parent, PackFile packFile) {
        super(parent);
        this.packFile = packFile;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ProgramExecution updateExecution = getProgramExecution();

        PackIndexFile packIndex = packFile.packIndexFile.get();
        List<PackIndexFile.FileEntry> originalEntries = packIndex.getFileEntries().stream().filter(f -> f.metafile).toList();

        updateExecution.onExit(exitCode -> {
            if(exitCode == 0) {
                if(!alreadyUpToDate.get() && !modsUpdated.get()) {
                    JOptionPane.showMessageDialog(parent, "Update cancelled, no changes were made.", Util.withTitlePrefix("Update Cancelled!"), JOptionPane.INFORMATION_MESSAGE);
                } else {
                    packFile.packIndexFile.clearCache();
                    PackIndexFile newPackIndex = packFile.packIndexFile.get();
                    List<PackIndexFile.FileEntry> unchangedEntries = newPackIndex.getFileEntries().stream()
                            .filter(f -> f.metafile)
                            .filter(f -> originalEntries.stream().anyMatch(g -> Objects.equals(f.hash, g.hash)))
                            .toList();

                    List<PackwizMetaFile> filesWithoutSuitableVersion = new ArrayList<>();

                    Path tempDirectory = packFile.resolveRelative(".pwgui-tmp");

                    try {
                        Files.createDirectories(tempDirectory);
                    } catch (Exception ex) {
                        PWGUI.LOGGER.exception(ex);
                        JOptionPane.showMessageDialog(parent, "Cannot create a temporary folder for version compatibility checking!\nSome content may not have a version that supports the current modloader/minecraft version.", Util.withTitlePrefix("Compatibility checking failed!"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    BatchedProgramExecution batchedProgramExecution = new BatchedProgramExecution();
                    for(PackIndexFile.FileEntry entry : unchangedEntries) {
                        PackwizMetaFile packwizMetaFile = new PackwizMetaFile(entry.path);
                        if(packwizMetaFile.pinned || !packwizMetaFile.haveUpdateSource()) continue;

                        // We ignore github updater for now as there's no reliable way to version check them
                        if(packwizMetaFile.updateGhSlug != null) continue;

                        String prefix = packwizMetaFile.updateMrVersion != null ? "mr" : "cf";
                        ProgramExecution execution;
                        if(prefix.equals("mr")) {
                            execution = Executables.packwiz.buildCommand(prefix, "add", packwizMetaFile.updateMrModId, "--meta-folder", tempDirectory.getFileName().toString());
                        } else {
                            execution = Executables.packwiz.buildCommand(prefix, "add", "--addon-id", String.valueOf(packwizMetaFile.updateCfProjectId), "--meta-folder", tempDirectory.getFileName().toString());
                        }

                        execution.whenStdout(stdout -> {
                            if(stdout.isQuestion()) execution.enterInput("N");

                            if(stdout.content().contains("failed to get latest version: no valid versions found") || stdout.content().contains("mod not available for the configured Minecraft version(s)")) {
                                filesWithoutSuitableVersion.add(packwizMetaFile);
                            }
                        });
                        batchedProgramExecution.add(execution);

                        // Try installing these mod to a temp directory. If it succeeds, it supports our new modpack configuration!
                        PWGUI.LOGGER.info(entry.file + " is unchanged, adding for check");
                    }

                    batchedProgramExecution.onFinish(programErrored -> {
                        PWGUI.LOGGER.info(String.format("Found %s incompatible item(s) under the current modpack configuration.", filesWithoutSuitableVersion.size()));

                        try {
                            FileUtils.deleteDirectory(tempDirectory.toFile());
                        } catch (Exception ex) {
                            PWGUI.LOGGER.exception(ex);
                        }

                        if(filesWithoutSuitableVersion.isEmpty()) {
                            JOptionPane.showMessageDialog(parent, "Everything up to date!", Util.withTitlePrefix("Update Success!"), JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            new IncompatibleSummaryDialog(parent, filesWithoutSuitableVersion).setVisible(true);
                        }

                        Executables.packwiz.refresh().execute("Clean-up after content compatibility check");
                    });

                    BatchedExecutionProgressDialog modCompatDialog = new BatchedExecutionProgressDialog(parent, "Checking content compatibility...", "Check content compatibility after update", batchedProgramExecution);
                    modCompatDialog.setVisible(true);
                }
            }
        });
        ExecutableProgressDialog updateProgressDialog = new ExecutableProgressDialog(parent, "Checking for update...", Constants.REASON_TRIGGERED_BY_USER, updateExecution);
        updateProgressDialog.setVisible(true);
    }
}
