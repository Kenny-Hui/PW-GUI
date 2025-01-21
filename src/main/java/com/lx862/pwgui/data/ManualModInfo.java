package com.lx862.pwgui.data;

public class ManualModInfo {
    public final String name;
    public final String fileName;
    public final String url;
    private boolean found;

    public ManualModInfo(String name, String fileName, String url) {
        this.name = name;
        this.fileName = fileName;
        this.url = url;
        this.found = false;
    }

    public boolean getFound() {
        return this.found;
    }

    public void setFound(boolean bl) {
        this.found = bl;
    }
}
