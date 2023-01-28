package com.rogoshum.magickcore.mixin.fabric.reflection;

import net.minecraft.world.entity.EntityDimensions;

public interface IScaleEntity {
    void setEntityDimensions(EntityDimensions dimensions);
    EntityDimensions getEntityDimensions();

    void setEyeHeight(float eyeHeight);
}
