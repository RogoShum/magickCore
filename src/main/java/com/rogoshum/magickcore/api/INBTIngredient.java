package com.rogoshum.magickcore.api;

import net.minecraft.nbt.CompoundTag;

public interface INBTIngredient {
    void setNBTMap(CompoundTag tag);

    CompoundTag getNBTMap();
}
