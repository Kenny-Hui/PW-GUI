package com.lx862.pwgui.data.model.file;

import java.io.File;

public class GitIgnoreFileModel extends PlainTextFileModel {
    public GitIgnoreFileModel(File file) {
        super(file);
    }

    @Override
    public String getDisplayName() {
        return "Git Ignore list";
    }

    @Override
    public boolean isUserFriendlyName() {
        return true;
    }
}
