package com.lx862.pwgui.data.model.file;

import com.lx862.pwgui.Main;
import com.lx862.pwgui.core.PackwizMetaFile;

import java.io.File;

public class PackMetadataFileModel extends PlainTextFileModel {
    private PackwizMetaFile packwizMetaFile;

    public PackMetadataFileModel(File file) {
        super(file);

        try {
            packwizMetaFile = new PackwizMetaFile(file.toPath());
        } catch (Exception e) {
            Main.LOGGER.exception(e);
        }
    }

    @Override
    public String getDisplayName() {
        return packwizMetaFile.name;
    }

    @Override
    public boolean isUserFriendlyName() {
        return true;
    }

    public PackwizMetaFile getPackMetadata() {
        return packwizMetaFile;
    }
}