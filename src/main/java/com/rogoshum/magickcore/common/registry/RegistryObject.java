package com.rogoshum.magickcore.common.registry;

public class RegistryObject<T> {
    private final T value;

    public RegistryObject(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
