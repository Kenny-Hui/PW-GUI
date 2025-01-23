package com.lx862.pwgui.core;

import com.lx862.pwgui.data.PackComponent;
import com.lx862.pwgui.data.PackComponentVersion;
import com.lx862.pwgui.data.Cache;
import com.lx862.pwgui.data.exception.MissingKeyPropertyException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PackFile extends TomlFile {
    public final Cache<PackIndexFile> packIndexFile;
    public final String packFormat;

    public String name;
    public String author;
    public String version;

    public String indexFile;
    public String indexHashFormat;
    public String indexHash;

    public String versionsFabric;
    public String versionsForge;
    public String versionsNeoforge;
    public String versionsLiteloader;
    public String versionsQuilt;
    public String versionsMinecraft;

    public final List<String> optionAcceptableGameVersion;
    public String optionDatapackFolder;

    public PackFile(Path path) throws MissingKeyPropertyException {
        super(path);

        this.name = toml.getString("name", "");

        this.author = toml.getString("author");
        this.version = toml.getString("version");
        if(version == null) throw new MissingKeyPropertyException(path.toFile().getName(), "version");
        this.packFormat = toml.getString("pack-format", "packwiz:1.1.0");

        this.indexFile = toml.getString("index.file");
        this.indexHashFormat = toml.getString("index.hash-format");
        this.indexHash = toml.getString("index.hash");

        this.versionsMinecraft = toml.getString("versions.minecraft");
        if(versionsMinecraft == null) throw new MissingKeyPropertyException(path.toFile().getName(), "versions.minecraft");
        this.versionsFabric = toml.getString("versions.fabric");
        this.versionsForge = toml.getString("versions.forge");
        this.versionsNeoforge = toml.getString("versions.neoforge");
        this.versionsLiteloader = toml.getString("versions.liteloader");
        this.versionsQuilt = toml.getString("versions.quilt");

        this.optionAcceptableGameVersion = toml.getList("options.acceptable-game-versions");
        this.optionDatapackFolder = toml.getString("options.datapack-folder");

        if(Files.notExists(resolveRelative(indexFile))) {
            throw new RuntimeException(String.format("%s says index file is at \"%s\", but is not found :(\nPlease double check %s.", this.path.getFileName(), this.indexFile, this.path.getFileName()));
        }
        this.packIndexFile = new Cache<>(() -> new PackIndexFile(getIndexPath()));
    }

    public List<PackComponentVersion> getComponents() {
        List<PackComponentVersion> list = new ArrayList<>();
        list.add(new PackComponentVersion(PackComponent.MINECRAFT, versionsMinecraft));
        if(versionsFabric != null) list.add(new PackComponentVersion(PackComponent.FABRIC, versionsFabric));
        if(versionsForge != null) list.add(new PackComponentVersion(PackComponent.FORGE, versionsForge));
        if(versionsNeoforge != null) list.add(new PackComponentVersion(PackComponent.NEOFORGE, versionsNeoforge));
        if(versionsLiteloader != null) list.add(new PackComponentVersion(PackComponent.LITELOADER, versionsLiteloader));
        if(versionsQuilt != null) list.add(new PackComponentVersion(PackComponent.QUILT, versionsQuilt));
        return list;
    }

    public PackComponentVersion getComponent(PackComponent component) {
        return getComponents().stream().filter(e -> e.component == component).findFirst().orElse(null);
    }

    public List<String> getOptionAcceptableGameVersion(boolean includeSelfVersion) {
        List<String> list = optionAcceptableGameVersion;
        if(list == null) list = new ArrayList<>();
        if(includeSelfVersion && !list.contains(versionsMinecraft)) list.add(versionsMinecraft);
        return list;
    }

    public Path resolveRelative(String path) {
        return this.path.getParent().resolve(path);
    }

    public Path getIndexPath() {
        return resolveRelative(indexFile);
    }

    public String getModloaderVersion() {
        return versionsFabric != null ? versionsFabric : versionsForge != null ? versionsForge : versionsNeoforge != null ? versionsNeoforge : versionsLiteloader != null ? versionsLiteloader : "Unknown";
    }

    @Override
    public void write() throws IOException {

    }
}
