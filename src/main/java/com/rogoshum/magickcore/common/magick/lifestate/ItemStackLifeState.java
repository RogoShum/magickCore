package com.rogoshum.magickcore.common.magick.lifestate;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.world.World;

public class ItemStackLifeState extends LifeState<ItemStack> {
    @Override
    public INBT serialize() {
        return this.value.serializeNBT();
    }

    @Override
    public void deserialize(INBT value, World world) {
        this.value = ItemStack.read((CompoundNBT) value);
    }
}
