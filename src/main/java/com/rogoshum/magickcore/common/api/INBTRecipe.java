package com.rogoshum.magickcore.common.api;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public interface INBTRecipe {

    public List<IItemContainer> getContainers();

    public abstract ItemStack getResultStack(CraftingInventory inv);

    public boolean matches(CraftingInventory inv);

    public INBTRecipe shapeless();

    INBTRecipe read(JsonObject json);

    INBTRecipe read(PacketBuffer buffer);

    void write(PacketBuffer buffer, INBTRecipe recipe);
}