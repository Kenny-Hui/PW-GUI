package com.lx862.pwgui.gui.action;

import com.lx862.pwgui.PWGUI;
import com.lx862.pwgui.executable.BatchedProgramExecution;
import com.lx862.pwgui.executable.Executables;
import com.lx862.pwgui.executable.ProgramExecution;
import com.lx862.pwgui.gui.prompt.BatchedExecutionProgressDialog;
import com.lx862.pwgui.pwcore.Modpack;
import com.lx862.pwgui.pwcore.PackFile;
import com.lx862.pwgui.pwcore.PackIndexFile;
import com.lx862.pwgui.pwcore.PackwizMetaFile;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class ReinstallAction extends AbstractAction {
    private final Window parent;
    private final Modpack modpack;

    public ReinstallAction(Window parent, Modpack modpack) {
        super("Reinstall...");
        this.parent = parent;
        this.modpack = modpack;
        putValue(MNEMONIC_KEY, KeyEvent.VK_R);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(JOptionPane.showConfirmDialog(parent, "This will reimport all packwiz metadata file. All user-made changes will be removed.\nAre you sure you want to continue?", Util.withTitlePrefix("Reinstall?"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            PackFile packFile = modpack.packFile.get();
            PackIndexFile indexFile = packFile.packIndexFile.get();
            List<PackwizMetaFile> metas = new ArrayList<>();

            for(PackIndexFile.FileEntry fileEntry : indexFile.getFileEntries()) {
                if(!fileEntry.metafile) continue;

                try {
                    PackwizMetaFile metaFile = new PackwizMetaFile(fileEntry.path);
                    metas.add(metaFile);
                } catch (Exception e) {
                    PWGUI.LOGGER.exception(e);
                    PWGUI.LOGGER.warn("Failed to parse meta file " + fileEntry.path + ", disregarding!");
                }
            }

            // Remove
            BatchedProgramExecution removeExecution = new BatchedProgramExecution();
            for(PackwizMetaFile packwizMetaFile : metas) {
                ProgramExecution programExecution = Executables.packwiz.buildCommand("remove", packwizMetaFile.getSlug());
                removeExecution.add(programExecution);
            }
            removeExecution.execute("Re-installation requested by user");

            // Add
            BatchedProgramExecution addExecution = new BatchedProgramExecution();
            for(PackwizMetaFile packwizMetaFile : metas) {
                String prefix = packwizMetaFile.updateMrVersion != null ? "mr" : packwizMetaFile.updateCfProjectId == -1 ? "url" : "cf";

                String metaFolder = modpack.getRootPath().relativize(packwizMetaFile.getPath().getParent()).toString();

                ProgramExecution execution;
                if(prefix.equals("mr")) {
                    execution = Executables.packwiz.buildCommand(prefix, "add", packwizMetaFile.updateMrModId, "--meta-folder", metaFolder, "--yes");
                } else if(prefix.equals("cf")) {
                    execution = Executables.packwiz.buildCommand(prefix, "add", "--addon-id", String.valueOf(packwizMetaFile.updateCfProjectId), "--meta-folder", metaFolder, "--yes");
                } else {
                    execution = Executables.packwiz.buildCommand(prefix, "add", packwizMetaFile.getSlug(), String.valueOf(packwizMetaFile.downloadUrl), "--meta-folder", metaFolder, "--yes");
                }

                addExecution.add(execution);
            }

            addExecution.onExit((success) -> {
                JOptionPane.showMessageDialog(parent, "Re-installation finished.");
            });

            BatchedExecutionProgressDialog batchedExecutionProgressDialog = new BatchedExecutionProgressDialog(parent, "Re-adding meta files", "Re-installation requested by user", addExecution);
            batchedExecutionProgressDialog.setVisible(true);
        }
    }
}
