package com.lx862.pwgui.gui.components.kui;

import com.lx862.pwgui.util.GUIHelper;

import javax.swing.*;
import java.awt.*;

/** A padded variant of DefaultListCellRenderer */
public class KListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> jList, Object item, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(jList, item, index, isSelected, cellHasFocus);
        setBorder(GUIHelper.getPaddedBorder(KGUIConstants.LIST_PADDING));
        return this;
    }
}
