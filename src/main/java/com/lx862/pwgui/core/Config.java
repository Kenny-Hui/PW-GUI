package com.lx862.pwgui.core;

import com.google.gson.*;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.util.GoUtil;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Config for the main program */
public class Config extends WritableFile {
    public static final Path CONFIG_DIR_PATH = GoUtil.userConfigDir().resolve("pwgui");
    private static final Path CONFIG_PATH = CONFIG_DIR_PATH.resolve("config.json");
    public final Map<String, Path> fileChooserLastPath;
    private Path packwizExecutablePath;
    private Path lastModpackPath;
    private boolean openLastModpackOnLaunch;

    public Config() throws FileNotFoundException {
        this(JsonParser.parseReader(new FileReader(CONFIG_PATH.toFile())).getAsJsonObject());
    }

    public Config(JsonObject jsonObject) {
        super(CONFIG_PATH);

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
        if(jsonObject.has("lastModpackPath")) {
            this.lastModpackPath = Paths.get(jsonObject.get("lastModpackPath").getAsString());
        }
        if(jsonObject.has("openLastModpackOnLaunch")) {
            this.openLastModpackOnLaunch = jsonObject.get("openLastModpackOnLaunch").getAsBoolean();
        }
    }

    public void write(String reason) throws IOException {
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
        if(lastModpackPath != null) jsonObject.addProperty("lastModpackPath", lastModpackPath.toString());
        jsonObject.addProperty("openLastModpackOnLaunch", openLastModpackOnLaunch);

        try(Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject, writer);
        }
        super.write(reason);
    }

    public Path getPackwizExecutablePath() {
        return this.packwizExecutablePath;
    }

    public void setPackwizExecutablePath(Path newPath) {
        this.packwizExecutablePath = newPath;
    }

    public Path getLastModpackPath() {
        return this.lastModpackPath;
    }

    public void setLastModpackPath(Path newPath) {
        if(!Objects.equals(newPath, this.lastModpackPath)) {
            this.lastModpackPath = newPath;
            try { // Write if changed
                write("Save last opened modpack path");
            } catch (IOException e) {
                Main.LOGGER.exception(e);
            }
        }
    }

    public boolean openLastModpackOnLaunch() {
        return this.openLastModpackOnLaunch;
    }

    public void setOpenLastModpackOnLaunch(boolean value) {
        this.openLastModpackOnLaunch = value;
    }
}

