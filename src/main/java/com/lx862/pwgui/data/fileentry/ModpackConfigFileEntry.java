package com.lx862.pwgui.data.fileentry;

import com.lx862.pwgui.gui.panel.editing.filetype.ModpackConfigPanel;
import com.lx862.pwgui.gui.components.NameTabPair;
import com.lx862.pwgui.core.PackFile;
import com.lx862.pwgui.gui.panel.editing.filetype.FileEntryPaneContext;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ModpackConfigFileEntry extends GenericFileEntry {
    public PackFile packFile;

    public ModpackConfigFileEntry(File file) {
        super(file);
        try {
            packFile = new PackFile(file.toPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<NameTabPair> getInspectPanels(FileEntryPaneContext context) {
        try {
            return addToList(super.getInspectPanels(context), new NameTabPair("Modpack Config", new ModpackConfigPanel(context, this)));
        } catch (IOException e) {
            e.printStackTrace();
            return super.getInspectPanels(context);
        }
    }

    @Override
    public String getTreeDisplayName() {
        return "Modpack Config";
    }

    @Override
    public boolean isNameModified() {
        return true;
    }
}