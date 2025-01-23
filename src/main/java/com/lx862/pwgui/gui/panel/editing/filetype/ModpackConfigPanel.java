package com.lx862.pwgui.gui.panel.editing.filetype;

import com.lx862.pwgui.gui.panel.ModpackExtraSettingPanel;
import com.lx862.pwgui.gui.panel.ModpackInfoPanel;
import com.lx862.pwgui.gui.panel.ModpackVersionPanel;
import com.lx862.pwgui.data.fileentry.ModpackConfigFileEntry;
import com.lx862.pwgui.gui.base.kui.KSeparator;

import javax.swing.*;
import java.io.IOException;

public class ModpackConfigPanel extends FileTypePanel {
    public ModpackConfigPanel(FileEntryPaneContext context, ModpackConfigFileEntry fileEntry) throws IOException {
        super(context);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        ModpackInfoPanel infoPanel = new ModpackInfoPanel(fileEntry.packFile);
        ModpackVersionPanel versionPanel = new ModpackVersionPanel(fileEntry.packFile);
        ModpackExtraSettingPanel modpackExtraSettingPanel = new ModpackExtraSettingPanel(fileEntry.packFile);
        infoPanel.setAlignmentX(LEFT_ALIGNMENT);
        versionPanel.setAlignmentX(LEFT_ALIGNMENT);
        modpackExtraSettingPanel.setAlignmentX(LEFT_ALIGNMENT);

        add(infoPanel);
        add(new KSeparator());
        add(versionPanel);
        add(new KSeparator());
        add(modpackExtraSettingPanel);
        add(Box.createVerticalGlue());
    }

    @Override
    public boolean savable() {
        return true;
    }
}
