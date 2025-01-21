package com.lx862.pwgui.gui.base;

import java.awt.*;

/* A class that represents both a title and the GUI component */
public class NameTabPair {
    public final String title;
    public final Component component;

    public NameTabPair(String title, Component component) {
        this.title = title;
        this.component = component;
    }
}
