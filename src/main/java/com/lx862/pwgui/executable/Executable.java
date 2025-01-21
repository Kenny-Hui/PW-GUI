package com.lx862.pwgui.executable;

import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.Main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Executable {
    private ExecutorService executor;
    private Path workingDirectory;
    protected final List<String> keywords;
    protected final List<String> potentialPaths;
    protected final String programName;
    protected String executableLocation = null;

    public Executable(String programName) {
        this.workingDirectory = Paths.get(System.getProperty("user.dir"));
        this.executor = Executors.newSingleThreadExecutor();
        this.keywords = new ArrayList<>();
        this.potentialPaths = new ArrayList<>();
        this.programName = programName;
    }

    public boolean locate(String executableOverride) {
        if(executableOverride != null) {
            if(isOurIntendedProgram(executableOverride)) {
                Main.LOGGER.info(String.format("%s executable is specified at %s", programName, executableOverride));
                executableLocation = executableOverride;
                return true;
            } else {
                Main.LOGGER.info(String.format("%s executable specified at %s is not valid!", programName, executableOverride));
            }
        }

        if(executableLocation == null) {
            Main.LOGGER.info(String.format("Probing for %s executable...", programName));
            for(String potentialPath : potentialPaths) {
                if(isOurIntendedProgram(potentialPath)) {
                    Main.LOGGER.info(String.format("Found %s executable at %s", programName, potentialPath));
                    executableLocation = potentialPath;
                    return true;
                }
            }
        }

        Main.LOGGER.info(Util.withBracketPrefix(String.format("Cannot probe %s executable!", programName)));
        return false;
    }

    public boolean usable() {
        return executableLocation != null;
    }

    protected boolean isOurIntendedProgram(String location) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(location);
            Process process = processBuilder.start();

            int expectedKeywordsHit = keywords.size();
            int actualKeywordsHit = 0;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if(keywords.contains(line)) actualKeywordsHit++;
                }
            }

            return expectedKeywordsHit == actualKeywordsHit;
        } catch (Exception e) {
            return false;
        }
    }

    public void changeWorkingDirectory(Path newPath) {
        Main.LOGGER.info(String.format("Working directory for %s changed to %s", programName, newPath.toString()));
        this.workingDirectory = newPath;
    }

    public ProgramExecution buildCommand(String... str) {
        String[] commandWithExecutable = new String[str.length + 1];
        // Insert our executable at the front
        commandWithExecutable[0] = executableLocation;
        for(int i = 0; i < str.length; i++) {
            commandWithExecutable[i+1] = str[i];
        }
        ProcessBuilder processBuilder = new ProcessBuilder(commandWithExecutable);
        processBuilder.directory(this.workingDirectory.toFile());

        if(executor == null || executor.isShutdown()) this.executor = Executors.newSingleThreadExecutor();

        return new ProgramExecution(programName, processBuilder, executor);
    }

    public String getProgramName() {
        return programName;
    }

    public void dispose() {
        executor.shutdownNow();
    }
}
