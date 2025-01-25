package com.lx862.pwgui.core;

import com.moandjiezana.toml.Toml;
import com.lx862.pwgui.Main;
import com.moandjiezana.toml.TomlWriter;

import java.io.IOException;
import java.nio.file.Path;

public abstract class TomlFile {
    protected final Path path;
    protected final Toml toml;

    public TomlFile(Path path, Toml toml) {
        this.path = path;
        this.toml = new Toml().read(toml);
    }

    public Path getPath() {
        return this.path;
    }

    public Toml getToml() {
        return this.toml;
    }

    public abstract void write() throws IOException;

    protected void writeToFilesystem(Object o) throws IOException {
        new TomlWriter().write(o, path.toFile());
        Main.LOGGER.info(String.format("Wrote TOML file to %s", path));
    }
}
