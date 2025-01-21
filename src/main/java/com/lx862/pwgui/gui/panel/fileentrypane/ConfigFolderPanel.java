package com.lx862.pwgui.gui.panel.fileentrypane;

import javax.swing.*;

public class ConfigFolderPanel extends FileTypePanel {
    public ConfigFolderPanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel descriptionLabel = new JLabel("<html>The config folder is used by Minecraft Mods to save their respective configs.</html>");
        descriptionLabel.setAlignmentX(LEFT_ALIGNMENT);
        add(descriptionLabel);
    }
}
