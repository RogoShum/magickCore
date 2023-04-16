package com.rogoshum.magickcore.common.extradata.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.extradata.EntityExtraData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.lib.LibBuff;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class LeechEntityData extends EntityExtraData {
    private LivingEntity owner = null;;
    private int count;
    private float force;

    public void setOwner(LivingEntity entityIn) {
        owner = entityIn;
    }

    public LivingEntity getOwner() {
        return owner;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setForce(float force) {
        this.force = force;
    }

    public float getForce() {
        return force;
    }


    public void tick(LivingEntity entity) {
        if(count <= 0) {
            owner = null;
            force = 0;
        }

        if(entity.level instanceof ServerLevel && this.owner != null && this.owner.isAlive() && entity.tickCount % 20 == 0) {
            float health = Math.min(0.25f*getForce(), entity.getHealth());
            this.owner.heal(health);
            entity.setHealth(Math.max(0.1f, entity.getHealth() - health));
            entity.level.playSound(null, entity, SoundEvents.GENERIC_HURT, SoundSource.NEUTRAL, 0.5f, 1f);
            this.count--;
        }
    }

    @Override
    public boolean isEntitySuitable(Entity entity) {
        return entity instanceof LivingEntity;
    }

    @Override
    public void read(CompoundTag nbt) {
    }

    @Override
    public void write(CompoundTag nbt) {
    }
}
