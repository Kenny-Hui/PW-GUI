package com.lx862.pwgui.data.model.file;

import javax.swing.*;
import java.io.File;

public class DirectoryModel extends FileSystemEntityModel {
    public DirectoryModel(File file) {
        super(file);
    }

    @Override
    public Icon getIcon() {
        return UIManager.getIcon("FileView.directoryIcon");
    }
}
