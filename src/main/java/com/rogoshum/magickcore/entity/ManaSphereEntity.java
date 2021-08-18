package com.rogoshum.magickcore.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.baseEntity.ManaPointEntity;
import com.rogoshum.magickcore.tool.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModSounds;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.magick.ReleaseAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ManaSphereEntity extends ManaPointEntity {
    public ManaSphereEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void tick() {
        super.tick();

        //for(int i = 0; i < 3;++i)
        {
            Vector3d rand = new Vector3d(MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne());
            this.hitReactions.put(this.rand.nextInt(200) - this.rand.nextInt(2000), new VectorHitReaction(rand, 0.3F, 0.05F));
        }

        Iterator<Integer> iter = hitReactions.keySet().iterator();
        while (iter.hasNext()) {
            VectorHitReaction reaction = hitReactions.get(iter.next());
            //MagickCore.LOGGER.info("isInvalid " + reaction.isInvalid());
            if (reaction.isInvalid()) {
                iter.remove();
            }
            reaction.tick();
        }
        this.traceEntity(null, 0, new Vector3d(0, 0, 0), 0.0f, 0);
        HashMap<Integer, TrailParticle> trace = this.getTraceEntity();
        Iterator<Integer> ite = trace.keySet().iterator();

        while (ite.hasNext()) {
            Integer id = ite.next();
            Entity entity = this.world.getEntityByID(id);
            if (entity == null)
                return;
                if(this.getManaData() != null) {
                    EntityEvents.HitEntityEvent event = new EntityEvents.HitEntityEvent(this, entity);
                    MinecraftForge.EVENT_BUS.post(event);
                    ReleaseAttribute attribute = new ReleaseAttribute(this.getOwner(), this, entity, this.getTickTime(), this.getForce() / 5);
                    MagickReleaseHelper.applyElementFunction(this.getElement(), this.getManaType(), attribute);
                }
            }
        applyParticle();
    }

    @Override
    protected void makeSound() {
        if(!this.world.isRemote && this.ticksExisted == 1)
        {
            this.playSound(ModSounds.sphere_spawn.get(), 1.0F, 1.0F - this.rand.nextFloat() / 5);
        }

        if(!this.world.isRemote && this.ticksExisted == 10)
            this.playSound(ModSounds.sphere_ambience.get(), 1.0F, (0.85F - this.rand.nextFloat() / 5));

        if(!this.world.isRemote && this.ticksExisted % 15 == 0 && this.ticksExisted < this.getTickTime() - 10 && this.ticksExisted > 10)
        {
            this.playSound(ModSounds.sphere_ambience.get(), 1.0F, (0.85F - this.rand.nextFloat() / 5));
        }

        if(!this.world.isRemote && this.ticksExisted == this.getTickTime() - 20)
        {
            this.playSound(ModSounds.shpere_dissipate.get(), 0.5F, (1.0F - this.rand.nextFloat()));
        }
    }

    @Override
    public int getSourceLight() {
        return 8;
    }

    @Override
    protected void collideWithNearbyEntities() {
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().grow(1.2));
        if (!list.isEmpty()) {
            for(int l = 0; l < list.size(); ++l) {
                Entity entity = list.get(l);

                if(!MagickReleaseHelper.sameLikeOwner(this.getOwner(), entity) && !ModBuff.hasBuff(entity, LibBuff.FREEZE))
                    this.applyEntityCollision(entity);
            }
        }
    }

    @Override
    public void applyEntityCollision(Entity entityIn) {
                double d0 = entityIn.getPosX() - this.getPosX();
                double d1 = entityIn.getPosZ() - this.getPosZ();
                double d2 = MathHelper.absMax(d0, d1);
                if (d2 >= (double)0.01F) {
                    d2 = (double)MathHelper.sqrt(d2);
                    d0 = d0 / d2;
                    d1 = d1 / d2;
                    double d3 = 1.0D / d2;
                    if (d3 > 1.0D) {
                        d3 = 1.0D;
                    }

                    d0 = d0 * d3;
                    d1 = d1 * d3;
                    d0 = d0 * (double)0.05F;
                    d1 = d1 * (double)0.05F;
                    d0 = d0 * (double)(1.0F - 1.5);
                    d1 = d1 * (double)(1.0F - 1.5);
                    if (!this.isBeingRidden()) {
                        this.addVelocity(-d0, 0.0D, -d1);
                    }

                    if (!entityIn.isBeingRidden()) {
                        entityIn.addVelocity(d0, 0.0D, d1);
                    }
                }
    }

    protected void applyParticle()
    {
        if(this.world.isRemote() && this.getElement() != null)
        {
            for(int i = 0; i < 2; ++i) {
                LitParticle par = new LitParticle(this.world, this.getElement().getRenderer().getParticleTexture()
                        , new Vector3d(this.getPosX()
                        , this.getPosY() + this.getHeight() / 2
                        , this.getPosZ())
                        , 0.15f, 0.15f, this.rand.nextFloat(), 40, this.getElement().getRenderer());
                par.setGlow();
                par.addMotion(MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.2);
                MagickCore.addMagickParticle(par);
            }
            for(int i = 0; i < 3; ++i) {
                LitParticle litPar = new LitParticle(this.world, this.getElement().getRenderer().getMistTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                        , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosY() + this.getHeight() / 2
                        , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosZ())
                        , this.rand.nextFloat() * this.getWidth() * 1.4f, this.rand.nextFloat() * this.getWidth() * 1.4f, 0.5f * this.rand.nextFloat(), this.getElement().getRenderer().getParticleRenderTick() / 2, this.getElement().getRenderer());
                litPar.setGlow();
                litPar.setParticleGravity(0f);
                litPar.setShakeLimit(15.0f);
                litPar.setCanCollide(false);
                litPar.addMotion(MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.2);
                MagickCore.addMagickParticle(litPar);
            }
        }
    }
}
