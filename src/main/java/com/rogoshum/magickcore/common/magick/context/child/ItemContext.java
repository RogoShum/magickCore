package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.common.lib.LibContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemContext extends ChildContext{
    public ItemStack itemStack = ItemStack.EMPTY;

    public static ItemContext create(ItemStack stack) {
        ItemContext context = new ItemContext();
        context.itemStack = stack;
        return context;
    }

    @Override
    public void serialize(CompoundNBT tag) {
        itemStack.save(tag);
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        itemStack = ItemStack.of(tag);
    }

    @Override
    public boolean valid() {
        return itemStack != null && itemStack != ItemStack.EMPTY;
    }

    @Override
    public String getName() {
        return LibContext.ITEM;
    }

    @Override
    public String getString(int tab) {
        return new TranslationTextComponent(itemStack.getDescriptionId()).getString();
    }
}
