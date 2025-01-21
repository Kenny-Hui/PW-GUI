package com.lx862.pwgui.gui.base;

import javax.swing.*;

public class BaseFrame extends JFrame {
    protected JMenuBar jMenuBar;

    public BaseFrame(String title) {
        super(title);
        this.jMenuBar = new JMenuBar();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setJMenuBar(jMenuBar);
    }
}
