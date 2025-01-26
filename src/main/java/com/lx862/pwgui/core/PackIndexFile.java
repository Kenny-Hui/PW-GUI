package com.lx862.pwgui.core;

import com.moandjiezana.toml.Toml;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class PackIndexFile extends TomlFile {
    private final List<FileEntry> fileEntries;
    private final String hashFormat;

    public PackIndexFile(Path path) {
        this(path, new Toml().read(path.toFile()));
    }

    public PackIndexFile(Path path, Toml toml) {
        super(path, toml);

        this.fileEntries = new ArrayList<>();
        this.hashFormat = toml.getString("hash-format");

        List<Toml> entries = toml.getTables("files");
        if(entries == null) return;
        for(Toml entryToml : entries) {
            fileEntries.add(new FileEntry(entryToml, this.hashFormat, path));
        }
    }

    @Override
    public void write(String reason) throws IOException {
        Map<String, Object> map = toml.toMap();
        map.put("hash-format", this.hashFormat);

        List<Map<String, Object>> entries = new ArrayList<>();
        for(FileEntry entry : fileEntries) {
            Map<String, Object> entryMap = new HashMap<>();
            entryMap.put("file", entry.file);
            entryMap.put("hash", entry.hash);
            if(entry.alias != null) entryMap.put("alias", entry.alias);
            if(!this.hashFormat.equals(entry.hashFormat)) entryMap.put("hash-format", entry.hashFormat);
            if(entry.metafile) entryMap.put("metafile", entry.metafile);
            if(entry.preserve) entryMap.put("preserve", entry.preserve);

            entries.add(entryMap);
        }
        map.put("files", entries);
        writeToFilesystem(map);
        super.write(reason);
    }

    public FileEntry getEntryByPath(Path path) {
        for(FileEntry entry : fileEntries) {
            if(entry.path.equals(path)) return entry;
        }
        return null;
    }

    public void updateEntry(FileEntry newFileEntry) {
        FileEntry originalEntry = getEntryByPath(newFileEntry.path);
        if(originalEntry == null) throw new IllegalArgumentException(String.format("Index file does not contain entry: %s", newFileEntry.path));
        int index = fileEntries.indexOf(originalEntry);
        fileEntries.set(index, newFileEntry);
    }

    public static class FileEntry {
        public final Path path;
        public final String file;
        public final String hash;
        public final String hashFormat;
        public String alias;
        public boolean metafile;
        public boolean preserve;

        public FileEntry(Toml toml, String indexHashFormat, Path indexFilePath) {
            this.file = toml.getString("file");
            this.path = indexFilePath.getParent().resolve(this.file);
            this.hash = toml.getString("hash");
            this.alias = toml.getString("alias");
            this.hashFormat = toml.getString("hash-format") == null ? indexHashFormat : toml.getString("hash-format");
            this.preserve = toml.getBoolean("preserve", false);
            this.metafile = toml.getBoolean("metafile", false);
        }

        @Override
        public boolean equals(Object o) {
            if(o == this) return true;
            if(!(o instanceof FileEntry)) return false;

            return ((FileEntry)o).path.equals(path);
        }
    }
}