package com.lx862.pwgui.data.fileentry;

import com.lx862.pwgui.gui.base.NameTabPair;
import com.lx862.pwgui.gui.panel.editing.filetype.FileEntryPaneContext;
import com.lx862.pwgui.gui.panel.editing.filetype.FilePanel;

import java.io.File;
import java.util.List;

public class GenericFileEntry extends FileSystemEntityEntry {
    public final long fileSize;
    public final long lastModified;

    public GenericFileEntry(File file) {
        super(file);
        this.fileSize = file.length();
        this.lastModified = file.lastModified();
    }

    @Override
    public List<NameTabPair> getInspectPanels(FileEntryPaneContext context) {
        return addToList(super.getInspectPanels(context), new NameTabPair("File", new FilePanel(context, this)));
    }
}
