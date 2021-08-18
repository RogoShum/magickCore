package com.rogoshum.magickcore.magick.lifestate;

import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.world.World;

public class IntegerLifeState extends LifeState<Integer>{
    @Override
    public INBT serialize() {
        return IntNBT.valueOf(value);
    }

    @Override
    public void deserialize(INBT value, World world) {
        this.value = ((IntNBT)value).getInt();
    }
}
