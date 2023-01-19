package com.rogoshum.magickcore.api.event;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Map;

public class RecipeLoadedEvent extends Event {
    private final Map<RecipeType<?>, ImmutableMap.Builder<ResourceLocation, Recipe<?>>> recipes;

    public RecipeLoadedEvent(Map<RecipeType<?>, ImmutableMap.Builder<ResourceLocation, Recipe<?>>> recipes) {
        this.recipes = recipes;
    }

    public Map<RecipeType<?>, ImmutableMap.Builder<ResourceLocation, Recipe<?>>> getRecipes() { return recipes; }
}
