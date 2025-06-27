package com.lx862.pwgui.gui.action;

import com.lx862.pwgui.gui.dialog.ChangeLicenseDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.function.Supplier;

public class EditLicenseAction extends AbstractAction {
    private final Supplier<JFrame> getParent;
    private final File licenseFile;

    public EditLicenseAction(Supplier<JFrame> getParent, File licenseFile) {
        super("Change license...");
        this.getParent = getParent;
        this.licenseFile = licenseFile;
        putValue(MNEMONIC_KEY, KeyEvent.VK_C);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        new ChangeLicenseDialog(getParent.get(), licenseFile).setVisible(true);
    }
}
