package com.rogoshum.magickcore.api.extradata;

import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;

public abstract class EntityExtraData {
    public static final String ENTITY_DATA = "entity_extra_data";
    public abstract boolean isEntitySuitable(Entity entity);

    public abstract void read(CompoundTag nbt);
    public abstract void write(CompoundTag nbt);
}
