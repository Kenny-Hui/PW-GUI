package com.lx862.pwgui.gui.action;

import com.lx862.pwgui.Main;
import com.lx862.pwgui.core.Config;
import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.gui.frame.SetupFrame;
import com.lx862.pwgui.util.Util;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class ResetProgramAction extends AbstractAction {
    private final Window[] parents;

    public ResetProgramAction(Window... parents) {
        super("Reset...");
        this.parents = parents;
        putValue(MNEMONIC_KEY, KeyEvent.VK_R);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(JOptionPane.showConfirmDialog(parents[0], String.format("This will reset %s to it's initial state as if it's the first time the program is launched.\nAre you sure you want to continue?", Constants.PROGRAM_NAME), Util.withTitlePrefix("Reset Program?"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                FileUtils.deleteDirectory(Config.CONFIG_DIR_PATH.toFile());
            } catch (IOException e) {
                Main.LOGGER.exception(e);
                JOptionPane.showMessageDialog(parents[0], String.format("Failed to delete folder %s!\nCannot reset program!", Config.CONFIG_DIR_PATH), Util.withTitlePrefix("Reset Failed!"), JOptionPane.ERROR_MESSAGE);
            }

            SetupFrame setupFrame = new SetupFrame(parents[0]);
            setupFrame.setVisible(true);

            for(Window parent : parents) {
                parent.dispose();
            }
        }
    }
}
