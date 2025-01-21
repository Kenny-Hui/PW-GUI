package com.lx862.pwgui.gui.base.kui;

import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.util.Util;

import javax.swing.*;

public class KCollapsibleToggle extends JCheckBox {
    private final String collapsedText;
    private final String expandedText;

    public KCollapsibleToggle(String collapsedText, String expandedText) {
        this.collapsedText = collapsedText;
        this.expandedText = expandedText;

        updateToggle();

        addItemListener(itemEvent -> {
            updateToggle();
        });
    }

    private void updateToggle() {
        setIcon(new ImageIcon(GUIHelper.convertImage(Util.getAssets(isSelected() ? "/ui/up_arrow.png" : "/ui/down_arrow.png"), 10)));
        setText(isSelected() ? expandedText : collapsedText);
    }
}
