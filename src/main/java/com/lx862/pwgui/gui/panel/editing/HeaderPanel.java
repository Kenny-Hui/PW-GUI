package com.lx862.pwgui.gui.panel.editing;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.lx862.pwgui.core.PackFile;
import com.lx862.pwgui.data.PackComponentVersion;
import com.lx862.pwgui.util.GUIHelper;

import javax.swing.*;
import java.awt.*;

/* The top section that displays the modpack's name and versions */
public class HeaderPanel extends JPanel {
    public HeaderPanel(PackFile packFile) {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        initialize(packFile);
    }

    public void initialize(PackFile packFile) {
        removeAll();

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel modpackNameLabel = new JLabel(packFile.getName());
        modpackNameLabel.setFont(FlatUIUtils.nonUIResource(UIManager.getFont("h2.font")));
        infoPanel.add(modpackNameLabel);

        if(!packFile.author.isEmpty() || !packFile.version.isEmpty()) {
            JLabel modpackVersionAuthorLabel = new JLabel();
            if(!packFile.version.isEmpty() && packFile.author.isEmpty()) {
                modpackVersionAuthorLabel.setText(packFile.version);
            } else if(!packFile.author.isEmpty() && packFile.version.isEmpty()) {
                modpackVersionAuthorLabel.setText(String.format("by %s", packFile.author));
            } else {
                modpackVersionAuthorLabel.setText(String.format("%s by %s", packFile.version, packFile.author));
            }
            infoPanel.add(modpackVersionAuthorLabel);
        }

        add(infoPanel);
        add(Box.createHorizontalGlue());

        JPanel componentsPanel = new JPanel();
        componentsPanel.setLayout(new BoxLayout(componentsPanel, BoxLayout.Y_AXIS));

        for (PackComponentVersion packComponentVersion : packFile.getComponents()) {
            JLabel componentLabel = new JLabel(packComponentVersion.getComponent().iconName.name + " version: " + packComponentVersion.getVersion(), new ImageIcon(GUIHelper.resizeImage(packComponentVersion.getComponent().iconName.image, 20)), SwingConstants.LEFT);
            componentLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            componentsPanel.add(componentLabel);
        }

        add(componentsPanel);
        revalidate();
        repaint();
    }
}
