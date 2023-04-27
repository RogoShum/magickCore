package com.rogoshum.magickcore.api.magick.condition;

import com.rogoshum.magickcore.api.enums.TargetType;
import com.rogoshum.magickcore.common.lib.LibConditions;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

public class InjurableEntityCondition extends EntityCondition{
    @Override
    public String getName() {
        return LibConditions.INJURABLE;
    }

    @Override
    public TargetType getType() {
        return TargetType.TARGET;
    }

    @Override
    public boolean test(Entity entity) {
        return entity.invulnerableTime <= 0;
    }

    @Override
    protected void serialize(CompoundTag tag) {

    }

    @Override
    protected void deserialize(CompoundTag tag) {

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
