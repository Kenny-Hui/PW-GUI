package com.lx862.pwgui.executable;

import com.lx862.pwgui.Main;

import java.nio.file.Path;

public class PackwizExecutable extends Executable {
    private String packFileLocation = null;

    public PackwizExecutable() {
        super("Packwiz");
        keywords.add("A command line tool for creating Minecraft modpacks");
        keywords.add("Use \"packwiz [command] --help\" for more information about a command.");

        potentialPaths.add("packwiz"); // Added in PATH
        potentialPaths.add("/etc/profiles/per-user/" + System.getProperty("user.name") + "/bin/packwiz"); // NixOS
    }

    @Override
    public ProgramExecution buildCommand(String... str) {
        if(packFileLocation == null) return super.buildCommand(str);

        String[] args = new String[str.length+2];
        for(int i = 0; i < str.length; i++) {
            args[i] = str[i];
        }
        args[args.length-2] = "--pack-file";
        args[args.length-1] = packFileLocation;

        return super.buildCommand(args);
    }

    @Override
    public String probe(String override) {
        Path configuredPackwizExecutablePath = Main.getConfig().getPackwizExecutablePath();
        if(configuredPackwizExecutablePath != null) {
            if(isOurIntendedProgram(configuredPackwizExecutablePath.toString())) {
                Main.LOGGER.info(String.format("%s executable configured at %s", programName, configuredPackwizExecutablePath));
                return configuredPackwizExecutablePath.toString();
            }
        }
        return super.probe(override);
    }

    public void setPackFileLocation(String str) {
        this.packFileLocation = str;
    }
}
