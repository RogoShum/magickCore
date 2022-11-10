package com.rogoshum.magickcore.common.api;

import java.util.HashMap;

public interface IRegistry<T> {
    T get(String id);

    public void register(String id, T object);

    HashMap<String, T> registry();

    String getRegistryType();
}
