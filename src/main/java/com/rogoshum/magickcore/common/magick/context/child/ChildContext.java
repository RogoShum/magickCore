package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.api.enums.ApplyType;
import net.minecraft.nbt.CompoundTag;

public abstract class ChildContext {
    public abstract void serialize(CompoundTag tag);
    public abstract void deserialize(CompoundTag tag);

    public abstract boolean valid();
    public abstract String getName();
    public abstract String getString(int tab);

    public ApplyType getLinkType() {
        return ApplyType.NONE;
    }
}
