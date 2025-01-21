package com.lx862.pwgui.data.fileentry;

import com.lx862.pwgui.gui.panel.fileentrypane.ImagePanel;
import com.lx862.pwgui.gui.base.NameTabPair;
import com.lx862.pwgui.gui.panel.fileentrypane.FileEntryPaneContext;

import java.io.File;
import java.util.List;

public class ImageFileEntry extends GenericFileEntry {
    public ImageFileEntry(File file) {
        super(file);
    }

    @Override
    public List<NameTabPair> getInspectPanels(FileEntryPaneContext context) {
        return addToList(super.getInspectPanels(context), new NameTabPair("Image", new ImagePanel(this)));
    }
}
