package com.rogoshum.magickcore.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.function.Supplier;

public class DeferredRegister<T> {
    private final Registry<T> registry;
    private final String modId;
    private final HashMap<String, T> map = new HashMap<>();

    public static <T> DeferredRegister<T> create(Registry<T> registry, String modId) {
        return new DeferredRegister<>(registry, modId);
    }

    public DeferredRegister(Registry<T> registry, String modId) {
        this.registry = registry;
        this.modId = modId;
    }

    public <I extends T> RegistryObject<I> register(String id, Supplier<I> supplier) {
        I value = supplier.get();
        map.put(id, value);
        return new RegistryObject<>(value);
    }

    public void register() {
        map.forEach((key, value) -> Registry.register(registry, new ResourceLocation(modId, key), value));
    }
}
