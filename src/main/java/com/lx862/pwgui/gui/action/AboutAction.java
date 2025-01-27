package com.lx862.pwgui.gui.action;

import com.lx862.pwgui.gui.dialog.AboutDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class AboutAction extends AbstractAction {
    private final Window parent;

    public AboutAction(Window parent) {
        super("About");
        this.parent = parent;
        putValue(MNEMONIC_KEY, KeyEvent.VK_A);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new AboutDialog(parent).setVisible(true);
    }
}
