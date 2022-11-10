package com.rogoshum.magickcore.common.magick.context.child;

import net.minecraft.nbt.CompoundNBT;

public abstract class ChildContext {
    public abstract void serialize(CompoundNBT tag);
    public abstract void deserialize(CompoundNBT tag);

    public abstract boolean valid();
    public abstract String getName();
    public abstract String getString(int tab);
}
