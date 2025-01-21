package com.lx862.pwgui.gui.panel.fileentrypane;

import com.lx862.pwgui.core.Modpack;
import com.lx862.pwgui.data.FileIgnoreRules;
import com.lx862.pwgui.gui.panel.EditPanel;

import java.util.function.Consumer;

public class FileEntryPaneContext {
    private final Modpack modpack;
    private final EditPanel parent;
    private final Consumer<FileIgnoreRules> setTreeIgnorePattern;

    public FileEntryPaneContext(EditPanel parent, Modpack modpack, Consumer<FileIgnoreRules> setTreeIgnorePattern) {
        this.parent = parent;
        this.modpack = modpack;
        this.setTreeIgnorePattern = setTreeIgnorePattern;
    }

    public Modpack getModpack() {
        return this.modpack;
    }

    public EditPanel getParent() {
        return this.parent;
    }

    public void invokeSetTreeIgnorePattern(FileIgnoreRules ignore) {
        setTreeIgnorePattern.accept(ignore);
    }
}
