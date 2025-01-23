package com.lx862.pwgui.gui.base.kui;

import javax.swing.*;
import java.awt.*;

/* JSplitPane with an initial split ratio */
public class KSplitPane extends JSplitPane {
    private final double initialSplitRatio;
    private boolean resized;

    public KSplitPane(int type, Component component1, Component component2, double initialSplitRatio) {
        super(type, component1, component2);
        this.initialSplitRatio = initialSplitRatio;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (!resized) {
            resized = true;
            setDividerLocation(initialSplitRatio);
        }
    }
}
