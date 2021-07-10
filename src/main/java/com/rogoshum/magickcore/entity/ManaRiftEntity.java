package com.rogoshum.magickcore.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.baseEntity.ManaPointEntity;
import com.rogoshum.magickcore.init.ModBuff;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

public class ManaRiftEntity extends ManaPointEntity {
    public ManaRiftEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void tick() {
        super.tick();

        Vector3d rand = new Vector3d(MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne());
        this.hitReactions.put(this.rand.nextInt(200) - this.rand.nextInt(2000), new VectorHitReaction(rand, 0.1F, 0.005F));

        Iterator<Integer> iter = hitReactions.keySet().iterator();
        while (iter.hasNext()) {
            VectorHitReaction reaction = hitReactions.get(iter.next());
            //MagickCore.LOGGER.info("isInvalid " + reaction.isInvalid());
            if (reaction.isInvalid()) {
                iter.remove();
            }
            reaction.tick();
        }

        List<LivingEntity> livings = getLivingEntity(3);
        for(LivingEntity living : livings)
        {
            if(living instanceof PlayerEntity)
                this.getElement().getAbility().applyBuff(living, 100, this.getForce());
            else
                this.getElement().getAbility().applyDebuff(living, 100, this.getForce());
        }
        applyParticle();
    }

    protected void applyParticle()
    {
        if(this.world.isRemote() && this.getElement() != null)
        {
            for(int i = 0; i < 2; ++i) {
                LitParticle par = new LitParticle(this.world, this.getElement().getRenderer().getParticleTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                        , this.getPosY() + this.getHeight() / 5
                        , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                        , 0.15f, 0.15f, this.rand.nextFloat(), 60, this.getElement().getRenderer());
                par.setGlow();
                par.addMotion(MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.2);
                MagickCore.addMagickParticle(par);
            }

            for(int i = 0; i < 1; ++i) {
                LitParticle par = new LitParticle(this.world, this.getElement().getRenderer().getParticleTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 5 + this.getPosX()
                        , this.getPosY() + this.getHeight() / 5
                        , MagickCore.getNegativeToOne() * this.getWidth() / 5 + this.getPosZ())
                        , this.rand.nextFloat() * this.getWidth() / 4, this.rand.nextFloat() * this.getWidth() / 4, this.rand.nextFloat(), 60, this.getElement().getRenderer());
                par.setGlow();
                par.addMotion(MagickCore.getNegativeToOne() * 0.01, MagickCore.getNegativeToOne() * 0.01, MagickCore.getNegativeToOne() * 0.01);
                MagickCore.addMagickParticle(par);
            }

            for(int i = 0; i < 1; ++i) {
                LitParticle litPar = new LitParticle(this.world, this.getElement().getRenderer().getMistTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                        , this.getPosY() + this.getHeight() / 6
                        , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                        , this.rand.nextFloat() * this.getWidth() / 4, this.rand.nextFloat() * this.getWidth() / 4, 0.5f * this.rand.nextFloat(), this.getElement().getRenderer().getParticleRenderTick(), this.getElement().getRenderer());
                litPar.setGlow();
                litPar.setParticleGravity(0f);
                litPar.setShakeLimit(15.0f);
                litPar.addMotion(MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.05);
                MagickCore.addMagickParticle(litPar);
            }
        }
    }
}
