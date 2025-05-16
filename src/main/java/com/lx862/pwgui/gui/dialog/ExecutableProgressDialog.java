package com.lx862.pwgui.gui.dialog;

import com.lx862.pwgui.executable.ProgramExecution;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class ExecutableProgressDialog extends ProgressDialog {
    private final ProgramExecution programExecution;
    private Supplier<Boolean> programErroredSupplier;

    public ExecutableProgressDialog(Window window, String title, String executionReason, ProgramExecution programExecution) {
        super(window, title);
        this.programExecution = programExecution;

        AtomicReference<String> lastOutput = new AtomicReference<>();
        programExecution.onStdout(stdout -> {
            SwingUtilities.invokeLater(() -> {
                lastOutput.set(stdout.content());
                setStatus(stdout.content());
            });
        });
        programExecution.onExit(exitCode -> {
            SwingUtilities.invokeLater(() -> {
                dispose();

                if(exitCode != 0 && exitCode != -1) { // -1 reserved for termination exit.
                    if(programErroredSupplier != null && !programErroredSupplier.get()) return; // it's not considered an error
                    String formattedMessage = String.format("%s exited with code %d:\n%s", programExecution.getProgramDisplayName(), exitCode, lastOutput.get());
                    JOptionPane.showMessageDialog(this, formattedMessage, Util.withTitlePrefix(programExecution.getProgramDisplayName()), JOptionPane.ERROR_MESSAGE);
                }
            });
        });
        setStatus(String.format("Waiting for %s...", programExecution.getProgramDisplayName()));
        programExecution.execute(executionReason);
    }

    public void whenProgramErrored(Supplier<Boolean> supplier) {
        this.programErroredSupplier = supplier;
    }

    @Override
    protected void onCancellation() {
        programExecution.terminate();
    }

    @Override
    public void dispose() {
        programExecution.terminate();
        super.dispose();
    }
}
