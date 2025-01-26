package com.lx862.pwgui.data.model.file;

import java.io.File;

public class GenericFileModel extends FileSystemEntityModel {
    public final long fileSize;
    public final long lastModified;

    public GenericFileModel(File file) {
        super(file);
        this.fileSize = file.length();
        this.lastModified = file.lastModified();
    }
}
