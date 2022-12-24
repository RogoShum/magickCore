package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.api.enums.ApplyType;
import net.minecraft.nbt.CompoundNBT;

public abstract class ChildContext {
    public abstract void serialize(CompoundNBT tag);
    public abstract void deserialize(CompoundNBT tag);

    public abstract boolean valid();
    public abstract String getName();
    public abstract String getString(int tab);

    public ApplyType getLinkType() {
        return ApplyType.NONE;
    }
}
