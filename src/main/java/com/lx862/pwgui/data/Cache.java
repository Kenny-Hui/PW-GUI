package com.lx862.pwgui.data;

import java.util.function.Supplier;

/** Represents a cached object. The supplier is executed when the content need to be obtained the first time. After that it's stored to memory for quicker retrieval */
public class Cache<T> {
    private final Supplier<T> supplier;
    private T value;

    public Cache(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if(value == null) {
            value = supplier.get();
        }
        return value;
    }

    /**
     * Resets the cache. Supplier will be run again next time get() is called.
     * */
    public void clearCache() {
        value = null;
    }

    @Override
    public String toString() {
        return String.format("Cache (%s)", value);
    }
}
