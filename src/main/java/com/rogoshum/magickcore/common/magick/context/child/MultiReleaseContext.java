package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.common.lib.LibContext;
import net.minecraft.nbt.CompoundTag;

public class MultiReleaseContext extends ChildContext{
    public static MultiReleaseContext create() {
        return new MultiReleaseContext();
    }

    @Override
    public void serialize(CompoundTag tag) {

    }

    @Override
    public void deserialize(CompoundTag tag) {

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
