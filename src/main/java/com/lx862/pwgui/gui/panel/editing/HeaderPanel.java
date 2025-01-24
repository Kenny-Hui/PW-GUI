package com.lx862.pwgui.gui.panel.editing;

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

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        JLabel modpackNameLabel = new JLabel(packFile.getName());
        modpackNameLabel.setFont(UIManager.getFont("h2.font"));
        leftPanel.add(modpackNameLabel);

        JLabel modpackVersionAuthorLabel = new JLabel();
        if (!packFile.author.isEmpty()) {
            modpackVersionAuthorLabel.setText(packFile.version + " by " + packFile.author);
        } else {
            modpackVersionAuthorLabel.setText(packFile.version);
        }
        leftPanel.add(modpackVersionAuthorLabel);

        add(leftPanel);
        add(Box.createHorizontalGlue());

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        for (PackComponentVersion packComponentVersion : packFile.getComponents()) {
            final JLabel componentLabel = new JLabel(packComponentVersion.getComponent().iconName.name + " version: " + packComponentVersion.getVersion(), new ImageIcon(GUIHelper.resizeImage(packComponentVersion.getComponent().iconName.image, 20)), SwingConstants.LEFT);
            componentLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            rightPanel.add(componentLabel);
        }

        add(rightPanel);
        revalidate();
        repaint();
    }
}
