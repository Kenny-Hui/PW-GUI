package com.lx862.pwgui.gui.components.kui;

import javax.swing.*;
import java.awt.*;

/** A padded variant of JButton */
public class KButton extends JButton {
    public KButton(String description, Icon icon) {
        super(description, icon);
        setMargin(new Insets(5, 15, 5, 15));
    }

    public KButton(String description) {
        this(description, null);
    }

    public KButton(Action action) {
        super(action);
        setMargin(new Insets(5, 15, 5, 15));
    }

    public void setEnabled(boolean value, String disabledReason) {
        if(!value) {
            setToolTipText(disabledReason);
        } else {
            setToolTipText(null);
        }
        super.setEnabled(value);
    }
}
