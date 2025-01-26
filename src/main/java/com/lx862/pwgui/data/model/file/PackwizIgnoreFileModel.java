package com.lx862.pwgui.data.model.file;

import java.io.File;

public class PackwizIgnoreFileModel extends PlainTextFileModel {
    public PackwizIgnoreFileModel(File file) {
        super(file);
    }

    @Override
    public String getDisplayName() {
        return "Packwiz Ignore list";
    }

    @Override
    public boolean isUserFriendlyName() {
        return true;
    }
}
