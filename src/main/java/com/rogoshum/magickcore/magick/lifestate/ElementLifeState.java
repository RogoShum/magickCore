package com.rogoshum.magickcore.magick.lifestate;

import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.entity.LifeStateEntity;
import com.rogoshum.magickcore.init.ModElements;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

public class ElementLifeState extends LifeState<IManaElement>{
    @Override
    public INBT serialize() {
        return StringNBT.valueOf(this.value.getType());
    }

    @Override
    public void deserialize(INBT value, World world) {
        this.value = ModElements.getElement(((StringNBT)value).getString());
    }
}
