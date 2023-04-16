package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public class OffsetContext extends ChildContext{
    public static final Type<OffsetContext> TYPE = new Type<>(LibContext.OFFSET);
    public Vec3 direction = Vec3.ZERO;

    public static OffsetContext create(Vec3 pos) {
        OffsetContext context = new OffsetContext();
        context.direction = pos;
        return context;
    }

    public OffsetContext add(Vec3 direction) {
        this.direction.add(direction);
        return this;
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
    public Type<OffsetContext> getType() {
        return TYPE;
    }

    @Override
    public String getString(int tab) {
        return direction.toString();
    }
}
