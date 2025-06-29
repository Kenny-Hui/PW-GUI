package com.lx862.pwgui.gui.prompt;

import com.lx862.pwgui.executable.Task;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class TaskProgressDialog extends ProgressDialog {
    private final Task task;
    private Supplier<Boolean> taskErroredSupplier;

    public TaskProgressDialog(Window window, String title, String executionReason, Task task) {
        super(window, title);
        this.task = task;

        AtomicReference<String> lastOutput = new AtomicReference<>();
        task.onOutput(stdout -> {
            SwingUtilities.invokeLater(() -> {
                lastOutput.set(stdout.content());
                setStatus(stdout.content());
            });
        });

        task.onExit(exitCode -> {
            SwingUtilities.invokeLater(() -> {
                dispose();

                if(exitCode != 0 && exitCode != -1) { // -1 reserved for termination exit.
                    if(taskErroredSupplier != null && !taskErroredSupplier.get()) return; // it's not considered an error
                    String formattedMessage = String.format("%s exited with code %d:\n%s", task.getTaskName(), exitCode, lastOutput.get());
                    JOptionPane.showMessageDialog(this, formattedMessage, Util.withTitlePrefix(task.getTaskName()), JOptionPane.ERROR_MESSAGE);
                }
            });
        });
        setStatus(String.format("Waiting for %s...", task.getTaskName()));
        task.run(executionReason);
    }

    public void whenProgramErrored(Supplier<Boolean> supplier) {
        this.taskErroredSupplier = supplier;
    }

    @Override
    protected void onCancellation() {
        task.terminate();
    }

    @Override
    public void dispose() {
        task.terminate();
        super.dispose();
    }
}
