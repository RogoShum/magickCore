package com.rogoshum.magickcore.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.baseEntity.ManaEntity;
import com.rogoshum.magickcore.entity.baseEntity.ManaPointEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ManaPowerEntity extends ManaPointEntity {
    private static final DataParameter<Float> MANA = EntityDataManager.createKey(ManaPowerEntity.class, DataSerializers.FLOAT);

    public ManaPowerEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void registerData() {
        this.dataManager.register(MANA, 0f);
    }

    public ManaPowerEntity setMana(float health)
    {
        this.getDataManager().set(MANA, health / 100f);
        return this;
    }

    public float getMana()
    {
        return this.getDataManager().get(MANA);
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.world.isRemote && this.ticksExisted % 20 == 0)
        {
            this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.0F, 1.0F + this.rand.nextFloat());
        }
        if(this.world.isRemote)
        {
            for(int i = 0; i < 2; ++i) {
                LitParticle par = new LitParticle(this.world, this.getElement().getRenderer().getTrailTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() / 5 + this.getPosX()
                        , MagickCore.getNegativeToOne() / 5 + this.getPosY() + this.getHeight() / 2
                        , MagickCore.getNegativeToOne() / 5 + this.getPosZ())
                        , 0.1f, 0.1f, 1.0f, 40, this.getElement().getRenderer());
                par.setGlow();
                par.setParticleGravity(0);
                par.addMotion(MagickCore.getNegativeToOne() * 0.01, MagickCore.getNegativeToOne() * 0.01, MagickCore.getNegativeToOne() * 0.01);
                MagickCore.addMagickParticle(par);
            }
        }

        if(getMana() > 0)
        {
            List<LivingEntity> livings = getLivingEntity(7);

            for (LivingEntity living : livings) {

                if (living instanceof PlayerEntity) {
                    IEntityState state = living.getCapability(MagickCore.entityState).orElse(null);
                    state.setMaxManaValue(state.getMaxManaValue() + getMana());
                    if (this.world.isRemote) {
                        int age = (int) (this.getDistance(living) * 2);
                        for (int i = 0; i < 5; ++i) {
                            LitParticle par = new LitParticle(this.world, this.getElement().getRenderer().getParticleTexture()
                                    , new Vector3d(MagickCore.getNegativeToOne() / 5 + this.getPosX()
                                    , MagickCore.getNegativeToOne() / 5 + this.getPosY() + this.getHeight() / 2
                                    , MagickCore.getNegativeToOne() / 5 + this.getPosZ())
                                    , MagickCore.getNegativeToOne() * 0.1f, MagickCore.getNegativeToOne() * 0.1f, 1.0f, age, this.getElement().getRenderer());
                            par.setGlow();
                            par.setParticleGravity(0);
                            par.setTraceTarget(living);
                            par.addMotion(MagickCore.getNegativeToOne() * 0.005, MagickCore.getNegativeToOne() * 0.005, MagickCore.getNegativeToOne() * 0.005);
                            MagickCore.addMagickParticle(par);
                        }
                    }
                }
            }
        }
        else if(this.ticksExisted > 20)
            this.remove();
    }
}
