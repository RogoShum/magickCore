package com.rogoshum.magickcore.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;

import javax.annotation.Nullable;
import java.util.UUID;

public interface ITakenState {
    public void setOwner(UUID entityIn) ;
    public UUID getOwnerUUID();

    public void setTime(int time) ;
    public int getTime();

    public int getRange();
    public void tick(MobEntity entity);
}
