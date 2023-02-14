package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.common.lib.LibContext;
import net.minecraft.nbt.CompoundTag;

public class RemoveHurtTimeContext extends ChildContext{
    public static RemoveHurtTimeContext create() {
        return new RemoveHurtTimeContext();
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
        return LibContext.REMOVE_HURT_TIME;
    }

    @Override
    public String getString(int tab) {
        return "";
    }
}
