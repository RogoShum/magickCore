package com.rogoshum.magickcore.common.registry;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RegistryObject<T>{
    private final T value;

    public RegistryObject(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public boolean isPresent() {
        return value != null;
    }

    public T orElse(@Nullable T other) {
        return value == null ? other : value;
    }
}
