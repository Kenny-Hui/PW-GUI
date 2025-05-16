package com.lx862.pwgui.executable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/** Executes multiple commands and invokes callback after completion of all commands */
public class BatchedProgramExecution {
    private final List<ProgramExecution> programExecutions;
    private final List<Consumer<ProgramExecution>> programStartCallbacks;
    private final List<Consumer<Integer>> programExitCallbacks;
    private final List<Consumer<Boolean>> finishCallbacks;
    private boolean startedExecution = false;
    private ExecutorService activeExecutor = null;

    public BatchedProgramExecution() {
        this.programExecutions = new ArrayList<>();
        this.programStartCallbacks = new ArrayList<>();
        this.programExitCallbacks = new ArrayList<>();
        this.finishCallbacks = new ArrayList<>();
    }

    /** Add another program to queued for execution */
    public void add(ProgramExecution exec) {
        if(startedExecution) throw new IllegalStateException("No more program execution should be added after batched execution is started!");
        programExecutions.add(exec);
    }

    public void onProgramStart(Consumer<ProgramExecution> onStartConsumer) {
        this.programStartCallbacks.add(onStartConsumer);
    }

    public void onProgramExit(Consumer<Integer> onExitConsumer) {
        this.programExitCallbacks.add(onExitConsumer);
    }

    /**
     * Add a callback to be invoked when all enqueued program have finished execution
     */
    public void onExit(Consumer<Boolean> finishConsumer) {
        this.finishCallbacks.add(finishConsumer);
    }

    public int getTotalCommands() {
        return programExecutions.size();
    }

    /**
     * Terminate process and shutdown executors
     */
    public void terminate() {
        if(activeExecutor != null) activeExecutor.shutdownNow();
        programExecutions.forEach(ProgramExecution::terminate);
    }

    public void execute(String reason) {
        startedExecution = true;
        if(programExecutions.isEmpty()) { // Nothing to run
            invokeCallback(finishCallbacks, true);
            return;
        }

        activeExecutor = Executors.newSingleThreadExecutor();

        AtomicInteger erroredCommands = new AtomicInteger();
        AtomicInteger executedCommands = new AtomicInteger();
        int totalCommands = programExecutions.size();

        for(ProgramExecution programExecution : programExecutions) {
            programExecution.onExit(exitCode -> {
                invokeCallback(programExitCallbacks, exitCode);

                if(exitCode > 0) erroredCommands.incrementAndGet();
                executedCommands.incrementAndGet();

                boolean allCommandExecuted = executedCommands.get() == totalCommands;
                if(allCommandExecuted) {
                    invokeCallback(finishCallbacks, erroredCommands.get() == 0);
                    activeExecutor.shutdown();
                }
            });

            invokeCallback(programStartCallbacks, programExecution);
            programExecution.execute(reason, activeExecutor);
        }
    }

    private <T> void invokeCallback(List<Consumer<T>> callbacks, T value) {
        for(Consumer<T> callback : callbacks) {
            callback.accept(value);
        }
    }
}
