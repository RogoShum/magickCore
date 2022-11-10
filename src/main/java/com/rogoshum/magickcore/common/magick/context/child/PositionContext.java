package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.lib.LibContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;

public class PositionContext extends ChildContext{
    public Vector3d pos = Vector3d.ZERO;

    public static PositionContext create(Vector3d pos) {
        PositionContext context = new PositionContext();
        context.pos = pos;
        return context;
    }

    @Override
    public void serialize(CompoundNBT tag) {
        NBTTagHelper.putVectorDouble(tag, "pos", pos);
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        if(NBTTagHelper.hasVectorDouble(tag, "pos"))
            pos = NBTTagHelper.getVectorFromNBT(tag, "pos");
    }

    @Override
    public boolean valid() {
        return pos != null;
    }

    @Override
    public String getName() {
        return LibContext.POSITION;
    }

    @Override
    public String getString(int tab) {
        return pos.toString();
    }
}
