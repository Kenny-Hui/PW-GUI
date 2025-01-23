package com.lx862.pwgui.gui.panel.editing.filetype;

import com.lx862.pwgui.core.Modpack;
import com.lx862.pwgui.data.FileIgnoreRules;
import com.lx862.pwgui.gui.panel.editing.EditPanel;

import java.util.function.Consumer;

public class FileEntryPaneContext {
    private final Modpack modpack;
    private final EditPanel parent;
    private final Consumer<FileIgnoreRules> setTreeIgnorePattern;
    private final Consumer<Boolean> shouldSave;

    public FileEntryPaneContext(EditPanel parent, Modpack modpack, Consumer<FileIgnoreRules> setTreeIgnorePattern, Consumer<Boolean> setSaveCallback) {
        this.parent = parent;
        this.modpack = modpack;
        this.setTreeIgnorePattern = setTreeIgnorePattern;
        this.shouldSave = setSaveCallback;
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

    public void setShouldSave(boolean bl) {
        shouldSave.accept(bl);
    }
}
