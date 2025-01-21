package com.lx862.pwgui.gui.base.kui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class KListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> jList, Object item, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(jList, item, index, isSelected, cellHasFocus);
        setBorder(new EmptyBorder(KGUIConstants.LIST_PADDING, KGUIConstants.LIST_PADDING, KGUIConstants.LIST_PADDING, KGUIConstants.LIST_PADDING));
        return this;
    }
}
