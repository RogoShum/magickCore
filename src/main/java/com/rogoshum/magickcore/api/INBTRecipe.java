package com.rogoshum.magickcore.api;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface INBTRecipe {

    public List<IItemContainer> getContainers();

    public abstract ItemStack getResultStack(CraftingContainer inv);

    public boolean matches(CraftingContainer inv);

    public INBTRecipe shapeless();

    INBTRecipe read(JsonObject json);

    INBTRecipe read(FriendlyByteBuf buffer);

    void write(FriendlyByteBuf buffer, INBTRecipe recipe);
}