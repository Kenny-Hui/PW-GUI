package com.lx862.pwgui.gui.components.kui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/* A padded variant of JComboBox */
public class KComboBox<T> extends JComboBox<T> {
    public KComboBox() {
        setBorder(new CompoundBorder(getBorder(), new EmptyBorder(KGUIConstants.COMBO_BOX_PADDING, KGUIConstants.COMBO_BOX_PADDING, KGUIConstants.COMBO_BOX_PADDING, KGUIConstants.COMBO_BOX_PADDING)));
        setRenderer(new KListCellRenderer());
    }
}
