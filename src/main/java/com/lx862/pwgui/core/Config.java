package com.lx862.pwgui.core;

import com.lx862.pwgui.util.GoUtil;
import com.moandjiezana.toml.Toml;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Config extends TomlFile {
    public static final Path CONFIG_DIR_PATH = GoUtil.userConfigDir().resolve("pwgui");
    public Path packwizExecutablePath;

    public Config() {
        this(new Toml().read(CONFIG_DIR_PATH.resolve("config.toml").toFile()));
    }

    public Config(Toml toml) {
        super(CONFIG_DIR_PATH.resolve("config.toml"), toml);
        packwizExecutablePath = toml.contains("executables.packwiz") ? Paths.get(toml.getString("executables.packwiz")) : null;
    }

    @Override
    public void write() throws IOException {
        CONFIG_DIR_PATH.toFile().mkdirs();
        Map<String, Object> map = toml.toMap();
        Map<String, Object> executableMap = (Map<String, Object>) map.getOrDefault("executables", new HashMap<>());
        executableMap.put("packwiz", packwizExecutablePath.toString());
        map.put("executables", executableMap);
        writeToFilesystem(map);
    }
}
