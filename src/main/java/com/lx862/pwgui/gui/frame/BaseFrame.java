package com.lx862.pwgui.gui.frame;

import com.lx862.pwgui.gui.action.ClearPackwizCacheAction;
import com.lx862.pwgui.gui.components.kui.KMenu;
import com.lx862.pwgui.gui.components.kui.KMenuItem;
import com.lx862.pwgui.gui.popup.ViewLogDialog;
import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.event.KeyEvent;

public abstract class BaseFrame extends JFrame {
    protected JMenuBar jMenuBar;

    public BaseFrame(String title) {
        super(title);
        this.jMenuBar = new JMenuBar();
        setIconImage(GUIHelper.convertImage(Util.getAssets("/assets/icon.png")));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setJMenuBar(jMenuBar);
    }

    protected KMenu getHelpMenu() {
        KMenu helpMenu = new KMenu("Help");

        KMenuItem clearPWCacheItem = new KMenuItem(new ClearPackwizCacheAction(this));
        helpMenu.add(clearPWCacheItem);

        KMenuItem viewLogMenuItem = new KMenuItem("View Log");
        viewLogMenuItem.setMnemonic(KeyEvent.VK_V);
        viewLogMenuItem.addActionListener(actionEvent -> {
            ViewLogDialog logViewer = new ViewLogDialog(this);
            logViewer.setVisible(true);
        });
        helpMenu.add(viewLogMenuItem);

        KMenuItem aboutMenuItem = new KMenuItem("About"); // TODO: Implement about dialog
        aboutMenuItem.setMnemonic(KeyEvent.VK_A);
        helpMenu.add(aboutMenuItem);

        return helpMenu;
    }
}
