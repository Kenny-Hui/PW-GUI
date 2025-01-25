package com.lx862.pwgui.core;

import com.lx862.pwgui.util.GoUtil;
import com.moandjiezana.toml.Toml;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config extends TomlFile {
    public static final Path CONFIG_DIR_PATH = GoUtil.userConfigDir().resolve("pwgui");
    public final Map<String, Path> fileChooserLastPath;
    private Path packwizExecutablePath;

    public Config() {
        this(new Toml().read(CONFIG_DIR_PATH.resolve("config.toml").toFile()));
    }

    public Config(Toml toml) {
        super(CONFIG_DIR_PATH.resolve("config.toml"), toml);
        this.fileChooserLastPath = new HashMap<>();
        this.packwizExecutablePath = toml.contains("executables.packwiz") ? Paths.get(toml.getString("executables.packwiz")) : null;
        if(toml.contains("last-picked-files")) {
            List<Object> lastPickedFiles = toml.getList("last-picked-files");
            for(Object o : lastPickedFiles) {
                if(!(o instanceof List)) continue;
                List<String> pair = ((List<String>)o);
                String contextName = pair.get(0);
                String path = pair.get(1);
                fileChooserLastPath.put(contextName, Paths.get(path));
            }
        }
    }

    public Path getPackwizExecutablePath() {
        return this.packwizExecutablePath;
    }

    public void setPackwizExecutablePath(Path newPath) {
        this.packwizExecutablePath = newPath;
    }

    @Override
    public void write() throws IOException {
        CONFIG_DIR_PATH.toFile().mkdirs();
        Map<String, Object> map = toml.toMap();
        Map<String, Object> executableMap = (Map<String, Object>) map.getOrDefault("executables", new HashMap<>());
        executableMap.put("packwiz", packwizExecutablePath.toString());

        map.put("executables", executableMap);

        List<List<String>> lastPickedFilesList = new ArrayList<>();
        for(Map.Entry<String, Path> entry : fileChooserLastPath.entrySet()) {
            List<String> entryToml = new ArrayList<>();
            entryToml.add(entry.getKey());
            entryToml.add(entry.getValue().toString());
            lastPickedFilesList.add(entryToml);
        }

        map.put("last-picked-files", lastPickedFilesList);
        writeToFilesystem(map);
    }
}

