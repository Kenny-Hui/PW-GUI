package com.lx862.pwgui.gui.base.kui;

import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;

/* A button disguised as a clickable hyperlink */
public class KLinkButton extends JButton {
    public KLinkButton(String url) {
        super();
        setText(String.format("<html><a href=\"%s\">%s</a></html>", url, url));
        setHorizontalAlignment(LEFT);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setToolTipText(url);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addActionListener(actionEvent -> {
            Util.tryEditFile(url);
        });
        setBorder(null);
    }
}
