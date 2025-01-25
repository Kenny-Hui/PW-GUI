package com.lx862.pwgui.data.fileentry;

import com.lx862.pwgui.gui.components.NameTabPair;
import com.lx862.pwgui.core.PackwizMetaFile;
import com.lx862.pwgui.gui.panel.editing.filetype.FileEntryPaneContext;
import com.lx862.pwgui.gui.panel.editing.filetype.PackwizMetaPanel;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PackMetadataFileEntry extends PlainTextFileEntry {
    private PackwizMetaFile packwizMetaFile;

    public PackMetadataFileEntry(File file) {
        super(file);

        try {
            packwizMetaFile = new PackwizMetaFile(file.toPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<NameTabPair> getInspectPanels(FileEntryPaneContext context) {
        try {
            return addToList(super.getInspectPanels(context), new NameTabPair("Packwiz Meta File", new PackwizMetaPanel(context, this)));
        } catch (IOException e) {
            e.printStackTrace();
            return super.getInspectPanels(context);
        }
    }

    @Override
    public String getTreeDisplayName() {
        return packwizMetaFile.name;
    }

    @Override
    public boolean isNameModified() {
        return true;
    }

    public PackwizMetaFile getPackMetadata() {
        return packwizMetaFile;
    }
}