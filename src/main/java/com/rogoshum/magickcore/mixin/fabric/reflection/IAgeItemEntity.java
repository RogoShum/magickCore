package com.rogoshum.magickcore.mixin.fabric.reflection;

import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemEntity.class)
public interface IAgeItemEntity {
    @Accessor("pickupDelay")
    int getPickupDelay();
    @Accessor("age")
    int getAge();
    @Accessor("age")
    void setAge(int age);
}
