package com.lx862.pwgui.gui.action;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class CloseWindowAction extends AbstractAction {
    private final Window parent;

    public CloseWindowAction(Window parent, boolean isCancel) {
        super(isCancel ? "Cancel" : "Close");
        putValue(MNEMONIC_KEY, KeyEvent.VK_C);
        this.parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        parent.dispose();
    }
}
