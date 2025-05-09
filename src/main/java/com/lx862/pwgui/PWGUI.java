package com.lx862.pwgui;

import com.lx862.pwgui.core.Config;
import com.lx862.pwgui.core.Logger;
import com.lx862.pwgui.executable.Executables;
import com.lx862.pwgui.gui.frame.EditFrame;
import com.lx862.pwgui.gui.frame.SetupFrame;
import com.lx862.pwgui.gui.frame.WelcomeFrame;
import com.lx862.pwgui.pwcore.Modpack;
import com.lx862.pwgui.util.GUIHelper;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;

public class PWGUI {
    public static final Logger LOGGER = new Logger();
    private static Config config = new Config();

    /**
     * Initialize/re-initialize the program
     * @param commandLine The CommandLine parsed from the CLI. Null if this is a reinitialization process.
     */
    public static void init(CommandLine commandLine) {
        try {
            config.read();
        } catch (Exception e) {
            if(!(e instanceof FileNotFoundException)) { // Missing file is expected on first launch, nothing notable that needs logging
                LOGGER.exception(e);
                LOGGER.error("Failed to read config file!");
            }
        }

        String packFilePath = config.openLastModpackOnLaunch.getValue() ? config.lastModpackPath.getValue() == null ? null : config.lastModpackPath.getValue().toString() : null;
        boolean packwizLocated;

        if(commandLine != null) {
            String packwizPathOverride = commandLine.getOptionValue("pwexec");
            String packFilePathOverride = commandLine.getOptionValue("pack");
            packwizLocated = Executables.packwiz.updateExecutableLocation(packwizPathOverride);
            if(packFilePathOverride != null) packFilePath = packFilePathOverride;
        } else {
            packwizLocated = Executables.packwiz.updateExecutableLocation(null);
        }
        // final boolean gitLocated = git.updateExecutableLocation(null); // We don't have git support yet

        // Get modpack
        Modpack modpack;
        if(packFilePath != null) { // Pack specified via CLI or last opened
            LOGGER.info(String.format("Pack File is specified at: %s", packFilePath));
            File packFile = new File(packFilePath);
            try {
                modpack = new Modpack(packFile.toPath());
            } catch (FileNotFoundException e) {
                LOGGER.info("Specified Pack File does not exist!");
                modpack = null;
            }
        } else {
            modpack = null;
        }

        launchGUI(packwizLocated, modpack);
    }

    private static void launchGUI(boolean packwizLocated, Modpack modpack) {
        Config config = getConfig();
        GUIHelper.setupApplicationTheme(config.applicationTheme.getValue(), config.useWindowDecoration.getValue(), null); // Initialize FlatLaf and it's config

        if(!packwizLocated) { // No packwiz, show setup wizard
            SwingUtilities.invokeLater(() -> {
                SetupFrame setupFrame = new SetupFrame(null);
                setupFrame.setVisible(true);
            });
            return;
        }

        if(modpack != null) { // Pack specified via CLI or last opened
            SwingUtilities.invokeLater(() -> {
                EditFrame editFrame = new EditFrame(null, modpack);
                editFrame.setVisible(true);
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                WelcomeFrame welcomeFrame = new WelcomeFrame(null);
                welcomeFrame.setVisible(true);
            });
        }
    }

    public static Config getConfig() {
        return config;
    }
}