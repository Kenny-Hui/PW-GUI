package com.lx862.pwgui.gui.base.kui;

import javax.swing.*;
import java.awt.*;

/* Wrapper for panel with the GridBagLayout. Designed for form-like input */
public class KGridBagLayoutPanel extends JPanel {
    private final GridBagConstraints c;
    private final int maxWidthSpan;
    private int y;

    public KGridBagLayoutPanel(int insets, int maxWidthSpan) {
        setLayout(new GridBagLayout());
        this.maxWidthSpan = maxWidthSpan;
        this.c = new GridBagConstraints();
        this.c.insets = new Insets(insets, insets, insets, insets);
        this.c.fill = GridBagConstraints.HORIZONTAL;
    }

    public void addRow(int widthSpan, Component... components) {
        int x = 0;

        for(int i = 0; i < components.length; i++) {
            c.gridx = x;
            c.gridy = y;
            c.weightx = i == maxWidthSpan-1 ? 1 : 0; // Fill last row
            c.gridwidth = widthSpan;
            add(components[i], c);
            x++;
        }
        y++;
    }

    public void addRow(int widthSpan, int fullWeightIndex, Component... components) {
        int x = 0;

        for(int i = 0; i < components.length; i++) {
            c.gridx = x;
            c.gridy = y;
            c.weightx = i == fullWeightIndex ? 1 : 0;
            c.gridwidth = widthSpan;
            add(components[i], c);
            x++;
        }
        y++;
    }
}
