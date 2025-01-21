package com.lx862.pwgui.core;

import com.moandjiezana.toml.Toml;
import com.lx862.pwgui.Main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class TomlFile {
    protected final Path path;
    protected final Toml toml;

    public TomlFile(Path path) {
        this.path = path;
        this.toml = new Toml().read(path.toFile());
    }

    public TomlFile(Path path, String tomlString) {
        this.path = path;
        this.toml = new Toml().read(tomlString);
    }

    public Path getPath() {
        return this.path;
    }

    public abstract void write() throws IOException;

    protected void writeToFilesystem(String str) throws IOException {
        Files.write(path, str.getBytes(StandardCharsets.UTF_8));
        Main.LOGGER.info(String.format("Wrote TOML file to %s", path));
    }
}
