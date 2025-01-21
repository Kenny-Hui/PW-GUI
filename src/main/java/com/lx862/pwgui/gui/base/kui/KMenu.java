package com.lx862.pwgui.gui.base.kui;

import javax.swing.*;
import java.awt.*;

/* A padded variant of JMenu */
public class KMenu extends JMenu {
    public KMenu(String description) {
        super(description);
        setMargin(new Insets(5, 10, 5, 10));
    }
}
