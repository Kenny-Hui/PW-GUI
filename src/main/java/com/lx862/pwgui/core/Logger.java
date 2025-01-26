package com.lx862.pwgui.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** The logger used for the program */
public class Logger {
    private final List<Consumer<String>> logListeners;
    public final List<String> lines;

    public Logger() {
        this.lines = new ArrayList<>();
        this.logListeners = new ArrayList<>();
    }

    public void addListener(Consumer<String> logListener) {
        this.logListeners.add(logListener);

        for(String line : lines) {
            logListener.accept(line);
        }
    }

    public void removeListener(Consumer<String> logListener) {
        this.logListeners.remove(logListener);
    }

    public void error(String str) {
        error("[" + Constants.PROGRAM_NAME + "]", str);
    }

    public void error(String prefix, String str) {
        log(prefix + " [ERROR]", str);
    }

    public void exception(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw)); // Only in Java (TM)
        error(sw.toString());
    }

    public void info(String str) {
        info("[" + Constants.PROGRAM_NAME + "]", str);
    }

    public void info(String prefix, String str) {
        log(prefix + " [INFO]", str);
    }

    private void log(String prefix, String str) {
        String finalMessage = prefix + " " + str;
        lines.add(finalMessage);
        System.out.println(finalMessage);
        for(Consumer<String> logListener : logListeners) {
            logListener.accept(finalMessage);
        }
    }
}
