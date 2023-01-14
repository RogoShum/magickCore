package com.rogoshum.magickcore.api.entity;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IOwnerEntity {
    public void setOwner(@Nullable Entity entityIn);
    public void setOwnerUUID(UUID uuid) ;
    public Entity getOwner();
}
