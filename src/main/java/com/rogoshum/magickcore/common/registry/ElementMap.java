package com.rogoshum.magickcore.common.registry;

import java.util.HashMap;

public class ElementMap<K, V>{
    protected final HashMap<K, V> elementMap = new HashMap<>();

    public void add(K key, V value) {
        elementMap.put(key, value);
    }
}
