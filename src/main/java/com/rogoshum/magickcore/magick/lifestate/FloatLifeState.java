package com.rogoshum.magickcore.magick.lifestate;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.world.World;

public class FloatLifeState extends LifeState<Float>{
    @Override
    public INBT serialize() {
        return FloatNBT.valueOf(value);
    }

    @Override
    public void deserialize(INBT value, World world) {
        this.value = ((FloatNBT)value).getFloat();
    }
}
