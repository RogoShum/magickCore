package com.rogoshum.magickcore.entity.superentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.ISuperEntity;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.baseEntity.ManaEntity;
import com.rogoshum.magickcore.entity.baseEntity.ManaPointEntity;
import com.rogoshum.magickcore.helper.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModSounds;
import com.rogoshum.magickcore.lib.LibBuff;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;

public class SilenceSquallEntity extends ManaEntity implements ISuperEntity {
    public SilenceSquallEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
        Entity cloest = null;
        if(!this.world.isRemote && this.ticksExisted == 1)
        {
            this.playSound(ModSounds.squal_spawn.get(), 1.0F, 1.0F + this.rand.nextFloat() / 3);
        }

        if(!this.world.isRemote && this.rand.nextBoolean())
        {
            this.playSound(SoundEvents.UI_TOAST_OUT, 1.0F, 1.0F + this.rand.nextFloat());
        }
        if(!this.world.isRemote && this.ticksExisted % 12 == 0)
        {
            this.playSound(ModSounds.squal_ambience.get(), 1.0F, 1.3F - this.rand.nextFloat() / 3);
        }

        this.traceEntity(null, 16, new Vector3d(0, 0, 0), 0.0f, 0);
        //if(this.ticksExisted % 2 ==0) {
            HashMap<Integer, TrailParticle> trace = this.getTraceEntity();
            Iterator<Integer> ite = trace.keySet().iterator();
            while (ite.hasNext()) {
                int id = ite.next();
                Entity entity = this.world.getEntityByID(id);
                if(entity == null)
                    return;
                if(!MagickReleaseHelper.sameLikeOwner(this.getOwner(), entity)) {
                    if(cloest == null || this.getDistance(entity) < this.getDistance(cloest))
                        cloest = entity;
                    if(this.getDistance(entity) <= 9.5)
                        ModBuff.applyBuff(entity, LibBuff.SLOW, 200, 4, false);
                    if(this.getDistance(entity) <= 3) {
                        ModBuff.applyBuff(entity, LibBuff.FREEZE, 200, 1, false);
                        if(this.ticksExisted % 20 == 0)
                        {
                            this.getElement().getAbility().damageEntity(this.getOwner(), this, entity, 20, 1f);
                            entity.hurtResistantTime = 0;
                        }
                    }
                }
            //}
        }

        if(cloest != null && cloest.isAlive()) {
            Vector3d vec = cloest.getPositionVec().add(0, 2, 0).subtract(this.getPositionVec());
            this.setMotion(vec.normalize().scale(0.1));
        }
        else if(this.getOwner() != null)
        {
            Vector3d vec = this.getOwner().getPositionVec().add(0, 2, 0).subtract(this.getPositionVec());
            this.setMotion(vec.normalize().scale(0.1));
        }
        this.prevPosX = this.getPosX();
        this.prevPosY = this.getPosY();
        this.prevPosZ = this.getPosZ();
        this.setPosition(this.getPosX() + this.getMotion().x, this.getPosY() + this.getMotion().y, this.getPosZ() + this.getMotion().z);
        this.setMotion(this.getMotion().scale(0.9));
        //this.setNoGravity(true);
        applyParticle();
    }

    protected void applyParticle()
    {
        if(this.world.isRemote() && this.getElement() != null)
        {
            for(int i = 0; i < 5; ++i) {
                LitParticle par = new LitParticle(this.world, this.getElement().getRenderer().getParticleTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * 18 + this.getPosX()
                        , MagickCore.getNegativeToOne() * 18 + this.getPosY() + this.getHeight() / 2
                        , MagickCore.getNegativeToOne() * 18 + this.getPosZ())
                        , MagickCore.getNegativeToOne() / 2, MagickCore.getNegativeToOne() / 2, 0.6f + 0.4f * this.rand.nextFloat(), 300, this.getElement().getRenderer());
                par.setGlow();
                par.setParticleGravity(0);
                par.setTraceTarget(this);
                par.addMotion(MagickCore.getNegativeToOne() * 0.005, MagickCore.getNegativeToOne() * 0.005, MagickCore.getNegativeToOne() * 0.005);
                MagickCore.addMagickParticle(par);
            }

            for(int i = 0; i < 5; ++i) {
                LitParticle par = new LitParticle(this.world, this.getElement().getRenderer().getTrailTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * 18 + this.getPosX()
                        , MagickCore.getNegativeToOne() * 18 + this.getPosY() + this.getHeight() / 2
                        , MagickCore.getNegativeToOne() * 18 + this.getPosZ())
                        , MagickCore.getNegativeToOne() / 4, MagickCore.getNegativeToOne() / 8, 1.0f, 300, this.getElement().getRenderer());
                par.setGlow();
                par.setParticleGravity(0);
                par.setTraceTarget(this);
                par.addMotion(MagickCore.getNegativeToOne() * 0.005, MagickCore.getNegativeToOne() * 0.005, MagickCore.getNegativeToOne() * 0.005);
                MagickCore.addMagickParticle(par);
            }

            for(int i = 0; i < 1; ++i) {
                LitParticle litPar = new LitParticle(this.world, this.getElement().getRenderer().getMistTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * 8 + this.getPosX()
                        , MagickCore.getNegativeToOne() * 6 + this.getPosY() + this.getHeight() / 2
                        , MagickCore.getNegativeToOne() * 8 + this.getPosZ())
                        , this.rand.nextFloat() * this.getWidth() * 1.5f, this.rand.nextFloat() * this.getWidth() * 1.5f, 0.3f + 0.2f * this.rand.nextFloat(), this.getElement().getRenderer().getParticleRenderTick(), this.getElement().getRenderer());
                litPar.setGlow();
                litPar.setParticleGravity(0f);
                litPar.setShakeLimit(15.0f);
                litPar.addMotion(MagickCore.getNegativeToOne() * 0.15, MagickCore.getNegativeToOne() * 0.15, MagickCore.getNegativeToOne() * 0.15);
                MagickCore.addMagickParticle(litPar);
            }
        }
    }
}
