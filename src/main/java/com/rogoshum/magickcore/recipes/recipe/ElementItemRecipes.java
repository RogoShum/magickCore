package com.rogoshum.magickcore.recipes.recipe;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IItemContainer;
import com.rogoshum.magickcore.init.ModRecipes;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.recipes.CopyTagContainer;
import com.rogoshum.magickcore.recipes.TagMatchItemContainer;
import com.rogoshum.magickcore.recipes.NBTRecipe;
import com.rogoshum.magickcore.recipes.NBTRecipeContainer;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;

public class ElementItemRecipes {
    public static final String orb_staff = MagickCore.MOD_ID + ":orb_staff";
    public static final String laser_staff = MagickCore.MOD_ID + ":laser_staff";
    public static final String star_staff = MagickCore.MOD_ID + ":star_staff";
    public static final String rune = MagickCore.MOD_ID + ":rune";
    public static final String eye_rune = MagickCore.MOD_ID + ":eye";
    public static final String super_rune = MagickCore.MOD_ID + ":super_spawner";
    public static final String rift_rune = MagickCore.MOD_ID + ":rift";

    public static final String magick_crafting = MagickCore.MOD_ID + ":magick_crafting";
    public static final String magick_container = MagickCore.MOD_ID + ":magick_container";

    public static final String element_crystal = MagickCore.MOD_ID + ":element_crystal";

    public static final IItemContainer crystalContainer = TagMatchItemContainer.create(element_crystal, ModRecipes.getStringTagMap("ELEMENT", LibElements.ORIGIN));
    public static final IItemContainer emptyContainer = NBTRecipeContainer.ItemContainer.create("minecraft:air");

    public static final IItemContainer stringContainer = NBTRecipeContainer.ItemContainer.create("string");
    public static final IItemContainer stickContainer = NBTRecipeContainer.ItemContainer.create("stick");
    public static final IItemContainer quartzContainer = NBTRecipeContainer.ItemContainer.create("quartz");

    public static SpecialRecipeSerializer<?> recipe_0 = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CreateStackContainer.create(magick_crafting,
            crystalContainer, crystalContainer, crystalContainer
            , crystalContainer, emptyContainer, crystalContainer
            , crystalContainer, crystalContainer, crystalContainer), r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return recipe_0;
        }
    }).setRegistryName("magick_crafting_recipe");

    public static SpecialRecipeSerializer<?> recipe_1 = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CreateStackContainer.create(magick_container,
            emptyContainer, crystalContainer, emptyContainer
            , crystalContainer, emptyContainer, crystalContainer
            , emptyContainer, crystalContainer, emptyContainer), r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return recipe_1;
        }
    }).setRegistryName("magick_container_recipe");

    public static SpecialRecipeSerializer<?> recipe_2 = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CreateStackContainer.create(orb_staff,
            stringContainer, crystalContainer, stringContainer
            , emptyContainer, stickContainer, emptyContainer
            , emptyContainer, stickContainer, emptyContainer), r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return recipe_2;
        }
    }).setRegistryName("orb_staff_recipe");

    public static SpecialRecipeSerializer<?> recipe_3 = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CreateStackContainer.create(star_staff,
            stringContainer, crystalContainer, stringContainer
            , crystalContainer, stickContainer, crystalContainer
            , emptyContainer, stickContainer, emptyContainer), r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return recipe_3;
        }
    }).setRegistryName("star_staff_recipe");

    public static SpecialRecipeSerializer<?> recipe_4 = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CreateStackContainer.create(laser_staff,
            emptyContainer, crystalContainer, emptyContainer
            , emptyContainer, stickContainer, emptyContainer
            , emptyContainer, stickContainer, emptyContainer), r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return recipe_4;
        }
    }).setRegistryName("laser_staff_recipe");

    public static SpecialRecipeSerializer<?> recipe_5 = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CreateStackContainer.create(rune,
            emptyContainer, crystalContainer, emptyContainer
            , crystalContainer, crystalContainer, crystalContainer
            , emptyContainer, crystalContainer, emptyContainer), r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return recipe_5;
        }
    }).setRegistryName("rune_recipe");

    public static SpecialRecipeSerializer<?> recipe_6 = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CreateStackContainer.create(super_rune,
            quartzContainer, crystalContainer, quartzContainer
            , crystalContainer, quartzContainer, crystalContainer
            , quartzContainer, crystalContainer, quartzContainer), r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return recipe_6;
        }
    }).setRegistryName("super_rune_recipe");

    public static SpecialRecipeSerializer<?> recipe_7 = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CreateStackContainer.create(eye_rune,
            emptyContainer, quartzContainer, emptyContainer
            , quartzContainer, crystalContainer, quartzContainer
            , emptyContainer, quartzContainer, emptyContainer), r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return recipe_7;
        }
    }).setRegistryName("eye_rune_recipe");

    public static SpecialRecipeSerializer<?> recipe_8 = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CreateStackContainer.create(rift_rune,
            crystalContainer, emptyContainer, quartzContainer
            , emptyContainer, emptyContainer, emptyContainer
            , quartzContainer, emptyContainer, crystalContainer), r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return recipe_8;
        }
    }).setRegistryName("rift_rune_recipe");
}
