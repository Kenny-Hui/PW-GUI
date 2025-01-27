package com.lx862.pwgui.gui.components.fstree;

import com.lx862.pwgui.data.model.file.*;
import com.lx862.pwgui.gui.components.kui.KTreeCellRenderer;
import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;

public class FileSystemTreeCellRenderer extends KTreeCellRenderer {
    private static final int IGNORED_COLOR_ALPHA = 75;

    @Override
    public Component getTreeCellRendererComponent(JTree jTree, Object o, boolean selected, boolean expanded, boolean leaf, int row, boolean b3) {
        super.getTreeCellRendererComponent(jTree, o, selected, expanded, leaf, row, b3);

        if(o instanceof FileSystemSortedTreeNode) {
            FileSystemEntityModel fileInfo = (FileSystemEntityModel)((FileSystemSortedTreeNode)o).getUserObject();
            boolean isDirectory = fileInfo.path.toFile().isDirectory();

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

            if(fileInfo.name.equals("options.txt")) {
                setIcon(new ImageIcon(GUIHelper.convertImage(Util.getAssets("/assets/components/minecraft.png"), 18)));
            } else if(fileInfo instanceof ModpackConfigFileModel) {
                setIcon(new ImageIcon(GUIHelper.convertImage(Util.getAssets("/assets/mime/settings.png"), 18)));
            } else if(fileInfo instanceof PackwizIgnoreFileModel) {
                setIcon(new ImageIcon(GUIHelper.convertImage(Util.getAssets("/assets/mime/packwizignore.png"), 18)));
            } else if(fileInfo instanceof ModpackIndexFileModel) {
                setIcon(new ImageIcon(GUIHelper.convertImage(Util.getAssets("/assets/mime/packwiz.png"), 18)));
            } else if(fileInfo instanceof GitIgnoreFileModel) {
                setIcon(new ImageIcon(GUIHelper.convertImage(Util.getAssets("/assets/mime/gitignore.png"), 18)));
            } else if(fileInfo instanceof ModrinthPackFileModel) {
                setIcon(new ImageIcon(GUIHelper.convertImage(Util.getAssets("/assets/services/modrinth.png"), 18)));
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
