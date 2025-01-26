package com.lx862.pwgui.data.model.file;

import java.io.File;

public class MinecraftOptionsFileModel extends PlainTextFileModel {
    public MinecraftOptionsFileModel(File file) {
        super(file);
    }

    @Override
    public String getDisplayName() {
        return "Minecraft Options File";
    }

    @Override
    public boolean isUserFriendlyName() {
        return true;
    }
}
