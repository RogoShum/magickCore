package com.rogoshum.magickcore.magick.context.child;

import com.rogoshum.magickcore.lib.LibContext;
import net.minecraft.nbt.CompoundNBT;

public class MultiReleaseContext extends ChildContext{
    public static MultiReleaseContext create() {
        return new MultiReleaseContext();
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
        return LibContext.MULTI_RELEASE;
    }

    @Override
    public String getString(int tab) {
        return "";
    }
}
