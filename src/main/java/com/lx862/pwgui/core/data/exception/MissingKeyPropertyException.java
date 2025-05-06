package com.lx862.pwgui.core.data.exception;

public class MissingKeyPropertyException extends RuntimeException {
    public MissingKeyPropertyException(String filename, String property) {
        super(String.format("Property '%s' is required in file %s", property, filename));
    }
}
