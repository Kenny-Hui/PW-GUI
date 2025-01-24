package com.lx862.pwgui.gui.action;

import com.lx862.pwgui.Main;
import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.executable.ProgramExecution;
import com.lx862.pwgui.gui.dialog.ExecutableProgressDialog;
import com.lx862.pwgui.gui.dialog.UpdateSummaryDialog;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class UpdateAllAction extends AbstractAction {
    private final JFrame parent;

    public UpdateAllAction(JFrame parent) {
        super("Update all items");
        this.parent = parent;
        putValue(MNEMONIC_KEY, KeyEvent.VK_U);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ProgramExecution programExecution = Main.packwiz.buildCommand("update", "--all");
        List<String> updateMods = new ArrayList<>();
        List<String> skippedMods = new ArrayList<>();
        List<String> unsupportedMods = new ArrayList<>();
        AtomicBoolean startLogMods = new AtomicBoolean();
        AtomicBoolean alreadyUpToDate = new AtomicBoolean();
        AtomicBoolean modsUpdated = new AtomicBoolean();

        programExecution.whenStdout(line -> {
            if(line.startsWith("Updates found:")) {
                startLogMods.set(true);
            }

            if(line.startsWith("A supported update system for") && line.endsWith("cannot be found.")) {
                unsupportedMods.add(line.split("A supported update system for \"")[1].split("\" cannot be found\\.")[0]);
            }

            if(line.startsWith("Update skipped for pinned mod")) {
                skippedMods.add(line.split("Update skipped for pinned mod ")[1]);
            }

            if(line.equals("Do you want to update? [Y/n]: ")) {
                new UpdateSummaryDialog(parent, updateMods, skippedMods, unsupportedMods, (update) -> {
                    if(update) programExecution.enterInput("Y");
                    else programExecution.enterInput("N");
                }).setVisible(true);
            }

            if(line.equals("All files are up to date!")) {
                alreadyUpToDate.set(true);
            }

            if(line.equals("Do you want to update? [Y/n]: Files updated!")) {
                modsUpdated.set(true);
            }

            if(startLogMods.get() && line.contains(" -> ")) {
                updateMods.add(line);
            }
        });

        programExecution.whenExit(exitCode -> {
            if(exitCode == 0) {
                if(alreadyUpToDate.get()) {
                    JOptionPane.showMessageDialog(parent, "All files are already up to date!", Util.withTitlePrefix("Already up to date"), JOptionPane.INFORMATION_MESSAGE);
                } else if(modsUpdated.get()) {
                    JOptionPane.showMessageDialog(parent, "All files have been updated!", Util.withTitlePrefix("Update successful"), JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parent, "Update cancelled!", Util.withTitlePrefix("Update cancelled"), JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        ExecutableProgressDialog executableProgressDialog = new ExecutableProgressDialog(parent, "Checking for update...", Constants.REASON_TRIGGERED_BY_USER, programExecution);
        executableProgressDialog.setVisible(true);
    }
}
