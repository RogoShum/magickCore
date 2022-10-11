package com.rogoshum.magickcore.magick.context.child;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.tool.NBTTagHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;

public class DirectionContext extends ChildContext{
    public Vector3d direction = Vector3d.ZERO;

    public static DirectionContext create(Vector3d pos) {
        DirectionContext context = new DirectionContext();
        context.direction = pos;
        return context;
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
        return LibContext.DIRECTION;
    }

    @Override
    public String getString(int tab) {
        return direction.toString();
    }
}
