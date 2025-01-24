package com.lx862.pwgui.core;

import com.lx862.pwgui.data.PackComponent;
import com.lx862.pwgui.data.PackComponentVersion;
import com.lx862.pwgui.data.Cache;
import com.lx862.pwgui.data.exception.MissingKeyPropertyException;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class PackFile extends TomlFile {
    public final Cache<PackIndexFile> packIndexFile;
    public final String packFormat;

    public String name;
    public String author;
    public String version;

    public String indexFile;
    public String indexHashFormat;
    public String indexHash;

    private PackComponentVersion versionsMinecraft;
    private PackComponentVersion versionsModloader;

    public final List<String> optionsAcceptableGameVersion;
    public String optionsDatapackFolder;

    public PackFile(Path path) {
        this(path, new Toml().read(path.toFile()));
    }

    public PackFile(Path path, Toml toml) throws MissingKeyPropertyException {
        super(path, toml);

        this.name = toml.getString("name", "");
        this.author = toml.getString("author", "");
        this.version = toml.getString("version", "");
        this.packFormat = toml.getString("pack-format", "packwiz:1.1.0");

        this.indexFile = toml.getString("index.file");
        this.indexHashFormat = toml.getString("index.hash-format");
        this.indexHash = toml.getString("index.hash");

        if(!toml.contains("versions.minecraft")) throw new MissingKeyPropertyException(path.toFile().getName(), "versions.minecraft");
        this.versionsMinecraft = new PackComponentVersion(PackComponent.MINECRAFT, toml.getString("versions.minecraft"));

        for(PackComponent component : Arrays.stream(PackComponent.values()).filter(e -> e.choosable).collect(Collectors.toList())) {
            if(toml.contains("versions." + component.slug)) {
                this.versionsModloader = new PackComponentVersion(component, toml.getString("versions." + component.slug));
                break;
            }
        }

        this.optionsAcceptableGameVersion = toml.getList("options.acceptable-game-versions");
        this.optionsDatapackFolder = toml.getString("options.datapack-folder");

        if(Files.notExists(resolveRelative(indexFile))) {
            throw new RuntimeException(String.format("%s says index file is at \"%s\", but is not found :(\nPlease double check %s.", this.path.getFileName(), this.indexFile, this.path.getFileName()));
        }
        this.packIndexFile = new Cache<>(() -> new PackIndexFile(getIndexPath()));
    }

    public String getName() {
        return this.name.isEmpty() ? "export" : this.name;
    }

    public List<PackComponentVersion> getComponents() {
        List<PackComponentVersion> list = new ArrayList<>();
        list.add(versionsMinecraft);
        if(versionsModloader != null) list.add(versionsModloader);
        return list;
    }

    public PackComponentVersion getComponent(PackComponent component) {
        return getComponents().stream().filter(e -> e.getComponent() == component).findFirst().orElse(null);
    }

    public List<String> getOptionAcceptableGameVersion(boolean includeSelfVersion) {
        List<String> versionList = optionsAcceptableGameVersion == null ? new ArrayList<>() : new ArrayList<>(optionsAcceptableGameVersion);

        if(includeSelfVersion && !versionList.contains(versionsMinecraft.getVersion())) {
            versionList.add(versionsMinecraft.getVersion());
        }
        return versionList;
    }

    public Path resolveRelative(String path) {
        return this.path.getParent().resolve(path);
    }

    public Path getIndexPath() {
        return resolveRelative(indexFile);
    }

    public Path getDatapackPath() {
        return this.optionsDatapackFolder == null ? null : resolveRelative(optionsDatapackFolder);
    }

    public void setMinecraft(PackComponentVersion newComponent) {
        this.versionsMinecraft = newComponent;
    }

    public PackComponentVersion getModloader() {
        return versionsModloader;
    }

    public void setModloader(PackComponentVersion newComponent) {
        this.versionsModloader = newComponent;
    }

    @Override
    public void write() throws IOException {
        Map<String, Object> map = toml.toMap();
        map.put("name", this.name);
        map.put("author", this.author);
        map.put("version", this.version);

        Map<String, Object> versionsMap = (Map<String, Object>) map.getOrDefault("versions", new HashMap<>());
        versionsMap.put("minecraft", getComponent(PackComponent.MINECRAFT).getVersion());
        if(versionsModloader != null) versionsMap.put(versionsModloader.getComponent().slug, versionsModloader.getVersion());
        map.put("versions", versionsMap);

        Map<String, Object> optionsMap = (Map<String, Object>) map.getOrDefault("options", new HashMap<>());
        optionsMap.put("acceptable-game-versions", getOptionAcceptableGameVersion(false));
        map.put("options", optionsMap);

        writeToFilesystem(new TomlWriter().write(map));
    }
}
