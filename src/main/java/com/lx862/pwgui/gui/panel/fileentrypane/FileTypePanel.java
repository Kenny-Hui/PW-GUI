package com.lx862.pwgui.gui.panel.fileentrypane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

public abstract class FileTypePanel extends JPanel {
    public FileTypePanel() {
        super();
        setBorder(new EmptyBorder(6, 6, 6, 6));
    }

    public boolean autoSaveOnExit() {
        return true;
    }

    public boolean shouldSave() {
        return false;
    }
    
    public void save(Component parent) throws IOException {
    }

    public void unload() {
    }
}
