package com.rogoshum.magickcore.helper;

import com.rogoshum.magickcore.enums.EnumManaType;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.api.IManaItem;
import com.rogoshum.magickcore.api.IManaLimit;
import net.minecraft.item.ItemStack;

public class ManaItemHelper {

    public static void putDataIn(ItemStack stack, IManaLimit material, IManaElement element, float force, int tick, float mana, float range, boolean trace, EnumManaType manaYype)
    {
        if(stack.getItem() instanceof IManaItem && ((IManaItem) stack.getItem()).getItemData(stack) != null)
        {
            IManaItem item = (IManaItem) stack.getItem();
            item.setMaterial(stack, material);
            item.setElement(stack, element);
            item.setForce(stack, force);
            item.setManaType(stack, manaYype);
            item.setTickTime(stack, tick);
            item.setTrace(stack, trace);
            item.setMana(stack, mana);
            item.setRange(stack, range);
        }
    }

}
