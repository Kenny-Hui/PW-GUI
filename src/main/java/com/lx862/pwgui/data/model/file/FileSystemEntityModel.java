package com.lx862.pwgui.data.model.file;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;

public abstract class FileSystemEntityModel {
    public final String name;
    public final Path path;

    public FileSystemEntityModel(File file) {
        this.path = file.toPath();
        this.name = file.getName();
    }

    public String getDisplayName() {
        return name;
    }

    public boolean isUserFriendlyName() {
        return false;
    }

    public abstract Icon getIcon();
}
