package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaCapacity;
import com.rogoshum.magickcore.client.entity.easyrender.ElementOrbRenderer;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.api.magick.ManaFactor;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.api.magick.ManaCapacity;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Supplier;

public class ManaElementOrbEntity extends ManaProjectileEntity implements IManaCapacity {
    private final ManaCapacity manaCapacity = ManaCapacity.create(Float.MAX_VALUE);
    private static final EntityDataAccessor<Boolean> TYPE = SynchedEntityData.defineId(ManaElementOrbEntity.class, EntityDataSerializers.BOOLEAN);
    public ManaElementOrbEntity(EntityType<? extends ThrowableProjectile> type, Level worldIn) {
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
            remove(RemovalReason.DISCARDED);
        }
    }

    public boolean getOrbType() {
        return this.entityData.get(TYPE);
    }

    public void setOrbType(boolean type) {
        this.entityData.set(TYPE, type);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        manaCapacity().deserialize(compound);
        super.readAdditionalSaveData(compound);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        manaCapacity().serialize(compound);
        super.addAdditionalSaveData(compound);
    }

    @Override
    public boolean releaseMagick() {
        return false;
    }

    @Override
    public boolean hitEntityRemove(EntityHitResult entityRayTraceResult) {
        return true;
    }

    @Override
    public boolean hitBlockRemove(BlockHitResult blockRayTraceResult) {
        return false;
    }

    @Override
    public ManaCapacity manaCapacity() {
        return manaCapacity;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ResourceLocation getEntityIcon() {
        return spellContext().element().getRenderer().getParticleTexture();
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.NON_MANA;
    }

    @Override
    protected void applyParticle() {
        int count = (int) (20 * getBbWidth());
        for (int i = 0; i < count; ++i) {
            LitParticle par = new LitParticle(this.level, MagickCore.proxy.getElementRender(spellContext().element().type()).getMistTexture()
                    , new Vec3(this.getX() + 0.1 * MagickCore.getNegativeToOne()
                    , this.getY() + 0.1 * MagickCore.getNegativeToOne() + this.getBbHeight() / 2
                    , this.getZ() + 0.1 * MagickCore.getNegativeToOne())
                    , 0.3f * getBbWidth(), 0.3f * getBbWidth(), 1.0f, 2, MagickCore.proxy.getElementRender(spellContext().element().type()));
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
