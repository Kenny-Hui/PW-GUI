package com.lx862.pwgui.gui.action;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class OKAction extends AbstractAction {
    private final Runnable callback;

    public OKAction(Runnable callback) {
        super("OK");
        this.callback = callback;
        putValue(MNEMONIC_KEY, KeyEvent.VK_O);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        callback.run();
    }
}
