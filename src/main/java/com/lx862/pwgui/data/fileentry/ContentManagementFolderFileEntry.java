package com.lx862.pwgui.data.fileentry;

import com.lx862.pwgui.gui.base.NameTabPair;
import com.lx862.pwgui.gui.panel.editing.filetype.content.AddContentPanel;
import com.lx862.pwgui.gui.panel.editing.filetype.FileEntryPaneContext;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ContentManagementFolderFileEntry extends FileSystemEntityEntry {
    private static final HashMap<String, String> folderToNameMap = new HashMap<>();

    static {
        folderToNameMap.put("mods", "Mods");
        folderToNameMap.put("resourcepacks", "Resource Packs");
        folderToNameMap.put("shaderpacks", "Shader Packs");
        folderToNameMap.put("plugins", "Plugins");
    }

    public ContentManagementFolderFileEntry(File file) {
        super(file);
    }

    @Override
    public List<NameTabPair> getInspectPanels(FileEntryPaneContext context) {
        return addToList(super.getInspectPanels(context), new NameTabPair(String.format("Add new %s", getTreeDisplayName()), new AddContentPanel(context, this)));
    }

    @Override
    public String getTreeDisplayName() {
        String fileName = path.toFile().getName();
        return folderToNameMap.getOrDefault(fileName, fileName);
    }

    @Override
    public boolean isNameModified() {
        return true;
    }
}