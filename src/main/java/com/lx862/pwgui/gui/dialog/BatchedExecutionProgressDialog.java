package com.lx862.pwgui.gui.dialog;

import com.lx862.pwgui.executable.BatchedProgramExecution;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BatchedExecutionProgressDialog extends ProgressDialog {
    private final BatchedProgramExecution batchedProgramExecution;

    public BatchedExecutionProgressDialog(Window window, String title, String executionReason, BatchedProgramExecution batchedProgramExecution) {
        super(window, title);
        this.batchedProgramExecution = batchedProgramExecution;
        int totalCommands = batchedProgramExecution.getTotalCommands();

        AtomicInteger executedCommands = new AtomicInteger();
        batchedProgramExecution.onProgramStart(programExecution -> {
            programExecution.onStdout((stdoutContext -> {
                setStatus(stdoutContext.content());
            }));
        });

        batchedProgramExecution.onProgramExit(exitCode -> {
            setStatus(String.format("Executed %d/%d commands", executedCommands.incrementAndGet(), totalCommands));
            setProgress((double)executedCommands.get() / totalCommands);
        });

        batchedProgramExecution.onExit(success -> {
            SwingUtilities.invokeLater(this::dispose);
        });
        setStatus(String.format("Execute %d commands...", totalCommands));
        batchedProgramExecution.execute(executionReason);
    }

    @Override
    public void dispose() {
        batchedProgramExecution.terminate();
        super.dispose();
    }
}
