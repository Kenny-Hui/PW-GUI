package com.lx862.pwgui.data.model.file;

import java.io.File;

public class ModpackIndexFileModel extends PlainTextFileModel {
    public ModpackIndexFileModel(File file) {
        super(file);
    }

    @Override
    public String getDisplayName() {
        return "Packwiz Index";
    }

    @Override
    public boolean isUserFriendlyName() {
        return true;
    }
}