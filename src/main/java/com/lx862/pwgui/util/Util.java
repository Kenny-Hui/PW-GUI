package com.lx862.pwgui.util;

import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.Main;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class Util {
    public static String withTitlePrefix(String str) {
        return String.format("%s - %s", Constants.PROGRAM_NAME, str);
    }

    public static String withBracketPrefix(String str) {
        return String.format("[%s] %s", Constants.PROGRAM_NAME, str);
    }

    public static InputStream getAssets(String path) {
        return Main.class.getResourceAsStream(path);
    }

    public static void tryOpenFile(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, String.format("Failed to open file \"%s\":\n%s", file.getName(), e.getMessage()), withTitlePrefix("Open External Application"), JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void tryEditFile(String uri) {
        try {
            Desktop.getDesktop().browse(new URI(uri));
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, String.format("Failed to open link \"%s\":\n%s", uri, e.getMessage()), withTitlePrefix("Open External Link"), JOptionPane.ERROR_MESSAGE);
        }
    }

    public static String getSystemUserName() {
        return System.getProperty("user.name");
    }
}
