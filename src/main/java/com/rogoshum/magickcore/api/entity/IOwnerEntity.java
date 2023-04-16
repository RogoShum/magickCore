package com.rogoshum.magickcore.api.entity;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IOwnerEntity {
    public void setCaster(@Nullable Entity entityIn);
    public void setCasterUUID(UUID uuid) ;
    public Entity getCaster();
}
