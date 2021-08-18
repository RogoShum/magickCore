package com.rogoshum.magickcore.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.baseEntity.ManaProjectileEntity;
import com.rogoshum.magickcore.tool.MagickReleaseHelper;
import com.rogoshum.magickcore.magick.ReleaseAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

public class ManaLaserEntity extends ManaProjectileEntity {
    public ManaLaserEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
    }

    protected void traceTarget()
    {
        if(this.getTraceTarget() != MagickCore.emptyUUID && !this.world.isRemote)
        {
            Entity entity = ((ServerWorld)this.world).getEntityByUuid(this.getTraceTarget());

            if(entity != null) {
                Vector3d goal = new Vector3d(entity.getPosX(), entity.getPosY() + entity.getHeight() / 1.5f, entity.getPosZ());
                Vector3d self = new Vector3d(this.getPosX(), this.getPosY(), this.getPosZ());

                Vector3d motion = goal.subtract(self).normalize().scale(this.getMotion().length() * 0.175);
                this.setMotion(motion.add(this.getMotion().scale(0.95)));
            }
            else
                this.setMotion(this.getMotion().scale(1.1));
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }

    @Override
    protected void applyParticle() {
        if(this.world.isRemote() && this.getElement() != null)
        {
            LitParticle par = new LitParticle(this.world, this.getElement().getRenderer().getParticleTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() + this.getPosX()
                    , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosY() + this.getHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                    , 0.15f, 0.15f, 1.0f, 10, this.getElement().getRenderer());
            par.setGlow();
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        if(this.getManaData() != null) {
            EntityEvents.HitEntityEvent event = new EntityEvents.HitEntityEvent(this, p_213868_1_.getEntity());
            MinecraftForge.EVENT_BUS.post(event);
            ReleaseAttribute attribute = new ReleaseAttribute(this.getOwner(), this, p_213868_1_.getEntity(), this.getTickTime(), this.getForce() / 3);
            MagickReleaseHelper.applyElementFunction(this.getElement(), this.getManaType(), attribute);
        }
    }
}
