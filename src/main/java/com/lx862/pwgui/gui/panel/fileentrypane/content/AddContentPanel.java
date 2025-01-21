package com.lx862.pwgui.gui.panel.fileentrypane.content;

import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.data.IconNamePair;
import com.lx862.pwgui.data.fileentry.ContentManagementFolderFileEntry;
import com.lx862.pwgui.gui.panel.fileentrypane.FileEntryPaneContext;
import com.lx862.pwgui.gui.panel.fileentrypane.FileTypePanel;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;

public class AddContentPanel extends FileTypePanel {
    public AddContentPanel(FileEntryPaneContext context, ContentManagementFolderFileEntry fileEntry) {
        setLayout(new BorderLayout());
        JTabbedPane tab = new JTabbedPane();
        tab.addTab(IconNamePair.MODRINTH.name, new ImageIcon(GUIHelper.resizeImage(IconNamePair.MODRINTH.image, 20)), new ModrinthPanel(context, fileEntry));
        tab.addTab(IconNamePair.CURSEFORGE.name, new ImageIcon(GUIHelper.resizeImage(IconNamePair.CURSEFORGE.image, 20)), new CurseForgePanel(context, fileEntry));
        tab.addTab("URL Link", new ImageIcon(GUIHelper.convertImage(Util.getAssets("/mime/link.png"), 20)), new UrlPanel(context, fileEntry));
        add(tab);
    }
}
