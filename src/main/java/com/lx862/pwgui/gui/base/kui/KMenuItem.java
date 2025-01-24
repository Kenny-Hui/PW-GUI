package com.lx862.pwgui.gui.base.kui;

import javax.swing.*;
import java.awt.*;

/* A padded variant of JMenuItem */
public class KMenuItem extends JMenuItem {
    public KMenuItem(String description) {
        super(description);
        setMargin(new Insets(5, 10, 5, 10));
    }

    public KMenuItem(Action action) {
        super(action);
        setMargin(new Insets(5, 10, 5, 10));
    }
}
