package com.lx862.pwgui.data.fileentry;

import com.lx862.pwgui.gui.components.NameTabPair;
import com.lx862.pwgui.gui.panel.editing.filetype.FileEntryPaneContext;
import com.lx862.pwgui.gui.panel.editing.filetype.PlainTextPanel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class PlainTextFileEntry extends GenericFileEntry {
    private String cachedContent;

    public PlainTextFileEntry(File file) {
        super(file);
    }

    public String getContent() throws IOException {
        if(cachedContent == null) {
            cachedContent = String.join("\n", Files.readAllLines(path));
        }
        return cachedContent;
    }

    @Override
    public List<NameTabPair> getInspectPanels(FileEntryPaneContext context) {
        return addToList(super.getInspectPanels(context), new NameTabPair("Plain Text", new PlainTextPanel(this, context)));
    }
}