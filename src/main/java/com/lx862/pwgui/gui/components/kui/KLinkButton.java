package com.lx862.pwgui.gui.components.kui;

import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;

/** A button disguised as a clickable hyperlink */
public class KLinkButton extends JButton {
    public KLinkButton(String url) {
        this(url, url);
    }

    public KLinkButton(String name, String url) {
        this(name, url, SwingConstants.LEFT);
    }

    public KLinkButton(String name, String url, int swingConstants) {
        super();
        setText(String.format("<html><a href=\"%s\">%s</a></html>", url == null ? "https://example.com" : url, name));
        setHorizontalAlignment(swingConstants);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setToolTipText(url);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if(url != null) {
            addActionListener(actionEvent -> {
                Util.tryBrowse(url);
            });
        }
        setBorder(null);
    }
}
