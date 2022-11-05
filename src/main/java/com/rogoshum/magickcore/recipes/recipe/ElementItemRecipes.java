package com.rogoshum.magickcore.recipes.recipe;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IItemContainer;
import com.rogoshum.magickcore.init.ModRecipes;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.recipes.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;

public class ElementItemRecipes {
    public static final String orb_staff = MagickCore.MOD_ID + ":orb_staff";
    public static final String laser_staff = MagickCore.MOD_ID + ":laser_staff";
    public static final String star_staff = MagickCore.MOD_ID + ":star_staff";
    public static final String super_rune = MagickCore.MOD_ID + ":super_spawner";
    public static final String rift_rune = MagickCore.MOD_ID + ":rift";
    public static final String orb_bottle = MagickCore.MOD_ID + ":orb_bottle";

    public static final String magick_crafting = MagickCore.MOD_ID + ":magick_crafting";
    public static final String magick_container = MagickCore.MOD_ID + ":magick_container";

    public static final String element_crystal = MagickCore.MOD_ID + ":element_crystal";

    public static final IItemContainer crystalContainer = TagMatchItemContainer.create(element_crystal, ModRecipes.getStringTagMap("ELEMENT", LibElements.ORIGIN));
    public static final IItemContainer emptyContainer = NBTRecipeContainer.ItemContainer.create("minecraft:air");

    public static final IItemContainer stringContainer = NBTRecipeContainer.ItemContainer.create("string");
    public static final IItemContainer stickContainer = NBTRecipeContainer.ItemContainer.create("stick");
    public static final IItemContainer quartzContainer = NBTRecipeContainer.ItemContainer.create("quartz");

    public static SpecialRecipeSerializer<?> recipe_6 = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CreateStackContainer.create(super_rune,
            quartzContainer, crystalContainer, quartzContainer
            , crystalContainer, quartzContainer, crystalContainer
            , quartzContainer, crystalContainer, quartzContainer), r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return recipe_6;
        }
    }).setRegistryName("super_rune_recipe");

    public static SpecialRecipeSerializer<?> recipe_9 = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CreateStackContainer.create(orb_bottle,
            emptyContainer, emptyContainer, emptyContainer
            , crystalContainer, emptyContainer, crystalContainer
            , emptyContainer, crystalContainer, emptyContainer), r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return recipe_9;
        }
    }).setRegistryName("orb_bottle_recipe");
}
