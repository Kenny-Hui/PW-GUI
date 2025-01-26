package com.lx862.pwgui.util;

import com.formdev.flatlaf.FlatLaf;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.data.ApplicationTheme;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class GUIHelper {
    public static void setupApplicationTheme(ApplicationTheme applicationTheme, Window window) {
        SwingUtilities.invokeLater(() -> {
            FlatLaf.setup(applicationTheme.getLaf());

            UIManager.put("Component.focusWidth", 1);
            UIManager.put("ScrollBar.showButtons", true);
            UIManager.put("ScrollBar.width", 14);
            UIManager.put("TabbedPane.showTabSeparators", true);
            UIManager.put("Button.arc", 9);
            UIManager.put("TextComponent.arc", 5);
            UIManager.put("Component.hideMnemonics", false);

            if(window == null) { // Every window
                for(Window subWindow : Window.getWindows()) {
                    SwingUtilities.updateComponentTreeUI(subWindow);
                }
            } else {
                SwingUtilities.updateComponentTreeUI(window);
            }
        });
    }

    public static Component createVerticalPadding(int height) {
        return Box.createRigidArea(new Dimension(0, height));
    }

    public static Image resizeImage(Image img, int size) {
        double imgWidth = img.getWidth(null);
        double imgHeight = img.getHeight(null);
        double ratioW = 1;
        double ratioH = 1;
        if(imgWidth > size) {
            ratioW = size / imgWidth;
        }
        if(imgHeight > size) {
            ratioH = size / imgHeight;
        }
        double minRatio = Math.min(ratioW, ratioH);

        return img.getScaledInstance((int)(imgWidth * minRatio), (int)(imgHeight * minRatio), Image.SCALE_SMOOTH);
    }

    /** Returns a new image with the specified opacity */
    public static Image imageOpacity(Image image, float opacity) {
        BufferedImage newImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D)newImage.getGraphics();
        g2d.setComposite(AlphaComposite.SrcOver.derive(opacity));
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return newImage;
    }

    public static Image convertImage(InputStream is, int size) {
        Image img = convertImage(is);
        if(img != null) {
            return resizeImage(img, size);
        }
        return null;
    }

    public static Image convertImage(InputStream is) {
        try {
            return ImageIO.read(is);
        } catch (Exception e) {
            Main.LOGGER.exception(e);
            return null;
        }
    }
}
