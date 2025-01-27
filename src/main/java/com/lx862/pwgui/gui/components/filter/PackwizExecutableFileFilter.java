package com.lx862.pwgui.gui.components.filter;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class PackwizExecutableFileFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().equals("packwiz") || f.getName().equals("packwiz.exe");
    }

    @Override
    public String getDescription() {
        return "Packwiz Executable (packwiz/packwiz.exe)";
    }
}
