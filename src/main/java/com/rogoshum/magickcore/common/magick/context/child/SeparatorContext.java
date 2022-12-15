package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.common.lib.LibContext;
import net.minecraft.nbt.CompoundNBT;

public class SeparatorContext extends ChildContext{
    public static SeparatorContext create() {
        return new SeparatorContext();
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
        return LibContext.SEPARATOR;
    }

    @Override
    public String getString(int tab) {
        return "";
    }
}