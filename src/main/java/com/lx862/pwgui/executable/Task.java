package com.lx862.pwgui.executable;

import com.lx862.pwgui.PWGUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public abstract class Task {
    private final List<Consumer<OutputMessage>> outputListeners;
    private final List<Consumer<Integer>> exitListeners;
    private final String taskName;
    private final ExecutorService defaultExecutor;

    public Task(String taskName, ExecutorService defaultExecutor) {
        this.taskName = taskName;
        this.defaultExecutor = defaultExecutor;
        this.outputListeners = new ArrayList<>();
        this.exitListeners = new ArrayList<>();

        onOutput((output) -> { // Display log when we got a new line
            PWGUI.LOGGER.info("[" + taskName + "]", output.content());
        });
    }

    public Task onOutput(Consumer<OutputMessage> consumer) {
        this.outputListeners.add(consumer);
        return this;
    }

    public Task onExit(Consumer<Integer> consumer) {
        this.exitListeners.add(consumer);
        return this;
    }

    public void run(String reason) {
        run(reason, this.defaultExecutor);
    }

    public abstract void run(String reason, ExecutorService executor);

    public abstract void terminate();

    protected void callOutputListeners(OutputMessage outputMessage) {
        for(Consumer<OutputMessage> outputListener : outputListeners) {
            SwingUtilities.invokeLater(() -> {
                outputListener.accept(outputMessage);
            });
        }
    }

    protected void callExitListeners(int exitCode) {
        for(Consumer<Integer> listener : exitListeners) {
            SwingUtilities.invokeLater(() -> listener.accept(exitCode));
        }
    }

    public String getTaskName() {
        return this.taskName;
    }

    public record OutputMessage(String content, boolean isPrompt) {
    }
}
