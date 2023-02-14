package com.rogoshum.magickcore.api.event;

import com.rogoshum.magickcore.common.extradata.EntityExtraData;
import com.rogoshum.magickcore.common.extradata.ItemExtraData;
import net.minecraftforge.eventbus.api.GenericEvent;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.function.Function;

public abstract class ExtraDataEvent<T, D> extends GenericEvent<T> {
    private final HashMap<String, D> dataMap;

    protected ExtraDataEvent(HashMap<String, D> dataMap) {
        this.dataMap = dataMap;
    }

    public void add(String id, D extraData) {
        if(!dataMap.containsKey(id))
            dataMap.put(id, extraData);
    }

    public static class Entity extends ExtraDataEvent<Entity, Callable<EntityExtraData>> {
        public Entity(HashMap<String, Callable<EntityExtraData>> dataMap) {
            super(dataMap);
        }
    }

    public static class ItemStack extends ExtraDataEvent<String, Function<net.minecraft.world.item.ItemStack, ItemExtraData>> {
        public ItemStack(HashMap<String, Function<net.minecraft.world.item.ItemStack, ItemExtraData>> dataMap) {
            super(dataMap);
        }
    }
}
