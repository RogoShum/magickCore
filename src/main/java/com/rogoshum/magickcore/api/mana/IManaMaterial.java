package com.rogoshum.magickcore.api.mana;

import net.minecraft.world.item.ItemStack;

public interface IManaMaterial {
    boolean disappearAfterRead();

    int getManaNeed(ItemStack stack);

    boolean upgradeManaItem(ItemStack stack, ISpellContext data);

    default boolean typeMaterial() {
        return false;
    }

    default boolean elementMaterial() {
        return false;
    }

    default boolean singleMaterial() {
        return false;
    }
}
