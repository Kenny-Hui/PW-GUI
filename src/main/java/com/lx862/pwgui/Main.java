package com.lx862.pwgui;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.lx862.pwgui.core.Modpack;
import com.lx862.pwgui.executable.GitExecutable;
import com.lx862.pwgui.executable.PackwizExecutable;
import com.lx862.pwgui.gui.EditFrame;
import com.lx862.pwgui.gui.WelcomeFrame;
import com.lx862.pwgui.core.Logger;
import org.apache.commons.cli.*;

import javax.swing.*;
import java.io.File;

public class Main {
    public static final Logger LOGGER = new Logger();
    public static final PackwizExecutable packwiz = new PackwizExecutable();
    public static final GitExecutable git = new GitExecutable();

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("e", "pwexec", true, "Specify the path to packwiz executable");
        options.addOption("p", "pack", true, "Specify the location of pack.toml, automatically opening the Modpack.");

        CommandLineParser cliParser = new DefaultParser();

        try {
            CommandLine cmd = cliParser.parse(options, args);

            final String execPath = cmd.getOptionValue("pwexec");
            final String modpackPath = cmd.getOptionValue("pack");

            final boolean packwizLocated = packwiz.locate(execPath);
            final boolean gitLocated = git.locate(null);

            FlatLaf.setup(new FlatIntelliJLaf());
            //FlatLaf.setup(new FlatDarculaLaf()); // TODO: Theme switching

            UIManager.put("Component.focusWidth", 1);
            UIManager.put("ScrollBar.showButtons", true);
            UIManager.put("ScrollBar.width", 14);
            UIManager.put("TabbedPane.showTabSeparators", true);
            UIManager.put("Button.arc", 9);
            UIManager.put("Component.hideMnemonics", false );

            if(!packwizLocated) {
                // TODO: Replace with a download packwiz dialog
                throw new IllegalStateException("Cannot find Packwiz executable.");
            } else {
                if(modpackPath != null) {
                    LOGGER.info(String.format("Pack File is specified: %s", modpackPath));

                    Modpack modpack = new Modpack(new File(modpackPath).toPath());

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
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}