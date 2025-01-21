package com.lx862.pwgui.data;

/* This class represents version metadata, such as Minecraft version, Fabric version, Modrinth mod version, etc.*/
public class VersionMetadata {
    private final String minecraftVersion;
    private final String versionName;
    private final State state;

    public VersionMetadata(String minecraftVersion, String versionName, State state) {
        this.minecraftVersion = minecraftVersion;
        this.versionName = versionName;
        this.state = state;
    }

    public String getVersionName() {
        return versionName;
    }

    /* Returns either the Minecraft version this version is designed for, or null if the version is agnostic to MC versions. */
    public String getMinecraftVersion() {
        return minecraftVersion;
    }

    public State getState() {
        return state;
    }

    public enum State {
        RELEASE,
        BETA,
        ALPHA
    }

    @Override
    public String toString() {
        return versionName;
    }
}
