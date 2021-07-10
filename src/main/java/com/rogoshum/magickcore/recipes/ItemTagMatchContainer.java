package com.rogoshum.magickcore.recipes;

import com.rogoshum.magickcore.MagickCore;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;

import java.util.HashMap;

public class ItemTagMatchContainer extends NBTRecipeContainer.ItemContainer {
    private final HashMap<String, INBT> map = new HashMap();
    protected ItemTagMatchContainer(String item, HashMap<String, INBT> map) {
        super(item);
        this.map.putAll(map);
    }

    public static ItemTagMatchContainer create(String item, HashMap<String, INBT> map)
    {
        return new ItemTagMatchContainer(item, map);
    }

    @Override
    public boolean matches(ItemStack stack) {
        if(stack.getItem().getRegistryName().toString().contains(item))
        {
            if(stack.hasTag()) {
                for (String key : map.keySet()) {
                    if (!stack.getTag().contains(key) || !stack.getTag().get(key).equals(map.get(key)))
                        return false;
                }
                return true;
            }
            else
                return false;
        }

        return false;
    }
}
