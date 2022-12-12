package com.rogoshum.magickcore.common.magick.condition;

import com.rogoshum.magickcore.api.enums.TargetType;
import com.rogoshum.magickcore.common.lib.LibConditions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;

import java.util.Objects;

public class BlockOnlyCondition extends Condition{
    @Override
    public String getName() {
        return LibConditions.BLOCK_ONLY;
    }

    @Override
    public TargetType getType() {
        return TargetType.TARGET;
    }

    @Override
    public boolean test(Entity entity) {
        return false;
    }

    @Override
    protected void serialize(CompoundNBT tag) {

    }

    @Override
    protected void deserialize(CompoundNBT tag) {

    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}