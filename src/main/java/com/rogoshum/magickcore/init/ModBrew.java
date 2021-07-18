package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.lib.LibEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import javax.annotation.Nonnull;
import java.util.List;

public class ModBrew {

    public static void registryBrewing()
    {
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe("awkward", "disc", PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.TRACE_P.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe("awkward", "shulker", PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_REGEN_P.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(LibEffect.SHIELD_REGEN, "redstone", PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_REGEN_P_I.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(LibEffect.SHIELD_REGEN, "glowstone", PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_REGEN_P_II.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe("awkward", "scute", PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_VALUE_P.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(LibEffect.SHIELD_VALUE, "redstone", PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_VALUE_P_I.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(LibEffect.SHIELD_VALUE, "glowstone", PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_VALUE_P_II.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe("awkward", "prismarine", PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_CONSUM_REDUCE_P.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(LibEffect.MANA_CONSUM_REDUCE, "redstone", PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_CONSUM_REDUCE_P_I.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(LibEffect.MANA_CONSUM_REDUCE, "glowstone", PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_CONSUM_REDUCE_P_II.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe("awkward", "nautilus", PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_REGEN_P.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(LibEffect.MANA_REGEN, "redstone", PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_REGEN_P_I.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(LibEffect.MANA_REGEN, "glowstone", PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_REGEN_P_II.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe("awkward", "netherite", PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_FORCE_P.get())));
    }

    public static class BrewingRecipe implements IBrewingRecipe
    {
        @Nonnull
        private final String input;
        @Nonnull private final String ingredient;
        @Nonnull private final ItemStack output;

        public BrewingRecipe(String input, String ingredient, ItemStack output)
        {
            this.input = input;
            this.ingredient = ingredient;
            this.output = output;
        }

        @Override
        public boolean isInput(@Nonnull ItemStack stack)
        {
            return isPotionEqual(stack);
        }

        @Override
        public ItemStack getOutput(ItemStack input, ItemStack ingredient)
        {
            return isInput(input) && isIngredient(ingredient) ? getOutput().copy() : ItemStack.EMPTY;
        }

        public ItemStack getOutput()
        {
            return output;
        }

        @Override
        public boolean isIngredient(ItemStack ingredient)
        {
            return ingredient.getItem().getRegistryName().toString().contains(this.ingredient);
        }

        private boolean isPotionEqual(ItemStack other) {

            if (other.getItem() != Items.POTION) {
                return false;
            }

            return other.getTranslationKey().toString().contains(this.input);
        }
    }
}
