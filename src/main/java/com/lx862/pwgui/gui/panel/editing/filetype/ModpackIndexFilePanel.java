package com.lx862.pwgui.gui.panel.editing.filetype;

import javax.swing.*;

public class ModpackIndexFilePanel extends FileTypePanel {
    public ModpackIndexFilePanel(FileEntryPaneContext context) {
        super(context);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel descriptionLabel = new JLabel("<html>The index file helps packwiz keep track of each file in the pack, including its hashes to ensure file integrity." +
                "<br><br>Most of the functionalities are already available in the GUI. As such there's no need to manually edit this file under normal circumstances.<br><br>In any case, you can check the raw text file by switching to <b>Plain Text</b> tab above.</html>");
        descriptionLabel.setAlignmentX(LEFT_ALIGNMENT);
        add(descriptionLabel);
    }
}
