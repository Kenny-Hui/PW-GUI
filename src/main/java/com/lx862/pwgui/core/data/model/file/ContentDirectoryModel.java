package com.lx862.pwgui.core.data.model.file;

import java.io.File;
import java.util.HashMap;

public class ContentDirectoryModel extends DirectoryModel {
    private static final HashMap<String, String> directoryToNameMap = new HashMap<>();

    static {
        directoryToNameMap.put("mods", "Mods");
        directoryToNameMap.put("resourcepacks", "Resource Packs");
        directoryToNameMap.put("shaderpacks", "Shader Packs");
        directoryToNameMap.put("plugins", "Plugins");
    }

    public ContentDirectoryModel(File file) {
        super(file);
    }

    @Override
    public String getDisplayName() {
        String fileName = path.toFile().getName();
        return directoryToNameMap.getOrDefault(fileName, fileName);
    }

    @Override
    public boolean isUserFriendlyName() {
        return true;
    }
}