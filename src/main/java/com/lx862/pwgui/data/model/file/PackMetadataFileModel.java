package com.lx862.pwgui.data.model.file;

import com.lx862.pwgui.PWGUI;
import com.lx862.pwgui.pwcore.PackwizMetaFile;

import java.io.File;

public class PackMetadataFileModel extends PlainTextFileModel {
    private final PackwizMetaFile packwizMetaFile;

    public PackMetadataFileModel(File file) {
        super(file);
        packwizMetaFile = new PackwizMetaFile(file.toPath());
    }

    @Override
    public String getDisplayName() {
        return PWGUI.getConfig().showMetaFileName.getValue() ? name : packwizMetaFile.name;
    }

    @Override
    public boolean isUserFriendlyName() {
        return !PWGUI.getConfig().showMetaFileName.getValue();
    }

    public PackwizMetaFile getPackMetadata() {
        return packwizMetaFile;
    }
}