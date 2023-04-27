package com.rogoshum.magickcore.api.magick.context.child;

import com.rogoshum.magickcore.common.lib.LibContext;
import net.minecraft.nbt.CompoundTag;

public class RemoveHurtTimeContext extends ChildContext{
    public static final Type<RemoveHurtTimeContext> TYPE = new Type<>(LibContext.REMOVE_HURT_TIME);
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
    public Type<RemoveHurtTimeContext> getType() {
        return TYPE;
    }

    @Override
    public String getString(int tab) {
        return "";
    }
}
