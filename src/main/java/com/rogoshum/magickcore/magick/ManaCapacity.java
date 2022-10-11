package com.rogoshum.magickcore.magick;

import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.registry.MagickRegistry;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.Objects;

public class ManaCapacity {
    private float capacity;
    private final float maxCapacity;

    public ManaCapacity(float maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public static ManaCapacity create(float maxCapacity) {
        return new ManaCapacity(maxCapacity);
    }

    @Nullable
    public static ManaCapacity create(CompoundNBT tag) {
        if(!tag.contains("ManaCapacity")) return null;
        CompoundNBT manaCapacity = tag.getCompound("ManaCapacity");
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

    public CompoundNBT serialize(CompoundNBT tag) {
        CompoundNBT manaData = new CompoundNBT();
        manaData.putFloat("CAPACITY", capacity);
        manaData.putFloat("MAX_CAPACITY", maxCapacity);
        tag.put("ManaCapacity", manaData);
        return tag;
    }

    public void deserialize(CompoundNBT tag) {
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
