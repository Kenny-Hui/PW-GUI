package com.lx862.pwgui.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Reimplementation of Go's function in Java
 */
public class GoUtil {
    // https://github.com/golang/go/blob/40b3c0e58a0ae8dec4684a009bf3806769e0fc41/src/os/file.go#L474-L485
    /** A reimplementation of Go's UserCacheDir, which is used by packwiz to determine the cache location */
    public static Path getUserCacheDir() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if(osName.contains("windows")) {
            return Paths.get(System.getenv("LocalAppData"));
        } else if(osName.contains("mac")) {
            return Paths.get(System.getenv("HOME") + "/Library/Caches");
        } else {
            String cacheDir = System.getenv("XDG_CACHE_HOME");
            if(cacheDir != null) {
                return Paths.get(cacheDir);
            } else {
                String home = System.getenv("HOME");
                return Paths.get(home).resolve(".cache");
            }
        }
    }

    /** A reimplementation of Go's UserConfigDir, which is used by packwiz to determine the config location */
    public static Path getUserConfigDir() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if(osName.contains("windows")) {
            return Paths.get(System.getenv("AppData"));
        } else if(osName.contains("mac")) {
            return Paths.get(System.getenv("HOME") + "/Library/Application Support");
        } else {
            String dir = System.getenv("XDG_CONFIG_HOME");
            if(dir != null) {
                return Paths.get(dir);
            } else {
                String home = System.getenv("HOME");
                return Paths.get(home).resolve(".config");
            }
        }
    }
}
