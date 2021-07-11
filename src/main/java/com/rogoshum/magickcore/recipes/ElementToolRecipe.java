package com.rogoshum.magickcore.recipes;

import com.rogoshum.magickcore.api.INBTRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;

public class ElementToolRecipe{
    public static final NBTRecipeContainer.ItemContainer string = NBTRecipeContainer.ItemContainer.create("element_string", "ELEMENT");
    public static final NBTRecipeContainer.ItemContainer sword = NBTRecipeContainer.ItemContainer.create("sword");
    public static final NBTRecipeContainer.ItemContainer axe = NBTRecipeContainer.ItemContainer.create("axe");
    public static final NBTRecipeContainer.ItemContainer helmet = NBTRecipeContainer.ItemContainer.create("helmet");
    public static final NBTRecipeContainer.ItemContainer chest = NBTRecipeContainer.ItemContainer.create("chest");
    public static final NBTRecipeContainer.ItemContainer leg = NBTRecipeContainer.ItemContainer.create("leg");
    public static final NBTRecipeContainer.ItemContainer boots = NBTRecipeContainer.ItemContainer.create("boots");
    public static final String sword_item = "sword";
    public static final String axe_item = "axe";
    public static final String helmet_item = "helmet";
    public static final String chest_item = "chest";
    public static final String leg_item = "leg";
    public static final String boots_item = "boots";

    public static final INBTRecipe elementSwordTag = ElementOnToolRecipe.create(sword_item, sword, string, string, string).shapeless();
    public static final SpecialRecipeSerializer<?> element_sword_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementSwordTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_sword_recipe;
        }
    }).setRegistryName("element_sword_recipe");

    public static final INBTRecipe elementAxeTag = ElementOnToolRecipe.create(axe_item, axe, string, string, string).shapeless();
    public static final SpecialRecipeSerializer<?> element_axe_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementAxeTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_axe_recipe;
        }
    }).setRegistryName("element_axe_recipe");

    public static final INBTRecipe elementHelmetTag = ElementOnToolRecipe.create(helmet_item, helmet, string, string, string).equip().shapeless();
    public static final SpecialRecipeSerializer<?> element_helmet_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementHelmetTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_helmet_recipe;
        }
    }).setRegistryName("element_helmet_recipe");

    public static final INBTRecipe elementChestTag = ElementOnToolRecipe.create(chest_item, chest, string, string, string).equip().shapeless();
    public static final SpecialRecipeSerializer<?> element_chest_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementChestTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_chest_recipe;
        }
    }).setRegistryName("element_chest_recipe");

    public static final INBTRecipe elementLegTag = ElementOnToolRecipe.create(leg_item, leg, string, string, string).equip().shapeless();
    public static final SpecialRecipeSerializer<?> element_leg_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementLegTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_leg_recipe;
        }
    }).setRegistryName("element_leg_recipe");

    public static final INBTRecipe elementBootsTag = ElementOnToolRecipe.create(boots_item, boots, string, string, string).equip().shapeless();
    public static final SpecialRecipeSerializer<?> element_boots_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementBootsTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_boots_recipe;
        }
    }).setRegistryName("element_boots_recipe");
}
