package com.rogoshum.magickcore.common.extradata.item;

import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.magick.ManaCapacity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ItemManaCapacity extends ManaCapacity {
    private final ItemStack stack;
    public ItemManaCapacity(ItemStack stack) {
        super(5000);
        this.stack = stack;
    }

    private CompoundTag getDataTag() {
        if(stack.hasTag() && stack.getTag().contains(LibRegistry.ITEM_DATA))
            return stack.getTag().getCompound(LibRegistry.ITEM_DATA);
        CompoundTag tag = new CompoundTag();
        stack.getOrCreateTag().put(LibRegistry.ITEM_DATA, tag);
        return tag;
    }

    public void save() {
        serialize(getDataTag());
    }

    @Override
    public void setMana(float mana) {
        float capacity = this.capacity;
        super.setMana(mana);
        if(capacity != this.capacity)
            save();
    }

    @Override
    public float receiveMana(float mana) {
        float capacity = this.capacity;
        float receive = super.receiveMana(mana);
        if(capacity != this.capacity)
            save();
        return receive;
    }

    @Override
    public float extractMana(float mana) {
        float capacity = this.capacity;
        float extract = super.extractMana(mana);
        if(capacity != this.capacity)
            save();
        return extract;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        save();
    }
}
