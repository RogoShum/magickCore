package com.rogoshum.magickcore.api.magick.context.child;

import com.rogoshum.magickcore.common.lib.LibContext;
import net.minecraft.nbt.CompoundTag;

public class SeparatorContext extends ChildContext{
    public static final Type<SeparatorContext> TYPE = new Type<>(LibContext.SEPARATOR);
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
    public Type<SeparatorContext> getType() {
        return TYPE;
    }

    @Override
    public String getString(int tab) {
        return "";
    }
}
