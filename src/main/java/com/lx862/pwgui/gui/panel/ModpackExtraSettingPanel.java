package com.lx862.pwgui.gui.panel;

import com.lx862.pwgui.data.PackComponent;
import com.lx862.pwgui.core.PackFile;
import com.lx862.pwgui.gui.base.kui.KButton;
import com.lx862.pwgui.gui.base.kui.KGridBagLayoutPanel;
import com.lx862.pwgui.gui.dialog.ChangeAcceptableGameVersionDialog;

import javax.swing.*;
import java.awt.*;

public class ModpackExtraSettingPanel extends KGridBagLayoutPanel {
    public ModpackExtraSettingPanel(PackFile existingFile) {
        super(3, 3);

        KButton changeVersionRangeButton = new KButton("Change...");
        changeVersionRangeButton.addActionListener(actionEvent -> {
            new ChangeAcceptableGameVersionDialog((JFrame) getTopLevelAncestor(), existingFile.getComponent(PackComponent.MINECRAFT).version, existingFile.getOptionAcceptableGameVersion(true)).setVisible(true);
        });

        addRow(1, 0, new JLabel("Acceptable Minecraft Version: " + String.join(", ", existingFile.getOptionAcceptableGameVersion(true))), changeVersionRangeButton);
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension size = getPreferredSize();
        size.width = Short.MAX_VALUE;
        return size;
    }
}
