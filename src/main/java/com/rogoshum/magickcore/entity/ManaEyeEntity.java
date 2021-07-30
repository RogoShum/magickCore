package com.rogoshum.magickcore.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.enums.EnumManaType;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.baseEntity.ManaPointEntity;
import com.rogoshum.magickcore.helper.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModEntites;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class ManaEyeEntity extends ManaPointEntity {
    public ManaEyeEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void tick() {
        super.tick();

        if(!this.world.isRemote && this.ticksExisted == 1)
        {
            this.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, 2.0F, 1.0F + this.rand.nextFloat());
        }

        if(this.ticksExisted % 20 == 0) {
            Vector3d rand = new Vector3d(MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne());
            this.hitReactions.put(this.rand.nextInt(200) - this.rand.nextInt(2000), new VectorHitReaction(rand, 0.2F, 0.01F));
        }

        if(this.ticksExisted % 100 == 0) {
            List<LivingEntity> entityList =  getLivingEntity(32);
            for(LivingEntity living : entityList)
            {
                ManaStarEntity starEntity = new ManaStarEntity(ModEntites.mana_star, this.world);

                String magickType = getTraceTarget() == MagickCore.emptyUUID_EYE ? "EYE_STAR" : "EYE_STAR_TRACE";

                EntityEvents.MagickReleaseEvent event = new EntityEvents.MagickReleaseEvent(this, this.getElement(), this.getForce(), this.getTickTime(), this.getManaType(), living.getUniqueID(), this.getRange(), magickType);
                MinecraftForge.EVENT_BUS.post(event);

                starEntity.setShooter(this.getOwner());
                starEntity.setPosition(this.getPosX(), this.getPosY() + this.getHeight() / 2, this.getPosZ());
                Vector3d motion = living.getPositionVec().add(0, living.getHeight() / 2, 0).subtract(starEntity.getPositionVec()).normalize();
                starEntity.shoot(motion.x, motion.y, motion.z, 0.5f, 0.9f);
                starEntity.setElement(event.getElement());
                starEntity.setForce(event.getForce());
                starEntity.setManaType(event.getType());
                starEntity.setTickTime(event.getTick());
                starEntity.setRange(event.getRange());

                if(this.getManaType().equals(EnumManaType.BUFF) && MagickReleaseHelper.sameLikeOwner(this.getOwner(), living)) {
                    starEntity.setTraceTarget(event.getTrace());
                    if (!this.world.isRemote())
                        this.world.addEntity(starEntity);
                }
                if((this.getManaType().equals(EnumManaType.DEBUFF) || this.getManaType().equals(EnumManaType.ATTACK)) && !MagickReleaseHelper.sameLikeOwner(this.getOwner(), living)) {

                    starEntity.setTraceTarget(event.getTrace());
                    if (!this.world.isRemote())
                        this.world.addEntity(starEntity);
                }

            }
        }
        applyParticle();
    }

    protected void applyParticle()
    {
        if(this.world.isRemote() && this.getElement() != null)
        {
            for (int i = 0; i < 4; ++i) {
                LitParticle par = new LitParticle(this.world, this.getElement().getRenderer().getParticleTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() * 0.25 + this.getPosX()
                        , MagickCore.getNegativeToOne() * this.getWidth() * 0.25 + this.getPosY() + this.getHeight() / 2
                        , MagickCore.getNegativeToOne() * this.getWidth() * 0.25 + this.getPosZ())
                        , 0.1f, 0.1f, this.rand.nextFloat(), 40, this.getElement().getRenderer());
                par.setGlow();
                par.setParticleGravity(0f);
                par.addMotion(MagickCore.getNegativeToOne() * 0.01, MagickCore.getNegativeToOne() * 0.15, MagickCore.getNegativeToOne() * 0.01);
                MagickCore.addMagickParticle(par);
            }

            float scale = this.rand.nextFloat() * this.getWidth() * 1.2f;
            for (int i = 0; i < 2; ++i) {
                LitParticle litPar = new LitParticle(this.world, this.getElement().getRenderer().getMistTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                        , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosY() + this.getHeight() / 3
                        , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosZ())
                        , scale, scale, this.rand.nextFloat() * 0.5f + 0.5f, this.getElement().getRenderer().getParticleRenderTick() / 2, this.getElement().getRenderer());
                litPar.setGlow();
                litPar.setParticleGravity(0f);
                litPar.setShakeLimit(15.0f);
                litPar.addMotion(MagickCore.getNegativeToOne() * 0.02, MagickCore.getNegativeToOne() * 0.02, MagickCore.getNegativeToOne() * 0.02);
                MagickCore.addMagickParticle(litPar);
            }
        }
    }
}
