package com.lx862.pwgui.data;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

/* Represents a .gitignore-formatted rules */
public class FileIgnoreRules {
    private final String[] rules;

    public FileIgnoreRules(String content) {
        this(content.split("\n"));
    }

    public FileIgnoreRules(String[] rules) {
        this.rules = rules;
    }

    /* This adds rules on top of our existing rules, essentially overlaying on top */
    public FileIgnoreRules overlay(FileIgnoreRules fileIgnoreRules) {
        int totalSize = rules.length + fileIgnoreRules.rules.length;
        String[] newRules = new String[totalSize];
        for(int i = 0; i < rules.length; i++) {
            newRules[i] = rules[i];
        }
        for(int i = 0; i < fileIgnoreRules.rules.length; i++) {
            newRules[rules.length + i] = fileIgnoreRules.rules[i];
        }
        return new FileIgnoreRules(newRules);
    }

    public boolean shouldIgnore(Path path) {
        for(String line : rules) {
            if(line.trim().isEmpty()) continue;
            if(line.startsWith("#")) continue; // Comment
            String finalLine = line;
            if(finalLine.startsWith("/")) {
                finalLine = finalLine.substring(1);
            } else if(finalLine.startsWith("**/")) {
                finalLine = finalLine.substring(3);
            }

            PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**/" + finalLine);
            if(pathMatcher.matches(path)) return true;
        }
        return false;
    }
}
