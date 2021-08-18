package com.rogoshum.magickcore.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.baseEntity.ManaPointEntity;
import com.rogoshum.magickcore.enums.EnumManaType;
import com.rogoshum.magickcore.tool.MagickReleaseHelper;
import com.rogoshum.magickcore.magick.ReleaseAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundEvents;
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
        if(!this.world.isRemote && this.ticksExisted == 1)
        {
            this.playSound(SoundEvents.BLOCK_BAMBOO_SAPLING_PLACE, 2.0F, 1.0F + this.rand.nextFloat());
        }

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

        List<LivingEntity> livings = getLivingEntity(1);
        for (LivingEntity living : livings) {
            ReleaseAttribute attribute = new ReleaseAttribute(this.getOwner(), this, living, 100, this.getForce());
            if (MagickReleaseHelper.sameLikeOwner(this.getOwner(), living)) {
                MagickReleaseHelper.applyElementFunction(this.getElement(), EnumManaType.BUFF, attribute);
            } else {
                MagickReleaseHelper.applyElementFunction(this.getElement(), EnumManaType.DEBUFF, attribute);
            }
        }

        applyParticle();
    }

    @Override
    public int getSourceLight() {
        return 8;
    }

    protected void applyParticle()
    {
        if(this.world.isRemote() && this.getElement() != null)
        {
            for(int i = 0; i < 1; ++i) {
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
