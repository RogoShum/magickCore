package com.rogoshum.magickcore.common.recipes.container;

import com.google.gson.JsonObject;
import com.rogoshum.magickcore.common.api.IItemContainer;
import com.rogoshum.magickcore.common.api.INBTRecipe;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class NBTRecipeContainer implements INBTRecipe {
    private boolean shapeless;
    private final List<IItemContainer> containers = new ArrayList<>();

    public NBTRecipeContainer(IItemContainer... containers)
    {
        Collections.addAll(this.containers, containers);
    }

    public List<IItemContainer> getContainers()
    {
        return containers;
    }

    public abstract ItemStack getResultStack(CraftingInventory inv);

    public boolean matches(CraftingInventory inv)
    {
        List<IItemContainer> copy = new ArrayList<>(containers);

        int invSize = 0;
        if(shapeless)
        {
            HashMap<Integer, Integer> matchTable = new HashMap<>();

            for(int i = 0; i < copy.size(); i++)
            {
                IItemContainer copyC = copy.get(i);
                for(int c = 0; c < inv.getSizeInventory(); c++)
                {
                    if(copyC.matches(inv.getStackInSlot(c)) && !matchTable.containsKey(c) && !matchTable.containsValue(i)) {
                        matchTable.put(c, i);
                    }
                    if(i == 0 && !inv.getStackInSlot(c).isEmpty())
                        invSize++;
                }
            }

            return matchTable.size() >= copy.size() && invSize <= copy.size();
        }
        else
        {
            while (copy.size() < inv.getSizeInventory())
            {
                copy.add(ItemMatcher.empty());
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

    public INBTRecipe shapeless() {
        shapeless = true;
        return this;
    }

    public static class ItemMatcher implements IItemContainer {
        protected final String item;
        protected String[] keys = {};

        public static IItemContainer create(String item, String... tagKey)
        {
            return new ItemMatcher(item, tagKey);
        }

        public static IItemContainer create(String item)
        {
            return new ItemMatcher(item);
        }

        public static IItemContainer empty()
        {
            return new ItemMatcher("minecraft:air");
        }

        protected ItemMatcher(String item, String... tagKey) {
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

        public boolean matches(ItemStack stack) {
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

        @Override
        public IItemContainer read(JsonObject json) {
            return null;
        }

        @Override
        public IItemContainer read(PacketBuffer buffer) {
            return null;
        }

        @Override
        public void write(PacketBuffer buffer, IItemContainer recipe) {

        }
    }
}
