package com.lx862.pwgui.data.fileentry;

import com.lx862.pwgui.gui.base.NameTabPair;
import com.lx862.pwgui.gui.panel.fileentrypane.FileEntryPaneContext;
import com.lx862.pwgui.gui.panel.fileentrypane.LicenseFilePanel;

import java.io.File;
import java.util.List;

public class LicenseFileEntry extends PlainTextFileEntry {
    public LicenseFileEntry(File file) {
        super(file);
    }

    @Override
    public List<NameTabPair> getInspectPanels(FileEntryPaneContext context) {
        return addToList(super.getInspectPanels(context), new NameTabPair("License", new LicenseFilePanel(this)));
    }
}
