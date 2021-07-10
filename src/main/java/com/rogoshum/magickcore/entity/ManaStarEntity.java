package com.rogoshum.magickcore.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.entity.baseEntity.ManaProjectileEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Iterator;

public class ManaStarEntity extends ManaProjectileEntity {
    public ManaStarEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
    }

    protected void traceTarget()
    {
        if(this.ticksExisted > 3 && this.getTraceTarget() != MagickCore.emptyUUID && !this.world.isRemote)
        {
            Entity entity = ((ServerWorld)this.world).getEntityByUuid(this.getTraceTarget());

            if(entity != null) {
                Vector3d goal = new Vector3d(entity.getPosX(), entity.getPosY() + entity.getEyeHeight(), entity.getPosZ());
                Vector3d self = new Vector3d(this.getPosX(), this.getPosY(), this.getPosZ());

                Vector3d motion = goal.subtract(self).normalize().scale(this.getMotion().length() * 0.1);
                this.setMotion(motion.add(this.getMotion()));
            }
        }
    }
}
