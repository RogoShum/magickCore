package com.rogoshum.magickcore.api.magick.context.child;

import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.lib.LibContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public class DirectionContext extends ChildContext{
    public static final Type<DirectionContext> TYPE = new Type<>(LibContext.DIRECTION);
    public Vec3 direction = Vec3.ZERO;

    public static DirectionContext create(Vec3 pos) {
        DirectionContext context = new DirectionContext();
        context.direction = pos;
        return context;
    }

    @Override
    public void serialize(CompoundTag tag) {
        NBTTagHelper.putVectorDouble(tag, "direction", direction);
    }

    @Override
    public void deserialize(CompoundTag tag) {
        if(NBTTagHelper.hasVectorDouble(tag, "direction"))
            direction = NBTTagHelper.getVectorFromNBT(tag, "direction");
    }

    @Override
    public boolean valid() {
        return direction != null;
    }

    @Override
    public Type<DirectionContext> getType() {
        return TYPE;
    }

    @Override
    public String getString(int tab) {
        return direction.toString();
    }
}
