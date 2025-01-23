package com.lx862.pwgui.data.fileentry;

import com.lx862.pwgui.gui.base.NameTabPair;
import com.lx862.pwgui.gui.panel.editing.filetype.FileEntryPaneContext;
import com.lx862.pwgui.gui.panel.editing.filetype.PackwizIgnorePanel;

import java.io.File;
import java.util.List;

public class PackwizIgnoreFileEntry extends PlainTextFileEntry {
    public PackwizIgnoreFileEntry(File file) {
        super(file);
    }

    @Override
    public List<NameTabPair> getInspectPanels(FileEntryPaneContext context) {
        return addToList(super.getInspectPanels(context), new NameTabPair("Packwiz Ignore", new PackwizIgnorePanel(context, this)));
    }

    @Override
    public String getTreeDisplayName() {
        return "Packwiz Ignore list";
    }

    @Override
    public boolean isNameModified() {
        return true;
    }
}
