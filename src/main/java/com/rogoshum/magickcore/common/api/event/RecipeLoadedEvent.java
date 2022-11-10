package com.rogoshum.magickcore.common.api.event;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

import java.util.Map;

public class RecipeLoadedEvent extends Event {
    private final Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> recipes;

    public RecipeLoadedEvent(Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> recipes) {
        this.recipes = recipes;
    }

    public Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> getRecipes() { return recipes; }
}
