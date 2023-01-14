package com.rogoshum.magickcore.common.magick;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ManaCapacity {
    protected float capacity;
    private final float maxCapacity;

    public ManaCapacity(float maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public static ManaCapacity create(float maxCapacity) {
        return new ManaCapacity(maxCapacity);
    }

    @Nullable
    public static ManaCapacity create(CompoundTag tag) {
        if(!tag.contains("ManaCapacity")) return null;
        CompoundTag manaCapacity = tag.getCompound("ManaCapacity");
        if(!manaCapacity.contains("MAX_CAPACITY")) return null;
        float maxCapacity = manaCapacity.getFloat("MAX_CAPACITY");
        ManaCapacity capacity = new ManaCapacity(maxCapacity);
        capacity.setMana(tag.getFloat("CAPACITY"));
        return capacity;
    }

    public float getMana() {
        return capacity;
    }

    public void setMana(float mana) {
        capacity = mana;
    }

    public float receiveMana(float mana) {
        float extra = 0;

        if(this.capacity + mana <= maxCapacity)
            this.capacity += mana;
        else {
            extra = this.capacity + mana - maxCapacity;
            this.capacity = maxCapacity;
        }

        return extra;
    }

    public float extractMana(float mana) {
        float extract = Math.min(this.capacity, mana);
        this.capacity -= extract;
        return extract;
    }

    public float getMaxMana() {
        return maxCapacity;
    }

    public CompoundTag serialize(CompoundTag tag) {
        CompoundTag manaData = new CompoundTag();
        manaData.putFloat("CAPACITY", capacity);
        manaData.putFloat("MAX_CAPACITY", maxCapacity);
        tag.put("ManaCapacity", manaData);
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        if(!tag.contains("ManaCapacity")) return;
        tag = tag.getCompound("ManaCapacity");
        if(tag.contains("CAPACITY"))
            this.capacity = tag.getFloat("CAPACITY");
        //if(tag.contains("MAX_CAPACITY"))
        //this.maxCapacity = tag.getFloat("MAX_CAPACITY");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManaCapacity capacity1 = (ManaCapacity) o;
        return Float.compare(capacity1.capacity, capacity) == 0 && Float.compare(capacity1.maxCapacity, maxCapacity) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(capacity, maxCapacity);
    }
}
