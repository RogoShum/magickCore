package com.rogoshum.magickcore.common.magick.lifestate;

import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.world.World;

public class ElementLifeState extends LifeState<MagickElement>{
    @Override
    public INBT serialize() {
        return StringNBT.valueOf(this.value.type());
    }

    @Override
    public void deserialize(INBT value, World world) {
        this.value = MagickRegistry.getElement(((StringNBT)value).getString());
    }
}
