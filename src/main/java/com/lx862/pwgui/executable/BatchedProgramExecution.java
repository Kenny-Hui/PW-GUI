package com.lx862.pwgui.executable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/** Executes multiple commands and invokes callback after completion of all commands */
public class BatchedProgramExecution {
    private final List<ProgramExecution> programExecutions;
    private final List<Consumer<Integer>> programExitCallbacks;
    private final List<Consumer<Boolean>> finishCallbacks;

    public BatchedProgramExecution() {
        this.programExecutions = new ArrayList<>();
        this.programExitCallbacks = new ArrayList<>();
        this.finishCallbacks = new ArrayList<>();
    }

    /** Add another program to queued for execution */
    public void add(ProgramExecution exec) {
        programExecutions.add(exec);
    }

    public void onExit(Consumer<Integer> exitConsumer) {
        this.programExitCallbacks.add(exitConsumer);
    }

    public void onFinish(Consumer<Boolean> finishConsumer) {
        this.finishCallbacks.add(finishConsumer);
    }

    public int getTotalCommands() {
        return programExecutions.size();
    }

    /**
     * Terminate all subprocess
     */
    public void terminate() {
        programExecutions.forEach(ProgramExecution::terminate);
    }

    public void execute(String reason) {
        if(programExecutions.isEmpty()) { // Nothing to run
            finishCallbacks.forEach(callback -> callback.accept(true));
        }

        AtomicInteger erroredCommands = new AtomicInteger();
        AtomicInteger executedCommands = new AtomicInteger();
        int totalCommands = programExecutions.size();

        for(ProgramExecution programExecution : programExecutions) {
            programExecution.onExit(exitCode -> {
                programExitCallbacks.forEach(callback -> callback.accept(exitCode));

                if(exitCode > 0) erroredCommands.incrementAndGet();
                executedCommands.incrementAndGet();

                boolean allCommandExecuted = executedCommands.get() == totalCommands;
                if(allCommandExecuted) {
                    finishCallbacks.forEach(callback -> callback.accept(erroredCommands.get() == 0));
                }
            });
            programExecution.execute(reason);
        }
    }
}
