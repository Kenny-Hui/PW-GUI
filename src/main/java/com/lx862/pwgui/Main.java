package com.lx862.pwgui;

import com.google.gson.JsonObject;
import com.lx862.pwgui.core.Config;
import com.lx862.pwgui.core.Modpack;
import com.lx862.pwgui.executable.Executables;
import com.lx862.pwgui.gui.frame.EditFrame;
import com.lx862.pwgui.gui.frame.SetupFrame;
import com.lx862.pwgui.gui.frame.WelcomeFrame;
import com.lx862.pwgui.core.Logger;
import com.lx862.pwgui.util.GUIHelper;
import org.apache.commons.cli.*;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static final Logger LOGGER = new Logger();
    private static Config config;

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("e", "pwexec", true, "Specify the path to packwiz executable");
        options.addOption("p", "pack", true, "Specify the location of pack.toml, automatically opening the Modpack.");

        CommandLineParser cliParser = new DefaultParser();

        try {
            CommandLine cmd = cliParser.parse(options, args);
            initializeProgram(cmd);
        } catch (Exception e) {
            Main.LOGGER.exception(e);
            System.exit(1);
        }
    }

    /**
     * Initialize/re-initialize the program
     * @param commandLine The CommandLine parsed from the CLI. Null if this is a reinitialization process.
     */
    public static void initializeProgram(CommandLine commandLine) {
        try {
            config = new Config();
        } catch (Exception e) {
            if(!(e instanceof FileNotFoundException)) { // Missing file is expected on first launch, nothing notable that needs logging
                Main.LOGGER.exception(e);
                Main.LOGGER.error("Failed to read config file!");
            }
            config = new Config(new JsonObject());
        }

        String packFilePath = config.openLastModpackOnLaunch() ? config.getLastModpackPath() == null ? null : config.getLastModpackPath().toString() : null;
        boolean packwizLocated;

        if(commandLine != null) {
            String packwizPathOverride = commandLine.getOptionValue("pwexec");
            packFilePath = commandLine.getOptionValue("pack");
            packwizLocated = Executables.packwiz.updateExecutableLocation(packwizPathOverride);
        } else {
            packwizLocated = Executables.packwiz.updateExecutableLocation(null);
        }
        // final boolean gitLocated = git.updateExecutableLocation(null); // We don't have git support yet

        launchGUI(packwizLocated, packFilePath);
    }

    private static void launchGUI(boolean packwizLocated, String packFilePath) {
        GUIHelper.setupApplicationTheme(Main.getConfig().getApplicationTheme(), null); // Initialize FlatLaf and it's config

        if(!packwizLocated) { // No packwiz, show setup wizard
            SwingUtilities.invokeLater(() -> {
                SetupFrame setupFrame = new SetupFrame(null);
                setupFrame.setVisible(true);
            });
            return;
        }

        if(packFilePath != null) { // Pack specified via CLI or last opened
            LOGGER.info(String.format("Pack File is specified at: %s", packFilePath));
            File packFile = new File(packFilePath);
            try {
                Modpack modpack = new Modpack(packFile.toPath());
                SwingUtilities.invokeLater(() -> {
                    EditFrame editFrame = new EditFrame(null, modpack);
                    editFrame.setVisible(true);
                });
                return;
            } catch (FileNotFoundException e) {
                LOGGER.info("Specified Pack File does not exist!");
            }
        }

        SwingUtilities.invokeLater(() -> {
            WelcomeFrame welcomeFrame = new WelcomeFrame(null);
            welcomeFrame.setVisible(true);
        });
    }

    public static Config getConfig() {
        return config;
    }
}