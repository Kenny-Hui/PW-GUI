package com.lx862.pwgui.gui.panel.editing.filetype;

import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.core.PackFile;
import com.lx862.pwgui.gui.action.UpdateAllAction;
import com.lx862.pwgui.gui.panel.ModpackExtraSettingPanel;
import com.lx862.pwgui.gui.panel.ModpackInfoPanel;
import com.lx862.pwgui.gui.panel.ModpackVersionPanel;
import com.lx862.pwgui.data.fileentry.ModpackConfigFileEntry;
import com.lx862.pwgui.gui.components.kui.KSeparator;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.event.ActionEvent;
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
    public void save() throws IOException {
        boolean mcVersionChanged = this.versionPanel.minecraftVersionChanged();
        packFile.write(Constants.REASON_TRIGGERED_BY_USER);

        if(mcVersionChanged) {
            if(JOptionPane.showConfirmDialog(getTopLevelAncestor(), "Minecraft version has changed.\nDo you want to update the mods as well?", Util.withTitlePrefix("Update Mods"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                new UpdateAllAction((JFrame)getTopLevelAncestor()).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
            }
        }
    }
}
