package com.rogoshum.magickcore.api.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IOwnerEntity extends OwnableEntity {
    public void setCaster(@Nullable Entity entityIn);
    public void setCasterUUID(UUID uuid) ;
    public Entity getCaster();

    default Entity getOwner() {
        return getCaster();
    }

    default UUID getOwnerUUID() {
        if(getCaster() == null)
            return null;
        return getCaster().getUUID();
    }
}
