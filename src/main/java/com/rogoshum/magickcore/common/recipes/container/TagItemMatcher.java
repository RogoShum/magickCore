package com.rogoshum.magickcore.common.recipes.container;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;

import java.util.HashMap;

public class TagItemMatcher extends NBTRecipeContainer.ItemMatcher {
    private final HashMap<String, INBT> map = new HashMap<>();
    protected TagItemMatcher(String item, HashMap<String, INBT> map) {
        super(item);
        this.map.putAll(map);
    }

    protected TagItemMatcher(String item) {
        super(item);
    }

    public static TagItemMatcher create(String item, HashMap<String, INBT> map) {
        return new TagItemMatcher(item, map);
    }

    public static TagItemMatcher create(String item)
    {
        return new TagItemMatcher(item);
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
