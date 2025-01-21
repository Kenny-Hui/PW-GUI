package com.lx862.pwgui.gui.panel.fileentrypane;

import com.lx862.pwgui.gui.base.kui.KImageDisplayPanel;
import com.lx862.pwgui.data.fileentry.GenericFileEntry;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends FileTypePanel {
    public ImagePanel(GenericFileEntry fileEntry) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        KImageDisplayPanel imagePreviewPanel = new KImageDisplayPanel(fileEntry.path.toFile());
        imagePreviewPanel.setAlignmentX(LEFT_ALIGNMENT);

        BufferedImage image = imagePreviewPanel.getImage();
        if(image != null) {
            JLabel desc = new JLabel(String.format("Dimensions: %dx%d", image.getWidth(), image.getHeight()));
            desc.setAlignmentX(LEFT_ALIGNMENT);
            add(desc);
        }

        add(imagePreviewPanel);
    }
}
