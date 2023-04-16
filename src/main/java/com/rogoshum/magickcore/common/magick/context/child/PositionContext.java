package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.lib.LibContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public class PositionContext extends ChildContext{
    public static final Type<PositionContext> TYPE = new Type<>(LibContext.POSITION);
    public Vec3 pos = Vec3.ZERO;

    public static PositionContext create(Vec3 pos) {
        PositionContext context = new PositionContext();
        context.pos = pos;
        return context;
    }

    @Override
    public void serialize(CompoundTag tag) {
        NBTTagHelper.putVectorDouble(tag, "pos", pos);
    }

    @Override
    public void deserialize(CompoundTag tag) {
        if(NBTTagHelper.hasVectorDouble(tag, "pos"))
            pos = NBTTagHelper.getVectorFromNBT(tag, "pos");
    }

    @Override
    public boolean valid() {
        return pos != null;
    }

    @Override
    public Type<PositionContext> getType() {
        return TYPE;
    }

    @Override
    public String getString(int tab) {
        return pos.toString();
    }
}
