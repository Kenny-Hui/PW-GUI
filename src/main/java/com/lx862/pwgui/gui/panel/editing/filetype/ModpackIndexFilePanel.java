package com.lx862.pwgui.gui.panel.editing.filetype;

import com.lx862.pwgui.data.model.file.ModpackIndexFileModel;
import com.lx862.pwgui.gui.action.RefreshPackAction;
import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.util.GUIHelper;

import javax.swing.*;

public class ModpackIndexFilePanel extends FileTypePanel {
    public ModpackIndexFilePanel(FileEntryPaneContext context, ModpackIndexFileModel fileEntry) {
        super(context);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JLabel descriptionLabel = new JLabel("<html>The index file helps packwiz keep track of each file in the pack, including its hashes to ensure file integrity.</html>");
        descriptionLabel.setAlignmentX(LEFT_ALIGNMENT);
        add(descriptionLabel);

        add(GUIHelper.createVerticalPadding(10));

        JLabel description2Label = new JLabel("<html>Most of the functionalities are already available in the GUI. As such there should be no need to manually edit this file.</html>");
        description2Label.setAlignmentX(LEFT_ALIGNMENT);
        add(description2Label);

        add(GUIHelper.createVerticalPadding(10));

        JLabel description3Label = new JLabel("<html>If you have added files outside of PW-GUI, you can press the refresh button below to keep the index up to date.</html>");
        description3Label.setAlignmentX(LEFT_ALIGNMENT);
        add(description3Label);

        add(GUIHelper.createVerticalPadding(10));

        KButton refreshButton = new KButton(new RefreshPackAction(getTopLevelAncestor()));
        refreshButton.setAlignmentX(LEFT_ALIGNMENT);
        add(refreshButton);
    }
}
