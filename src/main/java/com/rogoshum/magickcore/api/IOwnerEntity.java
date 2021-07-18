package com.rogoshum.magickcore.api;

import net.minecraft.entity.Entity;

import javax.annotation.Nullable;

public interface IOwnerEntity {
    public void setOwner(@Nullable Entity entityIn) ;
    public Entity getOwner();
}
