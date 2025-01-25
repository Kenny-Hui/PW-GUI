package com.lx862.pwgui.executable;

import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.Main;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class ProgramExecution {
    private final ProcessBuilder processBuilder;
    private final List<Consumer<String>> stdoutListeners;
    private final List<Consumer<Integer>> exitListeners;
    private final String programDisplayName;
    private final ExecutorService defaultExecutor;
    private Process process;

    public ProgramExecution(String programDisplayName, ProcessBuilder processBuilder, ExecutorService defaultExecutor) {
        this.programDisplayName = programDisplayName;
        this.processBuilder = processBuilder;
        this.defaultExecutor = defaultExecutor;
        this.stdoutListeners = new ArrayList<>();
        this.exitListeners = new ArrayList<>();

        whenStdout((line) -> { // Display log when we got a new line
            Main.LOGGER.info("[" + programDisplayName + "]", line);
        });
    }

    public ProgramExecution whenStdout(Consumer<String> consumer) {
        this.stdoutListeners.add(consumer);
        return this;
    }

    public ProgramExecution whenExit(Consumer<Integer> consumer) {
        this.exitListeners.add(consumer);
        return this;
    }

    public void execute(String reason) {
        execute(reason, this.defaultExecutor);
    }

    public void execute(String reason, ExecutorService executor) {
        Main.LOGGER.info(String.format("Running command \"%s\" due to \"%s\"", String.join(" ", processBuilder.command()), reason));

        executor.submit(() -> {
            try {
                this.process = this.processBuilder.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    StringBuilder sb = new StringBuilder();
                    int c;
                    while ((c = reader.read()) != -1) {
                        if(c == '\n' || c == '\r') { // Newline character
                            String line = sb.toString();
                            callStdoutListeners(line);
                            sb = new StringBuilder(); // Clear current line
                        } else {
                            sb.append((char)c);
                            String line = sb.toString();
                            if(line.endsWith("[Y/n]: ")) { // Inline prompt
                                callStdoutListeners(line);
                            }
                        }
                    }
                }

                this.process.waitFor();
                int exitValue = this.process.exitValue();
                callExitListeners(exitValue);
            } catch (IOException e) {
                e.printStackTrace();
                callStdoutListeners(Util.withBracketPrefix(String.format("Failed to execute %s:\n%s", programDisplayName, e.getMessage())));
                callExitListeners(-2);
            } catch (InterruptedException ignored) {
            }
        });
    }

    public String getProgramDisplayName() {
        return programDisplayName;
    }

    public void enterInput(String input) {
        if(this.process != null) {
            PrintWriter pw = new PrintWriter(process.getOutputStream());
            pw.write(input + "\n");
            pw.flush();
            Main.LOGGER.info(String.format("Input %s to %s", input, programDisplayName));
        }
    }

    public void stop() {
        if(this.process != null && this.process.isAlive()) this.process.destroy();
    }

    private void callStdoutListeners(String str) {
        for(Consumer<String> outputListener : stdoutListeners) {
            SwingUtilities.invokeLater(() -> {
                outputListener.accept(str);
            });
        }
    }

    private void callExitListeners(int exitCode) {
        for(Consumer<Integer> listener : exitListeners) {
            SwingUtilities.invokeLater(() -> listener.accept(exitCode));
        }
    }
}
