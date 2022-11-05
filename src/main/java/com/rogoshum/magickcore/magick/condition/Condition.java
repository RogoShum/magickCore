package com.rogoshum.magickcore.magick.condition;

import com.rogoshum.magickcore.enums.TargetType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;

public abstract class Condition {
    private TargetType targetType = TargetType.TARGET;
    private boolean negate;
    public boolean isNegate() {
        return negate;
    }

    public void setNegate() {
        negate = true;
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

    public abstract boolean test(Entity entity);

    protected abstract void serialize(CompoundNBT tag);

    protected abstract void deserialize(CompoundNBT tag);
}
