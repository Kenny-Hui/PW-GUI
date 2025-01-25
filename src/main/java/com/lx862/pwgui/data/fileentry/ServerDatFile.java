package com.lx862.pwgui.data.fileentry;

import java.io.File;

public class ServerDatFile extends FileSystemEntityEntry {

    public ServerDatFile(File file) {
        super(file);
    }

    @Override
    public String getTreeDisplayName() {
        return "Minecraft Servers";
    }

    @Override
    public boolean isNameModified() {
        return true;
    }
}