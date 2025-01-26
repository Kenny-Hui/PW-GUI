package com.lx862.pwgui.gui.panel.editing;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.lx862.pwgui.core.PackFile;
import com.lx862.pwgui.data.PackComponentVersion;
import com.lx862.pwgui.util.GUIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/* The top section that displays the modpack's name and versions */
class HeaderPanel extends JPanel {
    public HeaderPanel(PackFile packFile) {
        setBorder(new EmptyBorder(0, 0, 10, 0));
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

        JLabel modpackVersionAuthorLabel = new JLabel();
        if (!packFile.author.isEmpty()) {
            modpackVersionAuthorLabel.setText(packFile.version + " by " + packFile.author);
        } else {
            modpackVersionAuthorLabel.setText(packFile.version);
        }
        infoPanel.add(modpackVersionAuthorLabel);
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
