package com.lx862.pwgui.data.fileentry;

import com.lx862.pwgui.gui.base.NameTabPair;
import com.lx862.pwgui.gui.panel.editing.filetype.ModpackIndexFilePanel;
import com.lx862.pwgui.gui.panel.editing.filetype.FileEntryPaneContext;

import java.io.File;
import java.util.List;

public class ModpackIndexFileEntry extends PlainTextFileEntry {
    public ModpackIndexFileEntry(File file) {
        super(file);
    }

    @Override
    public List<NameTabPair> getInspectPanels(FileEntryPaneContext context) {
        return addToList(super.getInspectPanels(context), new NameTabPair("Packwiz Index", new ModpackIndexFilePanel(context)));
    }

    @Override
    public String getTreeDisplayName() {
        return "Packwiz Index";
    }

    @Override
    public boolean isNameModified() {
        return true;
    }
}