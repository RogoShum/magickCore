package com.rogoshum.magickcore.recipes;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.INBTRecipe;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class NBTRecipeContainer implements INBTRecipe {
    private boolean shapeless;
    private final List<ItemContainer> containers = new ArrayList<>();

    public NBTRecipeContainer(ItemContainer... containers)
    {
        Collections.addAll(this.containers, containers);
    }

    public List<ItemContainer> getContainers()
    {
        return containers;
    }

    public abstract ItemStack getResultStack(CraftingInventory inv);

    public boolean matches(CraftingInventory inv)
    {
        List<ItemContainer> copy = new ArrayList<>();
        copy.addAll(containers);

        int invSize = 0;
        if(shapeless)
        {
            HashMap<Integer, Integer> matchTable = new HashMap<>();

            for(int i = 0; i < copy.size(); i++)
            {
                ItemContainer copyC = copy.get(i);
                for(int c = 0; c < inv.getSizeInventory(); c++)
                {
                    if(copyC.matches(inv.getStackInSlot(c)) && !matchTable.containsKey(c) && !matchTable.containsValue(i)) {
                        matchTable.put(c, i);
                    }
                    if(i == 0 && !inv.getStackInSlot(c).isEmpty())
                        invSize++;
                }
            }

            if(matchTable.size() < copy.size() || invSize > copy.size())
                return false;

            return true;
        }
        else
        {
            while (copy.size() < inv.getSizeInventory())
            {
                copy.add(ItemContainer.empty());
            }

            if(inv.getSizeInventory() < copy.size())
                return false;

            for(int i = 0; i < inv.getSizeInventory(); i++)
            {
                if(!copy.get(i).matches(inv.getStackInSlot(i)))
                    return false;
            }

            return true;
        }
    }

    public INBTRecipe shapeless()
    {
        shapeless = true;
        return this;
    }

    public static class ItemContainer {
        protected final String item;
        protected String[] keys = {};

        public static ItemContainer create(String item, String... tagKey)
        {
            return new ItemContainer(item, tagKey);
        }

        public static ItemContainer create(String item)
        {
            return new ItemContainer(item);
        }

        public static ItemContainer empty()
        {
            return new ItemContainer("minecraft:air");
        }

        protected ItemContainer(String item, String... tagKey)
        {
            this.item = item;
            this.keys = tagKey;
        }

        public Item getItem() {
            return Registry.ITEM.getOrDefault(new ResourceLocation(this.item));
        }

        public boolean hasKey()
        {
            return keys.length > 0;
        }

        public String[] getKeys() {
            return keys;
        }

        public boolean matches(ItemStack stack)
        {
            if(stack.getItem().getRegistryName().toString().contains(item))
            {
                if(hasKey())
                {
                    if(stack.hasTag()) {
                        for (String key : keys) {
                            if (!stack.getTag().contains(key))
                                return false;
                        }
                        return true;
                    }
                    else
                        return false;
                }
                else
                    return true;
            }

            return false;
        }
    }
}
