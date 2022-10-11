package com.rogoshum.magickcore.magick.condition;

import com.rogoshum.magickcore.enums.EnumTargetType;
import com.rogoshum.magickcore.lib.LibConditions;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;

import java.util.Objects;

public class InjurableEntityCondition extends Condition{
    @Override
    public String getName() {
        return LibConditions.INJURABLE;
    }

    @Override
    public EnumTargetType getType() {
        return EnumTargetType.TARGET;
    }

    @Override
    public boolean test(Entity entity) {
        return entity.hurtResistantTime <= 0;
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
