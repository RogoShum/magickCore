package com.rogoshum.magickcore.magick.condition;

import com.rogoshum.magickcore.enums.EnumTargetType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;

import java.util.function.Predicate;

public abstract class Condition {
    private boolean negate;
    public boolean isNegate() {
        return negate;
    }

    public void setNegate() {
        negate = true;
    }

    public void write(CompoundNBT tag) {
        tag.putBoolean("negate", negate);
        serialize(tag);
    }

    public void read(CompoundNBT tag) {
        negate = tag.getBoolean("negate");
        deserialize(tag);
    }

    public abstract String getName();
    public abstract EnumTargetType getType();
    public abstract boolean test(Entity entity);

    protected abstract void serialize(CompoundNBT tag);

    protected abstract void deserialize(CompoundNBT tag);
}
