package com.lx862.pwgui.data.model.file;

import com.lx862.pwgui.Main;
import com.lx862.pwgui.core.PackFile;

import java.io.File;

public class ModpackConfigFileModel extends PlainTextFileModel {
    public PackFile packFile;

    public ModpackConfigFileModel(File file) {
        super(file);
        try {
            packFile = new PackFile(file.toPath());
        } catch (Exception e) {
            Main.LOGGER.exception(e);
        }
    }

    @Override
    public String getDisplayName() {
        return "Modpack Config";
    }

    @Override
    public boolean isUserFriendlyName() {
        return true;
    }
}