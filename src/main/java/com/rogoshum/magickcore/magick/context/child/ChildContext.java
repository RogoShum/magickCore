package com.rogoshum.magickcore.magick.context.child;

import com.rogoshum.magickcore.tool.ToolTipHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.world.World;

public abstract class ChildContext {
    public abstract void serialize(CompoundNBT tag);
    public abstract void deserialize(CompoundNBT tag);

    public abstract boolean valid();
    public abstract String getName();
    public abstract String getString(int tab);
}
