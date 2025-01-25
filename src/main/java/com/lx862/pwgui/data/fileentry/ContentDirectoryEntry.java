package com.lx862.pwgui.data.fileentry;

import com.lx862.pwgui.gui.components.NameTabPair;
import com.lx862.pwgui.gui.panel.editing.filetype.content.AddContentPanel;
import com.lx862.pwgui.gui.panel.editing.filetype.FileEntryPaneContext;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ContentDirectoryEntry extends DirectoryEntry {
    private static final HashMap<String, String> directoryToNameMap = new HashMap<>();

    static {
        directoryToNameMap.put("mods", "Mods");
        directoryToNameMap.put("resourcepacks", "Resource Packs");
        directoryToNameMap.put("shaderpacks", "Shader Packs");
        directoryToNameMap.put("plugins", "Plugins");
    }

    public ContentDirectoryEntry(File file) {
        super(file);
    }

    @Override
    public List<NameTabPair> getInspectPanels(FileEntryPaneContext context) {
        return addToList(super.getInspectPanels(context), new NameTabPair(String.format("Add new %s", getTreeDisplayName()), new AddContentPanel(context, this)));
    }

    @Override
    public String getTreeDisplayName() {
        String fileName = path.toFile().getName();
        return directoryToNameMap.getOrDefault(fileName, fileName);
    }

    @Override
    public boolean isNameModified() {
        return true;
    }
}