package com.lx862.pwgui.data;

import java.util.function.Consumer;

/* A wrapper against a variable, where a callback will be executed when setting the variable */
public class CallbackWrapper<T> {
    private T data;
    private final Consumer<T> onSetCallback;

    public CallbackWrapper(T initialData, Consumer<T> onSet) {
        this.data = initialData;
        this.onSetCallback = onSet;
    }

    public CallbackWrapper(Consumer<T> onSet) {
        this(null, onSet);
    }

    public T get() {
        return data;
    }

    public void set(T newData) {
        this.data = newData;
        onSetCallback.accept(newData);
    }
}
