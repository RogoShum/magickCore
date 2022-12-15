package com.rogoshum.magickcore.common.magick.condition;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.TargetType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;

public abstract class Condition<T> {
    private TargetType targetType = TargetType.TARGET;
    private boolean negate;
    public boolean isNegate() {
        return negate;
    }

    public void setNegate() {
        negate = !negate;
    }

    public void write(CompoundNBT tag) {
        tag.putBoolean("negate", negate);
        tag.putString("target_type", targetType.name());
        serialize(tag);
    }

    public void read(CompoundNBT tag) {
        negate = tag.getBoolean("negate");
        try {
            targetType = TargetType.valueOf(tag.getString("target_type"));
        } catch (Exception ignored) {}
        deserialize(tag);
    }

    public abstract String getName();
    public TargetType getType() {
        return targetType;
    }

    public void setType(TargetType type) {
        this.targetType = type;
    }

    public abstract boolean test(T entity);

    protected abstract void serialize(CompoundNBT tag);

    protected abstract void deserialize(CompoundNBT tag);

    public abstract boolean suitable(Object object);

    @Override
    public String toString() {
        return "";
    }
}
