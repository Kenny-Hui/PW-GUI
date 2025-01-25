package com.lx862.pwgui.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.lx862.pwgui.util.GoUtil;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public static final Path CONFIG_DIR_PATH = GoUtil.userConfigDir().resolve("pwgui");
    public final Map<String, Path> fileChooserLastPath;
    private Path packwizExecutablePath;

    public Config() throws FileNotFoundException {
        this(new Gson().fromJson(new JsonReader(new FileReader(CONFIG_DIR_PATH.resolve("config.json").toFile())), JsonObject.class));
    }

    public Config(JsonObject jsonObject) {
        this.fileChooserLastPath = new HashMap<>();
        if(jsonObject.has("executables")) {
            JsonObject executableObject = jsonObject.getAsJsonObject("executables");
            this.packwizExecutablePath = Paths.get(executableObject.get("packwiz").getAsString());
        }

        if(jsonObject.has("lastPickedFiles")) {
            JsonArray lastPickedFiles = jsonObject.getAsJsonArray("lastPickedFiles");

            for(int i = 0; i < lastPickedFiles.size(); i++) {
                JsonObject entry = lastPickedFiles.get(i).getAsJsonObject();
                String contextName = entry.get("context").getAsString();
                String path = entry.get("path").getAsString();
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

    public void write() throws IOException {
        CONFIG_DIR_PATH.toFile().mkdirs();

        JsonObject jsonObject = new JsonObject();
        JsonObject executableJsonObject = new JsonObject();
        executableJsonObject.addProperty("packwiz", packwizExecutablePath.toString());
        jsonObject.add("executables", executableJsonObject);

        JsonArray lastPickedFilesJsonArray = new JsonArray();
        for(Map.Entry<String, Path> entry : fileChooserLastPath.entrySet()) {
            JsonObject entryJsonObject = new JsonObject();
            entryJsonObject.addProperty("context", entry.getKey());
            entryJsonObject.addProperty("path", entry.getValue().toString());
            lastPickedFilesJsonArray.add(entryJsonObject);
        }
        jsonObject.add("lastPickedFiles", lastPickedFilesJsonArray);

        try(Writer writer = new FileWriter(CONFIG_DIR_PATH.resolve("config.json").toFile())) {
            new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject, writer);
        }
    }
}

