package com.lx862.pwgui.core;

import com.lx862.pwgui.Main;

import java.io.IOException;
import java.nio.file.Path;

public abstract class WritableFile {
    private final Path path;

    public WritableFile(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return this.path;
    }

    public void write(String reason) throws IOException {
        Main.LOGGER.info(String.format("Writing file to \"%s\" due to \"%s\"", path.toString(), reason));
    }
}
