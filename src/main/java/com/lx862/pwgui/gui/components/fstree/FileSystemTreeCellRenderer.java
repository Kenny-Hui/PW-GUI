package com.lx862.pwgui.gui.components.fstree;

import com.lx862.pwgui.data.model.file.*;
import com.lx862.pwgui.gui.components.kui.KTreeCellRenderer;

import javax.swing.*;
import java.awt.*;

public class FileSystemTreeCellRenderer extends KTreeCellRenderer {
    private static final int IGNORED_COLOR_ALPHA = 75;

    @Override
    public Component getTreeCellRendererComponent(JTree jTree, Object o, boolean selected, boolean expanded, boolean leaf, int row, boolean b3) {
        super.getTreeCellRendererComponent(jTree, o, selected, expanded, leaf, row, b3);

        if(o instanceof FileSystemSortedTreeNode) {
            FileSystemEntityModel fileInfo = (FileSystemEntityModel)((FileSystemSortedTreeNode)o).getUserObject();

            if(((FileSystemTree)jTree).getIgnorePattern() != null && ((FileSystemTree)jTree).getIgnorePattern().shouldIgnore(fileInfo.path)) {
                Color lowOpacityColor = new Color(getForeground().getRed(), getForeground().getGreen(), getForeground().getBlue(), IGNORED_COLOR_ALPHA);
                setForeground(lowOpacityColor);
            }

            if(fileInfo.isUserFriendlyName()) {
                setFont(getFont().deriveFont(Font.ITALIC));
            } else {
                setFont(getFont().deriveFont(Font.PLAIN));
            }
            setText(fileInfo.getDisplayName());

            setIcon(fileInfo.getIcon());
        } else {
            setFont(getFont().deriveFont(Font.PLAIN));
            setText(o.toString());
        }

        return this;
    }
}
