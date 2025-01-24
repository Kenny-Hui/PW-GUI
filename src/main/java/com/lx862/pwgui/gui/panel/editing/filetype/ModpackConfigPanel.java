package com.lx862.pwgui.gui.panel.editing.filetype;

import com.lx862.pwgui.core.PackFile;
import com.lx862.pwgui.gui.panel.ModpackExtraSettingPanel;
import com.lx862.pwgui.gui.panel.ModpackInfoPanel;
import com.lx862.pwgui.gui.panel.ModpackVersionPanel;
import com.lx862.pwgui.data.fileentry.ModpackConfigFileEntry;
import com.lx862.pwgui.gui.base.kui.KSeparator;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ModpackConfigPanel extends FileTypePanel {
    private final ModpackInfoPanel infoPanel;
    private final ModpackVersionPanel versionPanel;
    private final ModpackExtraSettingPanel modpackExtraSettingPanel;
    private final PackFile packFile;

    public ModpackConfigPanel(FileEntryPaneContext context, ModpackConfigFileEntry fileEntry) throws IOException {
        super(context);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        this.packFile = new PackFile(fileEntry.packFile.getPath(), fileEntry.packFile.getToml());

        this.infoPanel = new ModpackInfoPanel(packFile, this::updateSaveState);
        this.infoPanel.setAlignmentX(LEFT_ALIGNMENT);

        this.versionPanel = new ModpackVersionPanel(packFile, this::updateSaveState);
        this.versionPanel.setAlignmentX(LEFT_ALIGNMENT);

        this.modpackExtraSettingPanel = new ModpackExtraSettingPanel(packFile);
        this.modpackExtraSettingPanel.setAlignmentX(LEFT_ALIGNMENT);

        add(this.infoPanel);
        add(new KSeparator());
        add(this.versionPanel);
        add(new KSeparator());
        add(this.modpackExtraSettingPanel);
        add(Box.createVerticalGlue());
    }

    @Override
    public boolean shouldSave() {
        return this.infoPanel != null && this.versionPanel != null && (this.infoPanel.shouldSave() || this.versionPanel.shouldSave());
    }

    @Override
    public boolean savable() {
        return true;
    }

    @Override
    public void save(Component parent) throws IOException {
        packFile.write();
    }
}
