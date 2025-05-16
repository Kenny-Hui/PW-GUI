package com.lx862.pwgui.gui.action;

import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.executable.Executables;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicReference;

public class RefreshPackAction extends AbstractAction {
    private final Component parent;

    public RefreshPackAction(Component parent) {
        super("Refresh Pack Index");
        this.parent = parent;
        putValue(MNEMONIC_KEY, KeyEvent.VK_R);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AtomicReference<String> lastLine = new AtomicReference<>();

        Executables.packwiz.refresh()
            .onStdout((stdout) -> lastLine.set(stdout.content()))
            .onExit(exitCode -> {
                if(exitCode == 0) {
                    JOptionPane.showMessageDialog(parent, "Modpack index refreshed!", Util.withTitlePrefix("Refresh Modpack Index"), JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parent, String.format("Packwiz exited with exit code %d:\n%s", exitCode, lastLine.get()), Util.withTitlePrefix("Refresh Modpack Index"), JOptionPane.ERROR_MESSAGE);
                }
            })
        .execute(Constants.REASON_TRIGGERED_BY_USER);
    }
}
