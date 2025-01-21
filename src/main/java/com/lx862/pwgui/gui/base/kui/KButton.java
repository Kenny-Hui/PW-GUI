package com.lx862.pwgui.gui.base.kui;

import javax.swing.*;
import java.awt.*;

/* A padded variant of JButton */
public class KButton extends JButton {
    public KButton(String description, Icon icon) {
        super(description, icon);
        setMargin(new Insets(5, 15, 5, 15));
    }

    public KButton(String description) {
        this(description, null);
    }
}
