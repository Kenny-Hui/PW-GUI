package com.lx862.pwgui.core;

import com.lx862.pwgui.data.Cache;
import com.lx862.pwgui.data.FileIgnoreRules;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Modpack {
    private final Path root;
    public final Cache<PackFile> packFile;
    public final FileIgnoreRules defaultFileIgnoreRules;

    public Modpack(Path packFilePath) throws FileNotFoundException {
        this.root = packFilePath.getParent();
        this.defaultFileIgnoreRules = new FileIgnoreRules(
            // Sourced from https://github.com/packwiz/packwiz/blob/0626c00149a8d9a5e9f76e5640e7b8b95c064350/core/index.go#L131-L151
            new String[]{
                ".git/**",
                ".gitattributes",
                ".gitignore",

                // Exclude macOS metadata
                ".DS_Store",

                // Exclude exported CurseForge zip files
                "/*.zip",

                // Exclude exported Modrinth packs
                "*.mrpack",

                // Exclude packwiz binaries, if the user puts them in their pack folder
                "packwiz.exe",
                "packwiz"
            }
        );

        if(Files.isDirectory(packFilePath) || Files.notExists(packFilePath)) throw new FileNotFoundException(String.format("Cannot find target file: %s. Expected a toml file containing modpack config!", packFilePath));
        this.packFile = new Cache<>(() -> new PackFile(packFilePath));
    }

    public Path getRoot() {
        return this.root;
    }

    /* The mandatory file required for the pack to function. */
    public boolean isKeyFile(Path path) {
        return this.packFile.get().getPath().equals(path) || this.packFile.get().getIndexPath().equals(path);
    }

    public FileIgnoreRules getPackwizIgnore() {
        Path packwizIgnoreFile = this.root.resolve(".packwizignore");
        FileIgnoreRules bonusRules;

        if(Files.exists(packwizIgnoreFile)) {
            try {
                bonusRules = new FileIgnoreRules(String.join("\n", Files.readAllLines(packwizIgnoreFile)));
            } catch (IOException e) {
                bonusRules = null;
            }
        } else {
            bonusRules = null;
        }

        return bonusRules == null ? this.defaultFileIgnoreRules : this.defaultFileIgnoreRules.overlay(bonusRules);
    }
}
