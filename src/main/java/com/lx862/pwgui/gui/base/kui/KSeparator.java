package com.lx862.pwgui.gui.base.kui;

import javax.swing.*;
import java.awt.*;

/* This limits the seperator height to 1 */
public class KSeparator extends JSeparator {
    public KSeparator() {
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
    }
}
