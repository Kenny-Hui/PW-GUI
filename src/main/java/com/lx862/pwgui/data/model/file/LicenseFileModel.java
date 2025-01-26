package com.lx862.pwgui.data.model.file;

import java.io.File;

public class LicenseFileModel extends PlainTextFileModel {
    public LicenseFileModel(File file) {
        super(file);
    }

    @Override
    public String getDisplayName() {
        return "License File";
    }

    @Override
    public boolean isUserFriendlyName() {
        return true;
    }
}
