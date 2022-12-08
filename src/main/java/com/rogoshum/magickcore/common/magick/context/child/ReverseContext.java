package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.common.lib.LibContext;
import net.minecraft.nbt.CompoundNBT;

public class ReverseContext extends ChildContext{
    public static ReverseContext create() {
        return new ReverseContext();
    }

    @Override
    public void serialize(CompoundNBT tag) {

    }

    @Override
    public void deserialize(CompoundNBT tag) {

    }

    @Override
    public boolean valid() {
        return true;
    }

    @Override
    public String getName() {
        return LibContext.REVERSE;
    }

    @Override
    public String getString(int tab) {
        return "";
    }
}
