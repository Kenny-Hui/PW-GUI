package com.lx862.pwgui.data.fileentry;

import com.lx862.pwgui.gui.base.NameTabPair;
import com.lx862.pwgui.gui.panel.fileentrypane.FileEntryPaneContext;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileSystemEntityEntry {
    public final String name;
    public final Path path;

    public FileSystemEntityEntry(File file) {
        this.path = file.toPath();
        this.name = file.getName();
    }

    public List<NameTabPair> getInspectPanels(FileEntryPaneContext context) {
        return new ArrayList<>();
    }

    public String getTreeDisplayName() {
        return name;
    }

    public boolean isNameModified() {
        return false;
    }

    protected <T> List<T> addToList(List<T> existingList, T item) {
        existingList.add(item);
        return existingList;
    }
}
