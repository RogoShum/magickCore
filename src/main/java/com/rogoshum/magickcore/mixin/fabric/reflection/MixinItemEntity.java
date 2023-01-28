package com.rogoshum.magickcore.mixin.fabric.reflection;

import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemEntity.class)
public class MixinItemEntity implements IAgeItemEntity{
    @Shadow private int pickupDelay;

    @Shadow private int age;

    @Override
    public int getPickupDelay() {
        return this.pickupDelay;
    }

    @Override
    public int getAge() {
        return this.age;
    }

    @Override
    public void setAge(int age) {
        this.age = age;
    }
}
