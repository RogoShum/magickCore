package com.rogoshum.magickcore.api.mana;

import net.minecraft.item.ItemStack;

public interface IManaMaterial {
    boolean disappearAfterRead();

    public int getManaNeed(ItemStack stack);

    public boolean upgradeManaItem(ItemStack stack, ISpellContext data);
}
