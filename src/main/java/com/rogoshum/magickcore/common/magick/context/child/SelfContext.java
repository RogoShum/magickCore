package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.common.lib.LibContext;
import net.minecraft.nbt.CompoundNBT;

public class SelfContext extends ChildContext{
    public static SelfContext create() {
        return new SelfContext();
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
        return LibContext.SELF;
    }

    @Override
    public String getString(int tab) {
        return "";
    }
}
