package com.rogoshum.magickcore.api.magick.context.child;

import com.rogoshum.magickcore.common.lib.LibContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;

public class ItemContext extends ChildContext{
    public static final Type<ItemContext> TYPE = new Type<>(LibContext.ITEM);
    public ItemStack itemStack = ItemStack.EMPTY;

    public static ItemContext create(ItemStack stack) {
        ItemContext context = new ItemContext();
        context.itemStack = stack;
        return context;
    }

    @Override
    public void serialize(CompoundTag tag) {
        itemStack.save(tag);
    }

    @Override
    public void deserialize(CompoundTag tag) {
        itemStack = ItemStack.of(tag);
    }

    @Override
    public boolean valid() {
        return itemStack != null && itemStack != ItemStack.EMPTY;
    }

    @Override
    public Type<? extends ItemContext> getType() {
        return TYPE;
    }

    @Override
    public String getString(int tab) {
        return new TranslatableComponent(itemStack.getDescriptionId()).getString();
    }
}
