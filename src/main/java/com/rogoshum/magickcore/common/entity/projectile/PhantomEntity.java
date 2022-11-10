package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.PhantomRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.common.init.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class PhantomEntity extends ManaProjectileEntity {
    private Entity entity;
    public PhantomEntity(EntityType<? extends PhantomEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        MagickCore.proxy.addRenderer(() -> new PhantomRenderer(this));
    }

    @Override
    public EntityClassification getClassification(boolean forSpawnCount) {
        return ModEntities.PHANTOM.get().getClassification();
    }

    @Override
    public void releaseMagick() {
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return true;
    }

    @Override
    protected float getGravityVelocity() {
        return 0;
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if(entity != null)
            entity.attackEntityFrom(source, amount);
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        if(entity != null)
            entity.processInitialInteract(player, hand);
        return super.processInitialInteract(player, hand);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if(compound.contains("phantom"))
            entity = world.getEntityByID(compound.getInt("phantom"));
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if(entity != null)
            compound.putInt("phantom", entity.getEntityId());
    }

    @Override
    protected void applyParticle() {
        LitParticle litPar = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getMistTexture()
                , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth()*2 + this.getPosX()
                , MagickCore.getNegativeToOne() * this.getWidth()*2 + this.getPosY() + this.getHeight() * 0.5
                , MagickCore.getNegativeToOne() * this.getWidth()*2 + this.getPosZ())
                , 0.15f * this.getWidth(), 0.15f * this.getWidth(), 0.8f, 20, spellContext().element.getRenderer());
        litPar.setGlow();
        litPar.setParticleGravity(0f);
        litPar.setShakeLimit(15.0f);
        litPar.setLimitScale();
        MagickCore.addMagickParticle(litPar);
    }

    @Override
    public void tick() {
        super.tick();
        if(entity == null || !entity.isAlive())
            remove();
    }

    @Override
    public void reSize() {
        if(entity == null) return;
        float height = entity.getHeight();
        if(getHeight() != height)
            this.setHeight(height);
        float width = entity.getWidth();
        if(getWidth() != width)
            this.setWidth(width);
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return null;
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.NON_MANA;
    }

    @Override
    public boolean hitBlockRemove(BlockRayTraceResult blockRayTraceResult) {
        return false;
    }

    @Override
    public boolean hitEntityRemove(EntityRayTraceResult entityRayTraceResult) {
        return false;
    }
}
