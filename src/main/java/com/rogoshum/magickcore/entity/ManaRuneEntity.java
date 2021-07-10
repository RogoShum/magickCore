package com.rogoshum.magickcore.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.entity.baseEntity.ManaPointEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ManaRuneEntity extends ManaPointEntity {
    public ManaRuneEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void tick() {
        this.setTickTime(this.ticksExisted + 20);

        if(this.getOwner() instanceof LivingEntity)
        {
            this.setElement(this.getOwner().getCapability(MagickCore.entityState).orElse(null).getElement());
        }

        super.tick();
    }
}
