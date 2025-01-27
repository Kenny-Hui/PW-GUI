package com.lx862.pwgui.gui.components.kui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/** A padded variant of DefaultTreeCellRenderer */
public class KTreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree jTree, Object o, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(jTree, o, selected, expanded, leaf, row, hasFocus);
        setBorder(new EmptyBorder(KGUIConstants.TREE_VIEW_PADDING, 0, KGUIConstants.TREE_VIEW_PADDING, 0));

        return this;
    }
}
