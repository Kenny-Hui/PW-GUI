package com.lx862.pwgui.gui.components.kui;

import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.util.Util;

import javax.swing.*;

/* A button that displays more detailed information about a control field */
public class KHelpButton extends JButton {
    public KHelpButton(String detailedInfo) {
        setIcon(new ImageIcon(GUIHelper.convertImage(Util.getAssets("/assets/ui/help.png"), 16)));
        setToolTipText("What is this?");
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        addActionListener(actionEvent -> {
            JOptionPane.showMessageDialog(getTopLevelAncestor(), detailedInfo, "What is this?", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
