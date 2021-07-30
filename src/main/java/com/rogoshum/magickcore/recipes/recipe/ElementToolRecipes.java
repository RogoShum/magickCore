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
    public static final IItemContainer any = NBTRecipeContainer.ItemContainer.create(":");
    public static final String any_item = ":";

    public static final INBTRecipe elementSwordTag = ElementOnToolContainer.create(any_item, any, string, string, string).shapeless();
    public static final SpecialRecipeSerializer<?> element_any_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementSwordTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_any_recipe;
        }
    }).setRegistryName("element_any_recipe");
}
