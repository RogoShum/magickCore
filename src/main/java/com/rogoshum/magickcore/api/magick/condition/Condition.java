package com.rogoshum.magickcore.api.magick.condition;

import com.rogoshum.magickcore.api.enums.TargetType;
import net.minecraft.nbt.CompoundTag;

public abstract class Condition<T> {
    private TargetType targetType = TargetType.TARGET;
    private boolean negate;
    public boolean isNegate() {
        return negate;
    }

    public void setNegate() {
        negate = !negate;
    }

    public void write(CompoundTag tag) {
        tag.putBoolean("negate", negate);
        tag.putString("target_type", targetType.name());
        serialize(tag);
    }

    public void read(CompoundTag tag) {
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

    protected abstract void serialize(CompoundTag tag);

    protected abstract void deserialize(CompoundTag tag);

    public abstract boolean suitable(Object object);

    @Override
    public String toString() {
        return "";
    }
}
