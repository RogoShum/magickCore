package com.rogoshum.magickcore.common.extradata;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;

public abstract class EntityExtraData {
    public static final String ENTITY_DATA = "entity_extra_data";
    public abstract boolean isEntitySuitable(Entity entity);

    public abstract void read(CompoundNBT nbt);
    public abstract void write(CompoundNBT nbt);
}
