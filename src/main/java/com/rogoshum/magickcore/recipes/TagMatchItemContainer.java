package com.rogoshum.magickcore.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;

import java.util.HashMap;

public class TagMatchItemContainer extends NBTRecipeContainer.ItemContainer {
    private final HashMap<String, INBT> map = new HashMap();
    protected TagMatchItemContainer(String item, HashMap<String, INBT> map) {
        super(item);
        this.map.putAll(map);
    }

    public static TagMatchItemContainer create(String item, HashMap<String, INBT> map)
    {
        return new TagMatchItemContainer(item, map);
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
            else return map.isEmpty();
        }

        return false;
    }

    @Override
    public String toString() {
        return "ItemTagMatchContainer{ " + this.item +
                " tag= " + map +
                '}';
    }
}
