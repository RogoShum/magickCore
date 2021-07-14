package com.rogoshum.magickcore.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaItem;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.baseEntity.ManaProjectileEntity;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.item.ManaItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;

public class ManaElementOrbEntity extends ManaProjectileEntity {
    public ManaElementOrbEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.world.isRemote && this.ticksExisted == 1)
        {
            this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.0F, 1.0F - this.rand.nextFloat());
        }
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox());
        for(Entity entity : list)
        {
            boolean flag = false;
            if(entity instanceof LivingEntity && entity.isAlive())
            {
                IEntityState state = entity.getCapability(MagickCore.entityState).orElse(null);
                state.setManaValue(state.getManaValue() + 100);
                this.remove();
                flag = true;
            }
            if(flag) continue;
        }
    }

    @Override
    protected void applyParticle() {
        if(this.world.isRemote() && this.getElement() != null)
        {
            LitParticle par = new LitParticle(this.world, this.getElement().getRenderer().getParticleTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                    , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosY() + this.getHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosZ())
                    , this.rand.nextFloat() * 0.03f, this.rand.nextFloat() * 0.03f, 1.0f, 40, this.getElement().getRenderer());
            par.setGlow();
            par.setLimitScale();
            MagickCore.addMagickParticle(par);


                LitParticle litPar = new LitParticle(this.world, this.getElement().getRenderer().getMistTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                        , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosY() + this.getHeight() / 2
                        , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosZ())
                        , this.rand.nextFloat() * 0.2f, this.rand.nextFloat() * 0.2f, 0.8f, this.getElement().getRenderer().getParticleRenderTick() / 2, this.getElement().getRenderer());
                litPar.setGlow();
                litPar.setParticleGravity(0f);
                litPar.setShakeLimit(15.0f);
                //litPar.setLimitScale();
                MagickCore.addMagickParticle(litPar);
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }
}
