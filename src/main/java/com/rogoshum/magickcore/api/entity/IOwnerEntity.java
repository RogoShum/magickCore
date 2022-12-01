package com.rogoshum.magickcore.api.entity;

import net.minecraft.entity.Entity;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IOwnerEntity {
    public void setOwner(@Nullable Entity entityIn);
    public void setOwnerUUID(UUID uuid) ;
    public Entity getOwner();
}
