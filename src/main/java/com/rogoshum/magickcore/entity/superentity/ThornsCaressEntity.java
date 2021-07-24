package com.rogoshum.magickcore.entity.superentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.ISuperEntity;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.baseEntity.ManaPointEntity;
import com.rogoshum.magickcore.helper.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModSounds;
import com.rogoshum.magickcore.lib.LibBuff;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;

public class ThornsCaressEntity extends ManaPointEntity implements ISuperEntity {
    public ThornsCaressEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void tick() {
        super.tick();

        for(int i = 0; i < 30;++i)
        {
            Vector3d rand = new Vector3d(MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne());
            this.hitReactions.put(this.rand.nextInt(200) - this.rand.nextInt(2000), new VectorHitReaction(rand, 0.2F, 0.02F));
        }
        if(!this.world.isRemote && this.ticksExisted == 1)
        {
            this.playSound(ModSounds.wither_spawn.get(), 2.0F, 1.0F + this.rand.nextFloat() / 3);
        }
        if(!this.world.isRemote && this.ticksExisted % 13 == 0)
        {
            this.playSound(ModSounds.wither_ambience.get(), 0.7F, 0.85F - this.rand.nextFloat() / 5);
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
        this.traceEntity(null, 16, new Vector3d(0, this.getHeight() / 2, 0), 1f, 1);
        if(this.ticksExisted % 2 ==0) {
            HashMap<Integer, TrailParticle> trace = this.getTraceEntity();
            Iterator<Integer> ite = trace.keySet().iterator();
            while (ite.hasNext()) {
                int id = ite.next();
                Entity entity = this.world.getEntityByID(id);
                if(entity == null)
                    return;
                if(!MagickReleaseHelper.sameLikeOwner(this.getOwner(), entity)) {
                    ModBuff.applyBuff(entity, LibBuff.WITHER, 600, 2, false);
                    ModBuff.applyBuff(entity, LibBuff.CRIPPLE, 100, 5, false);
                    //this.getElement().getAbility().damageEntity(this, null, entity, 10, 10);
                    if (this.rand.nextInt(10) == 0) {
                        TrailParticle trail = trace.get(id);
                        for (Vector3d vec : trail.getTrailPoint()) {
                            if(this.rand.nextInt(4) == 0 && this.world.isRemote) {
                                LitParticle litPar = new LitParticle(this.world, this.getElement().getRenderer().getCycleTexture()
                                        , new Vector3d(vec.x
                                        , vec.y
                                        , vec.z)
                                        , MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne(), 0.6f + 0.4f * this.rand.nextFloat(), this.getElement().getRenderer().getParticleRenderTick(), this.getElement().getRenderer());
                                litPar.setGlow();
                                litPar.setParticleGravity(0f);
                                litPar.setShakeLimit(20.0f);
                                litPar.setLimitScale();
                                //litPar.addMotion(MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1);
                                MagickCore.addMagickParticle(litPar);
                            }
                        }
                    }
                }
            }
        }
        applyParticle();
    }

    protected void applyParticle()
    {
        if(this.world.isRemote() && this.getElement() != null)
        {
            if(this.ticksExisted % 2 == 0){
                LitParticle par = new LitParticle(this.world, this.getElement().getRenderer().getParticleTexture()
                        , new Vector3d(this.getPosX()
                        , this.getPosY() + this.getHeight() / 2
                        , this.getPosZ())
                        , 0.45f, 0.45f, this.rand.nextFloat(), 60, this.getElement().getRenderer());
                par.setGlow();
                //par.setParticleGravity(0);
                par.addMotion(MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.05);
                MagickCore.addMagickParticle(par);
            }
            if(this.ticksExisted % 5 == 0){
                LitParticle litPar = new LitParticle(this.world, this.getElement().getRenderer().getMistTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                        , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosY() + this.getHeight() / 2
                        , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosZ())
                        , this.rand.nextFloat() * this.getWidth(), this.rand.nextFloat() * this.getWidth(), 0.6f + 0.4f * this.rand.nextFloat(), this.getElement().getRenderer().getParticleRenderTick() / 4, this.getElement().getRenderer());
                litPar.setGlow();
                litPar.setParticleGravity(0f);
                litPar.setShakeLimit(35.0f);
                litPar.addMotion(MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1);
                MagickCore.addMagickParticle(litPar);
            }
        }
    }
}
