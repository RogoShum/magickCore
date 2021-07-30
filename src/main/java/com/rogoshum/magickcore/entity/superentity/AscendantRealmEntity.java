package com.rogoshum.magickcore.entity.superentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.enums.EnumManaLimit;
import com.rogoshum.magickcore.api.ISuperEntity;
import com.rogoshum.magickcore.capability.ITakenState;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.baseEntity.ManaPointEntity;
import com.rogoshum.magickcore.helper.MagickReleaseHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;

public class AscendantRealmEntity extends ManaPointEntity implements ISuperEntity {
    public AscendantRealmEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.world.isRemote && this.ticksExisted == 1)
        {
            this.playSound(SoundEvents.ENTITY_BLAZE_DEATH, 2.0F, 1.0F - this.rand.nextFloat());
        }

        if(!this.world.isRemote && this.rand.nextInt(200) == 0)
        {
            this.playSound(SoundEvents.ENTITY_BLAZE_AMBIENT, 2.0F, 1.0F - this.rand.nextFloat());
        }
        applyParticle();
        List<LivingEntity> list = getLivingEntity(1);

        for (LivingEntity living : list)
        {
            if(!(living instanceof MobEntity))
                continue;
            ITakenState state = living.getCapability(MagickCore.takenState).orElse(null);
            if(living.isAlive() && !state.getOwnerUUID().equals(this.getOwnerUUID()) && !MagickReleaseHelper.sameLikeOwner(this.getOwner(), living))
            {
                this.getElement().getAbility().hitEntity(this, living, this.getTickTime(), 1);
                this.getElement().getAbility().damageEntity(this.getOwner(), this, living, this.getTickTime() / 4, EnumManaLimit.FORCE.getValue());
            }
        }
    }

    protected void applyParticle()
    {
        if(this.world.isRemote() && this.getElement() != null)
        {
            for(int i = 0; i < 5; ++i) {
                LitParticle par = new LitParticle(this.world, this.getElement().getRenderer().getParticleTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() + this.getPosX()
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
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() + this.getPosX()
                        , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosY()
                        , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                        , this.rand.nextFloat(), this.rand.nextFloat(), 0.3f + 0.3f * this.rand.nextFloat(), this.getElement().getRenderer().getParticleRenderTick(), this.getElement().getRenderer());
                litPar.setGlow();
                litPar.setParticleGravity(0f);
                litPar.setShakeLimit(35.0f);
                litPar.addMotion(MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.01, MagickCore.getNegativeToOne() * 0.1);
                MagickCore.addMagickParticle(litPar);
            }
        }
    }
}
