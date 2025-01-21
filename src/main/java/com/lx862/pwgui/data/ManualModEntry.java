package com.lx862.pwgui.data;

import com.lx862.pwgui.gui.base.kui.KLinkButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ManualModEntry extends JPanel {

    public ManualModEntry(ManualModInfo info, boolean exists) {
        setBackground(new JTextArea().getBackground());
        setBorder(new EmptyBorder(6, 6, 0, 6));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        add(new JSeparator());
        JLabel title = new JLabel(String.format("<html><b>%s</b> <span style=\"color:green\">%s</span></html>", info.name, exists ? "(Found!)" : ""));
        title.setFont(UIManager.getFont("h4.font"));
        add(title);
        add(new JLabel(info.fileName));
        add(new KLinkButton(info.url));
    }
}
