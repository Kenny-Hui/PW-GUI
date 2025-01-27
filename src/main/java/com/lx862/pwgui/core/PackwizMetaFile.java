package com.lx862.pwgui.core;

import com.lx862.pwgui.data.exception.MissingKeyPropertyException;
import com.moandjiezana.toml.Toml;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/* This represents the metadata file in Packwiz (*.pw.toml) */
public class PackwizMetaFile extends TomlFile {
    public final String name;
    public final String fileName;
    public String side;

    public final String downloadUrl;
    public final String downloadMode;
    public final String downloadHashFormat;
    public final String downloadHash;

    public final long updateCfFileId;
    public final long updateCfProjectId;

    public final String updateMrModId;
    public final String updateMrVersion;

    public final String updateGhBranch;
    public final String updateGhRegex;
    public final String updateGhSlug;
    public final String updateGhTag;

    public boolean pinned;
    public String optionDescription;
    public boolean optionOptional;
    public boolean optionDefault;

    private final String slug; // The file name without .pw.toml

    public PackwizMetaFile(Path path) {
        this(path, new Toml().read(path.toFile()));
    }

    public PackwizMetaFile(Path path, Toml toml) throws MissingKeyPropertyException {
        super(path, toml);

        this.slug = path.toFile().getName().replace(".pw.toml", "");

        this.name = toml.getString("name");
        if(name == null) throw new MissingKeyPropertyException(path.toFile().getName(), "name");

        this.fileName = toml.getString("filename");
        if(fileName == null) throw new MissingKeyPropertyException(path.toFile().getName(), "filename");

        this.side = toml.getString("side");
        this.pinned = toml.getBoolean("pin", false);

        this.downloadHashFormat = toml.getString("download.hash-format");
        if(this.downloadHashFormat == null) throw new MissingKeyPropertyException(path.toFile().getName(), "download.hash-format");
        this.downloadHash = toml.getString("download.hash");
        if(this.downloadHash == null) throw new MissingKeyPropertyException(path.toFile().getName(), "download.hash");
        this.downloadUrl = toml.getString("download.url");
        this.downloadMode = toml.getString("download.mode");

        this.updateCfFileId = toml.getLong("update.curseforge.file-id", -1L);
        this.updateCfProjectId = toml.getLong("update.curseforge.project-id", -1L);

        this.updateMrModId = toml.getString("update.modrinth.mod-id");
        this.updateMrVersion = toml.getString("update.modrinth.version");

        this.updateGhBranch = toml.getString("update.github.branch");
        this.updateGhRegex = toml.getString("update.github.regex");
        this.updateGhSlug = toml.getString("update.github.slug");
        this.updateGhTag = toml.getString("update.github.tag");

        this.optionDescription = toml.getString("option.description");
        this.optionOptional = toml.getBoolean("option.optional", false);
        this.optionDefault = toml.getBoolean("option.default", false);
    }

    public boolean isClientSide() {
        return this.side == null || this.side.equals("client") || this.side.equals("both");
    }

    public boolean isServerSide() {
        return this.side == null || this.side.equals("server") || this.side.equals("both");
    }

    public String getSlug() {
        return slug;
    }

    @Override
    public void write(String reason) throws IOException {
        Map<String, Object> map = this.toml.toMap();
        map.put("side", this.side);

        if(pinned) {
            map.put("pin", true);
        } else {
            map.remove("pin");
        }

        // Option category
        map.remove("option"); // We rewrite the entire section
        if(this.optionDescription != null || this.optionOptional) {
            Map<String, Object> newOptions = new HashMap<>();
            if(optionDescription != null) {
                newOptions.put("description", this.optionDescription);
            }
            if(optionOptional) {
                newOptions.put("optional", true);
            }

            if(optionDefault) {
                newOptions.put("default", true);
            }

            map.put("option", newOptions);
        }

        writeToFilesystem(map);
        super.write(reason);
    }
}
