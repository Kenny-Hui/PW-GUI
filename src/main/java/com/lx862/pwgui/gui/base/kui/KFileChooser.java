package com.lx862.pwgui.gui.base.kui;

import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;

public class KFileChooser extends JFileChooser {

    /** Open the Save As dialog. User will be prompted if the file would get overwritten */
    public int openSaveAsDialog(Component component) {
        int showDialogResult = showSaveDialog(component);
        if(showDialogResult == APPROVE_OPTION) {
            if(Files.exists(getSelectedFile().toPath())) {
                int replaceResult = JOptionPane.showConfirmDialog(component, "File \"" + getSelectedFile().getName() + "\" already exist,\nAre you sure you want to replace the file?", Util.withTitlePrefix("Replace file?"), JOptionPane.YES_NO_OPTION);
                if(replaceResult == JOptionPane.YES_OPTION) {
                    return APPROVE_OPTION;
                } else {
                    return openSaveAsDialog(component);
                }
            } else {
                return APPROVE_OPTION;
            }
        } else {
            return showDialogResult;
        }
    }

    public int openOpenDialog(Component component, boolean shouldBeEmpty) {
        int showDialogResult = showSaveDialog(component);
        if(showDialogResult == APPROVE_OPTION) {
            if(shouldBeEmpty && getSelectedFile().list().length > 0) {
                int replaceResult = JOptionPane.showConfirmDialog(component, "Hmm folder is not empty, are you sure this is what you want?\nAll operations that follows will be performed directly in the folder you chose.\nIf that's not your intent, click \"No\" and create a new folder.", Util.withTitlePrefix("Replace file?"), JOptionPane.YES_NO_OPTION);
                if(replaceResult != JOptionPane.YES_OPTION) {
                    return openOpenDialog(component, shouldBeEmpty);
                }
            }
            return showDialogResult;
        } else {
            return showDialogResult;
        }
    }
}
