package com.lx862.pwgui.gui.base;

import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.util.Util;

import javax.swing.*;

public class BaseFrame extends JFrame {
    protected JMenuBar jMenuBar;

    public BaseFrame(String title) {
        super(title);
        this.jMenuBar = new JMenuBar();
        setIconImage(GUIHelper.convertImage(Util.getAssets("/icon.png")));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setJMenuBar(jMenuBar);
    }
}
