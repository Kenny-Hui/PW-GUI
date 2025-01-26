package com.lx862.pwgui.data.model.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class PlainTextFileModel extends GenericFileModel {
    private String cachedContent;

    public PlainTextFileModel(File file) {
        super(file);
    }

    public String getContent() throws IOException {
        if(cachedContent == null) {
            cachedContent = String.join("\n", Files.readAllLines(path));
        }
        return cachedContent;
    }
}