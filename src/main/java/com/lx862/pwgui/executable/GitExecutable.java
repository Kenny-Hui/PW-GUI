package com.lx862.pwgui.executable;

public class GitExecutable extends Executable {

    public GitExecutable() {
        super("Git");
        keywords.add("These are common Git commands used in various situations:");
        keywords.add("See 'git help git' for an overview of the system.");

        potentialPaths.add("git"); // Added in PATH
        potentialPaths.add("C:/Program Files (x86)/Git/bin/git.exe");
        potentialPaths.add("C:/Program Files (x86)/Git/libexec/git-core/git.exe");
    }
}
