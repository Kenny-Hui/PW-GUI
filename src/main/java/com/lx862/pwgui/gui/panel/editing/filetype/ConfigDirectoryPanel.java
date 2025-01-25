package com.lx862.pwgui.gui.panel.editing.filetype;

import javax.swing.*;

public class ConfigDirectoryPanel extends FileTypePanel {
    public ConfigDirectoryPanel(FileEntryPaneContext context) {
        super(context);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel descriptionLabel = new JLabel("<html>The config folder is used by Minecraft Mods to save their respective configs.</html>");
        descriptionLabel.setAlignmentX(LEFT_ALIGNMENT);
        add(descriptionLabel);
    }
}
