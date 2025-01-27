package com.lx862.pwgui.gui.components.kui;

import javax.swing.*;
import java.awt.*;

/** JSplitPane with an initial split ratio */
public class KSplitPane extends JSplitPane {
    public KSplitPane(int type, Component componentLeft, Component componentRight, double initialSplitRatio) {
        super(type, componentLeft, componentRight);

        SwingUtilities.invokeLater(() -> {
            setDividerLocation(initialSplitRatio);
        });
    }
}
