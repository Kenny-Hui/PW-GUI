package com.lx862.pwgui.gui.components.filter;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class CurseForgeModpackFilter extends FileFilter {
    @Override
    public boolean accept(File file) {
        return file.isDirectory() || file.getName().endsWith(".zip") || file.getName().equals("manifest.json");
    }

    @Override
    public String getDescription() {
        return "CurseForge Modpack (.zip / manifest.json)";
    }
}
