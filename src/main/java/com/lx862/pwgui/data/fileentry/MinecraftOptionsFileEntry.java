package com.lx862.pwgui.data.fileentry;

import com.lx862.pwgui.gui.panel.editing.filetype.MinecraftOptionPanel;
import com.lx862.pwgui.gui.base.NameTabPair;
import com.lx862.pwgui.gui.panel.editing.filetype.FileEntryPaneContext;

import java.io.File;
import java.util.List;

public class MinecraftOptionsFileEntry extends PlainTextFileEntry {
    public MinecraftOptionsFileEntry(File file) {
        super(file);
    }

    public List<NameTabPair> getInspectPanels(FileEntryPaneContext context) {
        return addToList(super.getInspectPanels(context), new NameTabPair("Minecraft Options", new MinecraftOptionPanel(context, this)));
    }

    @Override
    public String getTreeDisplayName() {
        return "Minecraft Options File";
    }

    @Override
    public boolean isNameModified() {
        return true;
    }
}
