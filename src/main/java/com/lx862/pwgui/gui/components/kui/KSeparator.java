package com.lx862.pwgui.gui.components.kui;

import javax.swing.*;
import java.awt.*;

/* A JSeparator with it's height limited to 1 */
public class KSeparator extends JSeparator {
    public KSeparator() {
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
    }
}
