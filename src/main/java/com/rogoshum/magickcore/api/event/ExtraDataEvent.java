package com.rogoshum.magickcore.api.event;

import com.rogoshum.magickcore.common.extradata.EntityExtraData;
import com.rogoshum.magickcore.common.extradata.ItemExtraData;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.function.Function;

public abstract class ExtraDataEvent<D> extends Event {
    private final HashMap<String, D> dataMap;

    protected ExtraDataEvent(HashMap<String, D> dataMap) {
        this.dataMap = dataMap;
    }

    public void add(String id, D extraData) {
        if(!dataMap.containsKey(id))
            dataMap.put(id, extraData);
    }

    public static class Entity extends ExtraDataEvent<Callable<EntityExtraData>> {
        public Entity(HashMap<String, Callable<EntityExtraData>> dataMap) {
            super(dataMap);
        }
    }

    public static class ItemStack extends ExtraDataEvent<Function<net.minecraft.world.item.ItemStack, ItemExtraData>> {
        public ItemStack(HashMap<String, Function<net.minecraft.world.item.ItemStack, ItemExtraData>> dataMap) {
            super(dataMap);
        }
    }
}
