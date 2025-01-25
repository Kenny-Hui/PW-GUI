package com.lx862.pwgui.data.fileentry;

import com.lx862.pwgui.gui.components.NameTabPair;
import com.lx862.pwgui.gui.panel.editing.filetype.DirectoryPanel;
import com.lx862.pwgui.gui.panel.editing.filetype.FileEntryPaneContext;

import java.io.File;
import java.util.List;

public class DirectoryEntry extends FileSystemEntityEntry {
    public DirectoryEntry(File file) {
        super(file);
    }

    @Override
    public List<NameTabPair> getInspectPanels(FileEntryPaneContext context) {
        return addToList(super.getInspectPanels(context), new NameTabPair("Folder", new DirectoryPanel(context, this)));
    }
}
