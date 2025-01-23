package com.lx862.pwgui.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/* The main logger used for this program */
public class Logger {
    private Consumer<String> logListener;
    public final List<String> lines;

    public Logger() {
        this.lines = new ArrayList<>();
    }

    public void listen(Consumer<String> logListener) {
        this.logListener = logListener;

        for(String line : lines) {
            logListener.accept(line);
        }
    }

    public void error(String str) {
        error("[" + Constants.PROGRAM_NAME + "]", str);
    }

    public void error(String prefix, String str) {
        log(prefix + " [ERROR]", str);
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
        if(this.logListener != null) this.logListener.accept(finalMessage);
    }
}
