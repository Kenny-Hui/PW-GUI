package com.lx862.pwgui.gui.components;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class ModDetailListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> jList, Object item, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(jList, item, index, isSelected, cellHasFocus);
        setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(128, 128, 128)),
                new EmptyBorder(3, 3, 3, 3)
        ));

        if (item instanceof String) {
            String str = (String) item;
            String[] split = str.split(" -> ");
            String newVersion = split[split.length - 1];
            String firstHalf = split[0];
            String name = firstHalf.substring(0, firstHalf.lastIndexOf(":"));
            String oldVersion = firstHalf.substring(name.length() + 2);

            setText(String.format("<html><b>%s</b><br><b>Old:</b> %s<br><b>New:</b> %s</html>", name, oldVersion, newVersion));
        }

        return this;
    }
}
