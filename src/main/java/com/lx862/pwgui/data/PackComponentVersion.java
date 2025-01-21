package com.lx862.pwgui.data;

public class PackComponentVersion {
    public final PackComponent component;
    public final String version;

    public PackComponentVersion(PackComponent packComponent, String version) {
        this.component = packComponent;
        this.version = version;
    }
}
