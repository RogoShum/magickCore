package com.rogoshum.magickcore.common.magick.condition;

import com.rogoshum.magickcore.api.enums.TargetType;
import com.rogoshum.magickcore.common.lib.LibConditions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.nbt.CompoundTag;

public class DistanceCondition extends EntityCondition{
    private double distance;

    @Override
    public String getName() {
        return LibConditions.DISTANCE;
    }

    public DistanceCondition distance(double distance) {
        this.distance = distance;
        return this;
    }

    @Override
    public boolean test(Entity entity) {
        if(!(entity instanceof LivingEntity)) return false;
        if(!((LivingEntity) entity).getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent())
            return false;
        LivingEntity target = ((LivingEntity) entity).getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
        return entity.distanceToSqr(target) <= distance * distance;
    }

    @Override
    public TargetType getType() {
        return TargetType.SELF;
    }

    @Override
    protected void serialize(CompoundTag tag) {
        tag.putDouble("distance", distance);
    }

    @Override
    protected void deserialize(CompoundTag tag) {
        distance = tag.getDouble("distance");
    }
}
