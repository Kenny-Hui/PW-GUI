package com.lx862.pwgui.gui.panel.editing.filetype;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

public abstract class FileTypePanel extends JPanel {
    private final FileEntryPaneContext context;

    public FileTypePanel(FileEntryPaneContext context) {
        super();
        this.context = context;
        setBorder(new EmptyBorder(6, 6, 6, 6));
    }

    public boolean shouldSave() {
        return false;
    }

    public boolean savable() {
        return false;
    }

    protected void updateSaveState() {
        context.setShouldSave(shouldSave());
    }
    
    public void save(Component parent) throws IOException {
        context.setShouldSave(false);
    }
}
