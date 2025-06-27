package com.lx862.pwgui.gui.components.kui;

import com.lx862.pwgui.util.GUIHelper;

import javax.swing.*;
import javax.swing.border.CompoundBorder;

/** A padded variant of JComboBox */
public class KComboBox<T> extends JComboBox<T> {
    public KComboBox() {
        setBorder(new CompoundBorder(getBorder(), GUIHelper.getPaddedBorder(KGUIConstants.COMBO_BOX_PADDING)));
        setRenderer(new KListCellRenderer());
    }
}
