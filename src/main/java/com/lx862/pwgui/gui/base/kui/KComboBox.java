package com.lx862.pwgui.gui.base.kui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/* A padded variant of JComboBox */
public class KComboBox<T> extends JComboBox<T> {
    public KComboBox() {
        setBorder(new CompoundBorder(getBorder(), new EmptyBorder(3, 3, 3, 3)));
    }
}
