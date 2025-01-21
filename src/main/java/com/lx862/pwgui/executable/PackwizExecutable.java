package com.lx862.pwgui.executable;

import com.lx862.pwgui.util.Util;

public class PackwizExecutable extends Executable {
    private String packFileLocation = null;

    public PackwizExecutable() {
        super("Packwiz");
        keywords.add("A command line tool for creating Minecraft modpacks");
        keywords.add("Use \"packwiz [command] --help\" for more information about a command.");

        potentialPaths.add("packwiz"); // Added in PATH
        potentialPaths.add("/etc/profiles/per-user/" + Util.getSystemUserName() + "/bin/packwiz"); // NixOS
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

    public void setPackFileLocation(String str) {
        this.packFileLocation = str;
    }
}
