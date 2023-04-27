package com.rogoshum.magickcore.api.registry;

import com.rogoshum.magickcore.api.IRegistry;

import java.util.HashMap;

public class ObjectRegistry<T> implements IRegistry<T> {
    private final HashMap<String, T> registry = new HashMap<>();
    private final String registryType;
    public ObjectRegistry(String registryType) {
        this.registryType = registryType;
        MagickRegistry.register(this.registryType, this);
    }

    public void register(String id, T object) {
        if(!registry.containsKey(id))
            registry.put(id, object);
        else try {
            throw new Exception("Containing same id in the map = [" + id +"]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public T get(String id) {
        if(registry.containsKey(id))
            return registry.get(id);
        return null;
    }

    public HashMap<String, T> registry() {
        return registry;
    }

    public String getRegistryType() {
        return registryType;
    }
}
