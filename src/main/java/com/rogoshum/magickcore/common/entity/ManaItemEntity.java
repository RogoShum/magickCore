package com.rogoshum.magickcore.common.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ManaItemEntity extends ItemEntity {
    public ManaItemEntity(EntityType<? extends ItemEntity> p_i50217_1_, Level world) {
        super(p_i50217_1_, world);
    }

    public ManaItemEntity(Level worldIn, double x, double y, double z) {
        this(EntityType.ITEM, worldIn);
        this.setPos(x, y, z);
        this.yRot = this.random.nextFloat() * 360.0F;
        this.setDeltaMovement(this.random.nextDouble() * 0.2D - 0.1D, 0.02D, this.random.nextDouble() * 0.2D - 0.1D);
    }

    public ManaItemEntity(Level worldIn, double x, double y, double z, ItemStack stack) {
        this(worldIn, x, y, z);
        this.setItem(stack);
    }

    @Override
    public void tick() {
        setNoGravity(true);
        super.tick();
        setGlowing(true);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(source.getEntity() != null)
            source.getEntity().hurt(source, amount * 0.9f);
        else if(source.getDirectEntity() != null)
            source.getDirectEntity().hurt(source, amount * 0.9f);
        return false;
    }

    @Override
    public boolean isGlowing() {
        return true;
    }

    @Override
    public float getBrightness() {
        return 15f;
    }
}
