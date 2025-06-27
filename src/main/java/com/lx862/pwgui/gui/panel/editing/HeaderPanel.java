package com.lx862.pwgui.gui.panel.editing;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.lx862.pwgui.PWGUI;
import com.lx862.pwgui.pwcore.PackFile;
import com.lx862.pwgui.pwcore.data.PackComponentVersion;
import com.lx862.pwgui.util.GUIHelper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/* The top section that displays the modpack's name and versions */
public class HeaderPanel extends JPanel {
    public HeaderPanel(PackFile packFile) {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        refresh(packFile);
    }

    public void refresh(PackFile newPackFile) {
        removeAll();

        // Prism / MMC seems to update this file to match the instance's icon on every launch
        File iconFile = newPackFile.resolveRelative("icon.png").toFile();
        if(iconFile.exists()) {
            try {
                BufferedImage iconImage = ImageIO.read(iconFile);
                if(iconImage != null) {
                    JLabel iconLabel = new JLabel();
                    iconLabel.setIcon(new ImageIcon(GUIHelper.resizeImage(iconImage, 36, 36, Image.SCALE_FAST), "icon.png from modpack"));
                    add(iconLabel);
                    add(GUIHelper.createHorizontalPadding(8));
                }
            } catch (Exception e) {
                PWGUI.LOGGER.exception(e);
            }
        }

        JPanel infoPanel = new PackInfoPanel(newPackFile);
        add(infoPanel);
        add(Box.createHorizontalGlue());
        JPanel componentsPanel = new PackComponentsPanel(newPackFile.getComponents());
        add(componentsPanel);
        updateUI();
    }

    static class PackInfoPanel extends JPanel {
        public PackInfoPanel(PackFile packFile) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            JLabel modpackNameLabel = new JLabel(packFile.getName());
            modpackNameLabel.setFont(FlatUIUtils.nonUIResource(UIManager.getFont("h2.font")));
            add(modpackNameLabel);

            if(!packFile.author.isEmpty() || !packFile.version.isEmpty()) {
                JLabel modpackVersionAuthorLabel = new JLabel();
                if(!packFile.version.isEmpty() && packFile.author.isEmpty()) {
                    modpackVersionAuthorLabel.setText(packFile.version);
                } else if(!packFile.author.isEmpty() && packFile.version.isEmpty()) {
                    modpackVersionAuthorLabel.setText(String.format("by %s", packFile.author));
                } else {
                    modpackVersionAuthorLabel.setText(String.format("%s by %s", packFile.version, packFile.author));
                }
                add(modpackVersionAuthorLabel);
            }
        }
    }

    static class PackComponentsPanel extends JPanel {
        public PackComponentsPanel(List<PackComponentVersion> components) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            for (PackComponentVersion packComponentVersion : components) {
                JLabel componentLabel = new JLabel(packComponentVersion.getComponent().iconName.name + " version: " + packComponentVersion.getVersion(), new ImageIcon(GUIHelper.clampImageSize(packComponentVersion.getComponent().iconName.image, 20)), SwingConstants.LEFT);
                componentLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
                add(componentLabel);
            }
        }
    }
}
