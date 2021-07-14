package com.rogoshum.magickcore.recipes.recipe;

import com.rogoshum.magickcore.api.IItemContainer;
import com.rogoshum.magickcore.api.INBTRecipe;
import com.rogoshum.magickcore.recipes.ElementOnToolContainer;
import com.rogoshum.magickcore.recipes.NBTRecipe;
import com.rogoshum.magickcore.recipes.NBTRecipeContainer;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;

public class ElementToolRecipes {
    public static final IItemContainer string = NBTRecipeContainer.ItemContainer.create("element_string", "ELEMENT");
    public static final IItemContainer sword = NBTRecipeContainer.ItemContainer.create("sword");
    public static final IItemContainer axe = NBTRecipeContainer.ItemContainer.create("axe");
    public static final IItemContainer helmet = NBTRecipeContainer.ItemContainer.create("helmet");
    public static final IItemContainer chest = NBTRecipeContainer.ItemContainer.create("chest");
    public static final IItemContainer leg = NBTRecipeContainer.ItemContainer.create("leg");
    public static final IItemContainer boots = NBTRecipeContainer.ItemContainer.create("boots");
    public static final String sword_item = "sword";
    public static final String axe_item = "axe";
    public static final String helmet_item = "helmet";
    public static final String chest_item = "chest";
    public static final String leg_item = "leg";
    public static final String boots_item = "boots";

    public static final INBTRecipe elementSwordTag = ElementOnToolContainer.create(sword_item, sword, string, string, string).shapeless();
    public static final SpecialRecipeSerializer<?> element_sword_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementSwordTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_sword_recipe;
        }
    }).setRegistryName("element_sword_recipe");

    public static final INBTRecipe elementAxeTag = ElementOnToolContainer.create(axe_item, axe, string, string, string).shapeless();
    public static final SpecialRecipeSerializer<?> element_axe_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementAxeTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_axe_recipe;
        }
    }).setRegistryName("element_axe_recipe");

    public static final INBTRecipe elementHelmetTag = ElementOnToolContainer.create(helmet_item, helmet, string, string, string).equip().shapeless();
    public static final SpecialRecipeSerializer<?> element_helmet_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementHelmetTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_helmet_recipe;
        }
    }).setRegistryName("element_helmet_recipe");

    public static final INBTRecipe elementChestTag = ElementOnToolContainer.create(chest_item, chest, string, string, string).equip().shapeless();
    public static final SpecialRecipeSerializer<?> element_chest_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementChestTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_chest_recipe;
        }
    }).setRegistryName("element_chest_recipe");

    public static final INBTRecipe elementLegTag = ElementOnToolContainer.create(leg_item, leg, string, string, string).equip().shapeless();
    public static final SpecialRecipeSerializer<?> element_leg_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementLegTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_leg_recipe;
        }
    }).setRegistryName("element_leg_recipe");

    public static final INBTRecipe elementBootsTag = ElementOnToolContainer.create(boots_item, boots, string, string, string).equip().shapeless();
    public static final SpecialRecipeSerializer<?> element_boots_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementBootsTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_boots_recipe;
        }
    }).setRegistryName("element_boots_recipe");
}
