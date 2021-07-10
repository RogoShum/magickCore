package com.rogoshum.magickcore.recipes;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;

public class ElementToolRecipe{
    public static final NBTRecipeContainer.ItemContainer string = NBTRecipeContainer.ItemContainer.create("element_string", "ELEMENT");
    public static final NBTRecipeContainer.ItemContainer sword = NBTRecipeContainer.ItemContainer.create("sword");
    public static final NBTRecipeContainer.ItemContainer helmet = NBTRecipeContainer.ItemContainer.create("helmet");
    public static final String sword_item = "sword";
    public static final String helmet_item = "helmet";

    public static final ElementOnToolRecipe elementSwordTag = (ElementOnToolRecipe) ElementOnToolRecipe.create(sword_item, sword, string, string, string).shapeless();

    public static final SpecialRecipeSerializer<?> element_sword_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementSwordTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_sword_recipe;
        }
    }).setRegistryName("element_sword_recipe");

    public static final ElementOnToolRecipe elementHelmetTag = (ElementOnToolRecipe) ElementOnToolRecipe.create(helmet_item, helmet, string, string, string).shapeless();

    public static final SpecialRecipeSerializer<?> element_helmet_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementHelmetTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_helmet_recipe;
        }
    }).setRegistryName("element_helmet_recipe");
}
