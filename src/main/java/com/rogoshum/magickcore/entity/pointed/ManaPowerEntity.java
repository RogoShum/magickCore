package com.rogoshum.magickcore.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.magick.context.SpellContext;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class ManaPowerEntity extends ManaPointEntity implements IManaRefraction {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/mana_power.png");
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
    public void releaseMagick() {
        if(getMana() > 0) {
            List<Entity> livings = findEntity(entity -> entity instanceof PlayerEntity);

            for (Entity living : livings) {
                EntityStateData state = ExtraDataHelper.entityStateData(living);
                state.setMaxManaValue(state.getMaxManaValue() + getMana());
                if (this.world.isRemote) {
                    int age = (int) (this.getDistance(living) * 2);
                    for (int i = 0; i < 5; ++i) {
                        LitParticle par = new LitParticle(this.world, this.spellContext().element.getRenderer().getParticleTexture()
                                , new Vector3d(MagickCore.getNegativeToOne() / 5 + this.getPosX()
                                , MagickCore.getNegativeToOne() / 5 + this.getPosY() + this.getHeight() / 2
                                , MagickCore.getNegativeToOne() / 5 + this.getPosZ())
                                , MagickCore.getNegativeToOne() * 0.1f, MagickCore.getNegativeToOne() * 0.1f, 1.0f, age, this.spellContext().element.getRenderer());
                        par.setGlow();
                        par.setParticleGravity(0);
                        par.setTraceTarget(living);
                        par.addMotion(MagickCore.getNegativeToOne() * 0.005, MagickCore.getNegativeToOne() * 0.005, MagickCore.getNegativeToOne() * 0.005);
                        MagickCore.addMagickParticle(par);
                    }
                }
            }
        }
        else if(this.ticksExisted > 20)
            this.remove();
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    protected void makeSound() {
        if(!this.world.isRemote && this.ticksExisted % 20 == 0)
        {
            this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.0F, 1.0F + this.rand.nextFloat());
        }
    }

    @Override
    protected void applyParticle() {
        if(this.world.isRemote)
        {
            for(int i = 0; i < 2; ++i) {
                LitParticle par = new LitParticle(this.world, this.spellContext().element.getRenderer().getTrailTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() / 5 + this.getPosX()
                        , MagickCore.getNegativeToOne() / 5 + this.getPosY() + this.getHeight() / 2
                        , MagickCore.getNegativeToOne() / 5 + this.getPosZ())
                        , 0.1f, 0.1f, 1.0f, 40, this.spellContext().element.getRenderer());
                par.setGlow();
                par.setParticleGravity(0);
                par.addMotion(MagickCore.getNegativeToOne() * 0.01, MagickCore.getNegativeToOne() * 0.01, MagickCore.getNegativeToOne() * 0.01);
                MagickCore.addMagickParticle(par);
            }
        }
    }

    @Override
    public float getSourceLight() {
        return 3;
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(7), predicate);
    }

    @Override
    public boolean refraction(SpellContext context) {
        return true;
    }
}
