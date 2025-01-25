package com.lx862.pwgui.data.fileentry;

import com.lx862.pwgui.gui.components.NameTabPair;
import com.lx862.pwgui.gui.panel.editing.filetype.FileEntryPaneContext;
import com.lx862.pwgui.gui.panel.editing.filetype.GitIgnorePanel;

import java.io.File;
import java.util.List;

public class GitIgnoreFileEntry extends PlainTextFileEntry {
    public GitIgnoreFileEntry(File file) {
        super(file);
    }

    @Override
    public List<NameTabPair> getInspectPanels(FileEntryPaneContext context) {
        return addToList(super.getInspectPanels(context), new NameTabPair("Git Ignore", new GitIgnorePanel(context,this)));
    }

    @Override
    public String getTreeDisplayName() {
        return "Git Ignore list";
    }

    @Override
    public boolean isNameModified() {
        return true;
    }
}
