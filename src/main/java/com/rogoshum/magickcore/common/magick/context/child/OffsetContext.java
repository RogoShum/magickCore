package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;

public class OffsetContext extends ChildContext{
    public Vector3d direction = Vector3d.ZERO;

    public static OffsetContext create(Vector3d pos) {
        OffsetContext context = new OffsetContext();
        context.direction = pos;
        return context;
    }

    public OffsetContext add(Vector3d direction) {
        this.direction.add(direction);
        return this;
    }

    @Override
    public void serialize(CompoundNBT tag) {
        NBTTagHelper.putVectorDouble(tag, "direction", direction);
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        if(NBTTagHelper.hasVectorDouble(tag, "direction"))
            direction = NBTTagHelper.getVectorFromNBT(tag, "direction");
    }

    @Override
    public boolean valid() {
        return direction != null;
    }

    @Override
    public String getName() {
        return LibContext.OFFSET;
    }

    @Override
    public String getString(int tab) {
        return direction.toString();
    }
}
