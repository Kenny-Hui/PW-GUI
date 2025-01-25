package com.lx862.pwgui.gui.components;

import com.lx862.pwgui.data.ManualModInfo;
import com.lx862.pwgui.gui.components.kui.KLinkButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ManualModEntryPanel extends JPanel {
    public ManualModEntryPanel(ManualModInfo info, boolean exists) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(new EmptyBorder(6, 6, 0, 6));
        setBackground(new JTextArea().getBackground());

        add(new JSeparator());

        JLabel title = new JLabel(String.format("<html><b>%s</b> <span style=\"color:green\">%s</span></html>", info.name, exists ? "(Found!)" : ""));
        title.setFont(UIManager.getFont("h4.font"));
        add(title);

        add(new JLabel(info.fileName));

        add(new KLinkButton(info.url));
    }
}
