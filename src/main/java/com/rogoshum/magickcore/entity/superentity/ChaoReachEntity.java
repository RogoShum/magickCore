package com.rogoshum.magickcore.entity.superentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.enums.EnumManaLimit;
import com.rogoshum.magickcore.api.entity.ISuperEntity;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.baseEntity.ManaPointEntity;
import com.rogoshum.magickcore.enums.EnumManaType;
import com.rogoshum.magickcore.tool.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModSounds;
import com.rogoshum.magickcore.magick.ReleaseAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;

public class ChaoReachEntity extends ManaPointEntity implements ISuperEntity {
    public ChaoReachEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.ticksExisted <= 30)
            return;
        initial = true;
        Vector3d rand = new Vector3d(MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne());
        this.hitReactions.put(this.rand.nextInt(200) - this.rand.nextInt(2000), new VectorHitReaction(rand, 0.2F, 0.005F));

        Iterator<Integer> iter = hitReactions.keySet().iterator();
        while (iter.hasNext()) {
            VectorHitReaction reaction = hitReactions.get(iter.next());
            //MagickCore.LOGGER.info("isInvalid " + reaction.isInvalid());
            if (reaction.isInvalid()) {
                iter.remove();
            }
            reaction.tick();
        }
        this.traceEntity(null, 32, new Vector3d(0, 0, 0), 1.0f, 1);
        boolean makeSound = false;
        //if(this.ticksExisted % 2 ==0) {
            HashMap<Integer, TrailParticle> trace = this.getTraceEntity();
            Iterator<Integer> ite = trace.keySet().iterator();
            while (ite.hasNext()) {
                int id = ite.next();
                Entity entity = this.world.getEntityByID(id);
                if(entity == null)
                    return;
                if(!MagickReleaseHelper.sameLikeOwner(this.getOwner(), entity) && MagickReleaseHelper.canEntityTraceAnother(this, entity)) {
                    makeSound = true;
                    ReleaseAttribute attribute = new ReleaseAttribute(this.getOwner(), this, entity, 50, EnumManaLimit.FORCE.getValue());
                    MagickReleaseHelper.applyElementFunction(this.getElement(), EnumManaType.DEBUFF, attribute);
                    attribute = new ReleaseAttribute(this.getOwner(), this, entity, 10, EnumManaLimit.FORCE.getValue());
                    MagickReleaseHelper.applyElementFunction(this.getElement(), EnumManaType.ATTACK, attribute);
                    if(this.world.isRemote) {
                        TrailParticle trail = trace.get(id);
                        for (Vector3d vec : trail.getTrailPoint()) {
                            if (this.rand.nextInt(40) == 0) {
                                LitParticle litPar = new LitParticle(this.world, this.getElement().getRenderer().getMistTexture()
                                        , new Vector3d(MagickCore.getNegativeToOne() + vec.x
                                        , MagickCore.getNegativeToOne() + vec.y + this.getHeight()
                                        , MagickCore.getNegativeToOne() + vec.z)
                                        , this.rand.nextFloat() * 1.5f, this.rand.nextFloat() * 1.5f, 0.6f + 0.4f * this.rand.nextFloat(), this.getElement().getRenderer().getParticleRenderTick(), this.getElement().getRenderer());
                                litPar.setGlow();
                                litPar.setParticleGravity(0f);
                                litPar.setShakeLimit(35.0f);
                                litPar.addMotion(MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1);
                                MagickCore.addMagickParticle(litPar);
                            }
                        }
                    }
                }
            }
        //}

        if(makeSound && this.ticksExisted % 2 == 0)
            this.playSound(ModSounds.chaos_attak.get(), 2.0F, 1.0F - this.rand.nextFloat() / 5);
        applyParticle();
    }

    @Override
    protected void makeSound() {
        if(this.ticksExisted == 1)
        {
            this.playSound(ModSounds.chaos_spawn.get(), 2.0F, 1.0F - this.rand.nextFloat());
        }

        if(this.ticksExisted > 20 && this.ticksExisted % 5 == 0)
        {
            this.playSound(ModSounds.chaos_ambience.get(), 1.0F, 1.0F + this.rand.nextFloat());
        }
    }

    protected void applyParticle()
    {
        if(this.world.isRemote() && this.getElement() != null)
        {
            for(int i = 0; i < 2; ++i) {
                LitParticle par = new LitParticle(this.world, this.getElement().getRenderer().getParticleTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                        , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosY() + this.getHeight() / 2
                        , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                        , 0.15f, 0.15f, this.rand.nextFloat(), 60, this.getElement().getRenderer());
                par.setGlow();
                par.setParticleGravity(0);
                par.addMotion(MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.2);
                MagickCore.addMagickParticle(par);
            }
            for(int i = 0; i < 1; ++i) {
                LitParticle litPar = new LitParticle(this.world, this.getElement().getRenderer().getMistTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                        , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosY() + this.getHeight() / 2
                        , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                        , this.rand.nextFloat() * this.getWidth() * this.getWidth(), this.rand.nextFloat() * this.getWidth() * this.getWidth(), 0.6f + 0.4f * this.rand.nextFloat(), this.getElement().getRenderer().getParticleRenderTick() / 2, this.getElement().getRenderer());
                litPar.setGlow();
                litPar.setParticleGravity(0f);
                litPar.setShakeLimit(35.0f);
                litPar.addMotion(MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1);
                MagickCore.addMagickParticle(litPar);
            }
        }
    }

    @Override
    public int getSourceLight() {
        return 15;
    }
}
