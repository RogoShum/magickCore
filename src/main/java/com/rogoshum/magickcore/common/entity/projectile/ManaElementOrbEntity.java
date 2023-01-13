package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaCapacity;
import com.rogoshum.magickcore.client.entity.easyrender.ElementOrbRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.common.magick.ManaCapacity;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Supplier;

public class ManaElementOrbEntity extends ManaProjectileEntity implements IManaCapacity {
    private final ManaCapacity manaCapacity = ManaCapacity.create(Float.MAX_VALUE);
    private static final DataParameter<Boolean> TYPE = EntityDataManager.defineId(ManaElementOrbEntity.class, DataSerializers.BOOLEAN);
    public ManaElementOrbEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
        this.entityData.define(TYPE, false);
    }

    @Override
    protected void makeSound() {
        if(!this.level.isClientSide && this.tickCount == 1) {
            this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0F, 1.0F - this.random.nextFloat());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaProjectileEntity>> getRenderer() {
        return () -> new ElementOrbRenderer(this);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void normalCollision() {
        List<Entity> list = level.getEntities(this, this.getBoundingBox().inflate(0.5), entity -> entity instanceof LivingEntity && entity.isAlive());
        if(!list.isEmpty()) {
            Entity entity = list.get(0);
            EntityStateData state = ExtraDataUtil.entityStateData(entity);
            if(getOrbType())
                state.setMaxManaValue(state.getMaxManaValue() + manaCapacity.getMana());
            else
                state.setManaValue(state.getManaValue() + manaCapacity.getMana());
            remove();
        }
    }

    public boolean getOrbType() {
        return this.entityData.get(TYPE);
    }

    public void setOrbType(boolean type) {
        this.entityData.set(TYPE, type);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
        manaCapacity().deserialize(compound);
        super.readAdditionalSaveData(compound);
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {
        manaCapacity().serialize(compound);
        super.addAdditionalSaveData(compound);
    }

    @Override
    public boolean releaseMagick() {
        return false;
    }

    @Override
    public boolean hitEntityRemove(EntityRayTraceResult entityRayTraceResult) {
        return true;
    }

    @Override
    public boolean hitBlockRemove(BlockRayTraceResult blockRayTraceResult) {
        return false;
    }

    @Override
    public ManaCapacity manaCapacity() {
        return manaCapacity;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ResourceLocation getEntityIcon() {
        return spellContext().element.getRenderer().getParticleTexture();
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.NON_MANA;
    }

    @Override
    protected void applyParticle() {
        int count = (int) (20 * getBbWidth());
        for (int i = 0; i < count; ++i) {
            LitParticle par = new LitParticle(this.level, MagickCore.proxy.getElementRender(spellContext().element.type()).getMistTexture()
                    , new Vector3d(this.getX() + 0.1 * MagickCore.getNegativeToOne()
                    , this.getY() + 0.1 * MagickCore.getNegativeToOne() + this.getBbHeight() / 2
                    , this.getZ() + 0.1 * MagickCore.getNegativeToOne())
                    , 0.3f * getBbWidth(), 0.3f * getBbWidth(), 1.0f, 2, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0);
            par.setLimitScale();
            par.setShakeLimit(5f);
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    protected float getGravity() {
        return 0.0F;
    }
}
