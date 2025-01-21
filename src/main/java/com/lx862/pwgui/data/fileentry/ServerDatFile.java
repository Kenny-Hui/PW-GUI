package com.lx862.pwgui.data.fileentry;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;

import java.io.File;

public class ServerDatFile extends FileSystemEntityEntry {
    private NamedTag deserializedTag;

    public ServerDatFile(File file) {
        super(file);
        try {
            this.deserializedTag = NBTUtil.read(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NamedTag getTag() {
        return deserializedTag;
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