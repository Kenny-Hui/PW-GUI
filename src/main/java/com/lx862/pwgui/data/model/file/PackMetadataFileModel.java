package com.lx862.pwgui.data.model.file;

import com.lx862.pwgui.Main;
import com.lx862.pwgui.core.PackwizMetaFile;

import java.io.File;

public class PackMetadataFileModel extends PlainTextFileModel {
    private final PackwizMetaFile packwizMetaFile;

    public PackMetadataFileModel(File file) {
        super(file);
        packwizMetaFile = new PackwizMetaFile(file.toPath());
    }

    @Override
    public String getDisplayName() {
        return Main.getConfig().showMetaFileName.getValue() ? name : packwizMetaFile.name;
    }

    @Override
    public boolean isUserFriendlyName() {
        return !Main.getConfig().showMetaFileName.getValue();
    }

    public PackwizMetaFile getPackMetadata() {
        return packwizMetaFile;
    }
}