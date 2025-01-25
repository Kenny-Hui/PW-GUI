package com.lx862.pwgui.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class GUIHelper {
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

    /* Returns a new image with the specified opacity */
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
            e.printStackTrace();
            return null;
        }
    }
}
