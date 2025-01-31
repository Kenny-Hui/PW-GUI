package com.lx862.pwgui.gui.components.kui;

import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.util.Util;

import javax.swing.*;

public class KHelpButton extends JButton {
    public KHelpButton(String str) {
        setIcon(new ImageIcon(GUIHelper.convertImage(Util.getAssets("/assets/ui/help.png"), 16)));
        setToolTipText("What is this?");
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        addActionListener(actionEvent -> {
            JOptionPane.showMessageDialog(getTopLevelAncestor(), str, "What is this?", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
