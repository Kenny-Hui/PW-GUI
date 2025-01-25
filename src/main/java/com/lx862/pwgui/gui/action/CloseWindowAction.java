package com.lx862.pwgui.gui.action;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class CloseWindowAction extends AbstractAction {
    private final Window window;

    public CloseWindowAction(Window window, boolean isCancel) {
        super(isCancel ? "Cancel" : "Close");
        putValue(MNEMONIC_KEY, KeyEvent.VK_C);
        this.window = window;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        window.dispose();
    }
}
