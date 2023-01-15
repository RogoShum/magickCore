package com.rogoshum.magickcore.common.magick.condition;

import com.rogoshum.magickcore.api.enums.TargetType;
import com.rogoshum.magickcore.common.lib.LibConditions;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

public class AlwaysCondition extends Condition<Object> {
    public static final Condition<Object> ALWAYS = new AlwaysCondition();

    @Override
    public String getName() {
        return LibConditions.ALWAYS;
    }

    @Override
    public TargetType getType() {
        return TargetType.ENVIRONMENT;
    }

    @Override
    public boolean test(Object entity) {
        return true;
    }

    @Override
    protected void serialize(CompoundTag tag) {

    }

    @Override
    protected void deserialize(CompoundTag tag) {

    }

    @Override
    public boolean suitable(Object object) {
        return true;
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
