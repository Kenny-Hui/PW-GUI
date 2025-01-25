package com.lx862.pwgui.gui.components.kui;

import com.lx862.pwgui.data.fileentry.*;
import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.gui.components.fstree.FileSystemTree;
import com.lx862.pwgui.gui.components.fstree.FileSystemSortedTreeNode;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class KTreeCellRenderer extends DefaultTreeCellRenderer {
    private static final int IGNORED_COLOR_ALPHA = 75;

    @Override
    public Component getTreeCellRendererComponent(JTree jTree, Object o, boolean selected, boolean expanded, boolean leaf, int row, boolean b3) {
        super.getTreeCellRendererComponent(jTree, o, selected, expanded, leaf, row, b3);
        setBorder(new EmptyBorder(KGUIConstants.LIST_PADDING, 0, KGUIConstants.LIST_PADDING, 0));

        if(o instanceof FileSystemSortedTreeNode) {
            FileSystemEntityEntry fileInfo = (FileSystemEntityEntry)((FileSystemSortedTreeNode)o).getUserObject();
            boolean isDirectory = fileInfo.path.toFile().isDirectory();

            if(((FileSystemTree)jTree).getIgnorePattern() != null && ((FileSystemTree)jTree).getIgnorePattern().shouldIgnore(fileInfo.path)) {
                Color lowOpacityColor = new Color(getForeground().getRed(), getForeground().getGreen(), getForeground().getBlue(), IGNORED_COLOR_ALPHA);
                setForeground(lowOpacityColor);
            }

            if(fileInfo.isNameModified()) {
                setFont(getFont().deriveFont(Font.ITALIC));
            } else {
                setFont(getFont().deriveFont(Font.PLAIN));
            }
            setText(fileInfo.getTreeDisplayName());

            if(fileInfo.name.equals("options.txt")) {
                setIcon(new ImageIcon(GUIHelper.convertImage(Util.getAssets("/components/minecraft.png"), 18)));
            } else if(fileInfo instanceof ModpackConfigFileEntry) {
                setIcon(new ImageIcon(GUIHelper.convertImage(Util.getAssets("/mime/settings.png"), 18)));
            } else if(fileInfo instanceof PackwizIgnoreFileEntry) {
                setIcon(new ImageIcon(GUIHelper.convertImage(Util.getAssets("/mime/packwizignore.png"), 18)));
            } else if(fileInfo instanceof ModpackIndexFileEntry) {
                setIcon(new ImageIcon(GUIHelper.convertImage(Util.getAssets("/mime/packwiz.png"), 18)));
            } else if(fileInfo instanceof GitIgnoreFileEntry) {
                setIcon(new ImageIcon(GUIHelper.convertImage(Util.getAssets("/mime/gitignore.png"), 18)));
            } else if(fileInfo instanceof ModrinthPackFileEntry) {
                setIcon(new ImageIcon(GUIHelper.convertImage(Util.getAssets("/services/modrinth.png"), 18)));
            } else if(!isDirectory) {
                setIcon(UIManager.getIcon("FileView.fileIcon"));
            } else {
                setIcon(UIManager.getIcon("FileView.directoryIcon"));
            }
        } else {
            setFont(getFont().deriveFont(Font.PLAIN));
            setText(o.toString());
        }

        return this;
    }
}
