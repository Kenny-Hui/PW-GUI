package com.lx862.pwgui.gui.base;

import javax.swing.*;

public class ToggleListSelectionModel extends DefaultListSelectionModel {
    @Override
    public void setSelectionInterval(int index0, int index1) {
        if (isSelectedIndex(index0)) {
            super.removeSelectionInterval(index0, index1);
        } else {
            super.addSelectionInterval(index0, index1);
        }
    }
}
