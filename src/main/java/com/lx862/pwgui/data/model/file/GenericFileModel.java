package com.lx862.pwgui.data.model.file;

import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.io.File;

public class GenericFileModel extends FileSystemEntityModel {
    public final long fileSize;
    public final long lastModified;

    public GenericFileModel(File file) {
        super(file);
        this.fileSize = file.length();
        this.lastModified = file.lastModified();
    }

    @Override
    public Icon getIcon() {
        return UIManager.getIcon("FileView.fileIcon");
    }
}
