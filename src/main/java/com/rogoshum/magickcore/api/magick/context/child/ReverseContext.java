package com.rogoshum.magickcore.api.magick.context.child;

import com.rogoshum.magickcore.common.lib.LibContext;
import net.minecraft.nbt.CompoundTag;

public class ReverseContext extends ChildContext{
    public static final Type<ReverseContext> TYPE = new Type<>(LibContext.REVERSE);
    public static ReverseContext create() {
        return new ReverseContext();
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
    public Type<ReverseContext> getType() {
        return TYPE;
    }

    @Override
    public String getString(int tab) {
        return "";
    }
}
