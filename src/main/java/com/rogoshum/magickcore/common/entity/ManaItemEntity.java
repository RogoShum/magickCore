package com.rogoshum.magickcore.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class ManaItemEntity extends ItemEntity {
    public ManaItemEntity(EntityType<? extends ItemEntity> p_i50217_1_, World world) {
        super(p_i50217_1_, world);
    }

    public ManaItemEntity(World worldIn, double x, double y, double z) {
        this(EntityType.ITEM, worldIn);
        this.setPos(x, y, z);
        this.yRot = this.random.nextFloat() * 360.0F;
        this.setDeltaMovement(this.random.nextDouble() * 0.2D - 0.1D, 0.02D, this.random.nextDouble() * 0.2D - 0.1D);
    }

    public ManaItemEntity(World worldIn, double x, double y, double z, ItemStack stack) {
        this(worldIn, x, y, z);
        this.setItem(stack);
        this.lifespan = (stack.getItem() == null ? 6000 : stack.getEntityLifespan(worldIn));
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
