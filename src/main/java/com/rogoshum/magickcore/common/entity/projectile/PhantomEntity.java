package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.PhantomRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.RayRenderer;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class PhantomEntity extends ManaProjectileEntity {
    private Entity entity;
    public PhantomEntity(EntityType<? extends PhantomEntity> type, Level worldIn) {
        super(type, worldIn);
    }


    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaProjectileEntity>> getRenderer() {
        return () -> new PhantomRenderer(this);
    }

    @Override
    public EntityClassification getClassification(boolean forSpawnCount) {
        return ModEntities.PHANTOM.get().getCategory();
    }

    @Override
    public boolean releaseMagick() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    protected float getGravity() {
        return 0;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(entity != null)
            entity.hurt(source, amount);
        return super.hurt(source, amount);
    }

    @Override
    public ActionResultType interact(PlayerEntity player, Hand hand) {
        if(entity != null)
            entity.interact(player, hand);
        return super.interact(player, hand);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        if(compound.contains("phantom"))
            entity = level.getEntity(compound.getInt("phantom"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        if(entity != null)
            compound.putInt("phantom", entity.getId());
    }

    @Override
    protected void applyParticle() {
        LitParticle litPar = new LitParticle(this.level, MagickCore.proxy.getElementRender(spellContext().element.type()).getMistTexture()
                , new Vector3d(MagickCore.getNegativeToOne() * this.getBbWidth()*2 + this.getX()
                , MagickCore.getNegativeToOne() * this.getBbWidth()*2 + this.getY() + this.getBbHeight() * 0.5
                , MagickCore.getNegativeToOne() * this.getBbWidth()*2 + this.getZ())
                , 0.15f * this.getBbWidth(), 0.15f * this.getBbWidth(), 0.8f, 20, spellContext().element.getRenderer());
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
        float height = entity.getBbHeight();
        if(getBbHeight() != height)
            this.setHeight(height);
        float width = entity.getBbWidth();
        if(getBbWidth() != width)
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
