package com.lx862.pwgui.executable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/* Executes multiple commands and invokes callback after completion of all commands */
public class BatchedProgramExecution {
    private final List<ProgramExecution> programExecutions;

    public BatchedProgramExecution() {
        this.programExecutions = new ArrayList<>();
    }

    public void add(ProgramExecution exec) {
        this.programExecutions.add(exec);
    }

    public void run(String reason, Consumer<Boolean> callback) {
        if(programExecutions.isEmpty()) callback.accept(true);

        AtomicInteger erroredCommands = new AtomicInteger();
        AtomicInteger executedCommands = new AtomicInteger();
        int totalCommands = programExecutions.size();

        for(ProgramExecution programExecution : programExecutions) {
            programExecution.whenExit(exitCode -> {
                if(exitCode > 0) erroredCommands.incrementAndGet();
                executedCommands.incrementAndGet();

                boolean allCommandExecuted = executedCommands.get() == totalCommands;
                if(allCommandExecuted) {
                    callback.accept(erroredCommands.get() == 0);
                }
            });
            programExecution.execute(reason);
        }
    }
}
