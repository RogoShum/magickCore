package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.common.lib.LibContext;
import net.minecraft.nbt.CompoundTag;

public class SeparatorContext extends ChildContext{
    public static SeparatorContext create() {
        return new SeparatorContext();
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
        return LibContext.SEPARATOR;
    }

    @Override
    public String getString(int tab) {
        return "";
    }
}
