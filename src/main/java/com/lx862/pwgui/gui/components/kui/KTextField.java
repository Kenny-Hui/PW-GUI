package com.lx862.pwgui.gui.components.kui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** JTextField with placeholder text support */
public class KTextField extends JTextField {
    private final String placeholder;

    public KTextField(String placeholderText) {
        this.placeholder = placeholderText;
        setBorder(new CompoundBorder(getBorder(), new EmptyBorder(3, 3, 3, 3)));
    }

    public KTextField() {
        this(null);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if(placeholder != null && !placeholder.isEmpty() && getText().isEmpty()) {
            float y = getInsets().top + g2d.getFontMetrics().getMaxAscent();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getDisabledTextColor());
            g2d.drawString(placeholder, getInsets().left, y);
        }
    }

    @Override
    public void setText(String str) {
        super.setText(str);

        // We want it to always start at the beginning
        setSelectionStart(0);
        setSelectionEnd(0);
    }
}
