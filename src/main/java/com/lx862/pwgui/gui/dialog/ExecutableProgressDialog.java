package com.lx862.pwgui.gui.dialog;

import com.lx862.pwgui.executable.ProgramExecution;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class ExecutableProgressDialog extends ProgressDialog {
    private final ProgramExecution programExecution;
    private Supplier<Boolean> programErroredSupplier;

    public ExecutableProgressDialog(JFrame frame, String title, String executionReason, ProgramExecution programExecution) {
        super(frame, title);
        this.programExecution = programExecution;

        AtomicReference<String> lastOutput = new AtomicReference<>();
        programExecution.whenStdout((text) -> {
            lastOutput.set(text);
            setStatus(text);
        });
        programExecution.whenExit(exitCode -> {
            dispose();

            if(exitCode != 0 && exitCode != -1) { // -1 reserved for termination exit.
                if(programErroredSupplier != null && !programErroredSupplier.get()) return; // it's not considered an error
                String formattedMessage = String.format("%s exited with code %d:\n%s", programExecution.getProgramDisplayName(), exitCode, lastOutput.get());
                JOptionPane.showMessageDialog(this, formattedMessage, Util.withTitlePrefix(programExecution.getProgramDisplayName()), JOptionPane.ERROR_MESSAGE);
            }
        });
        setStatus("Waiting for " + programExecution.getProgramDisplayName() + "...");
        programExecution.execute(executionReason);
    }

    public void whenProgramErrored(Supplier<Boolean> supplier) {
        this.programErroredSupplier = supplier;
    }

    @Override
    public void dispose() {
        programExecution.stop();
        super.dispose();
    }
}
