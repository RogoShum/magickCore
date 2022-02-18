package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.lib.LibEffect;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import javax.annotation.Nonnull;
import java.util.List;

public class ModBrew {

    public static void registryBrewing()
    {
        Ingredient nothing = Ingredient.fromStacks(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.NOTHING.get()));
        ItemStack SHIELD_REGEN = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_REGEN_P.get());
        ItemStack SHIELD_VALUE = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_VALUE_P.get());
        ItemStack MANA_REGEN = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_REGEN_P.get());
        ItemStack MANA_CONSUM_REDUCE = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_CONSUM_REDUCE_P.get());

        Registry.ITEM.forEach( item -> {
            String name = item.getRegistryName().toString();
            Ingredient ingredient = Ingredient.fromItems(item);
            if(name.contains("element_crystal"))
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.fromItems(Items.POTION), Ingredient.fromItems(item), PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.NOTHING.get())));
            else if(name.contains("disc"))
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(nothing, ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.TRACE_P.get())));
            else if(name.contains("shulker"))
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(nothing, ingredient, SHIELD_REGEN));
            else if(name.contains("scute"))
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(nothing, ingredient, SHIELD_VALUE));
            else if(name.contains("prismarine"))
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(nothing, ingredient, MANA_CONSUM_REDUCE));
            else if(name.contains("nautilus"))
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(nothing, ingredient, MANA_REGEN));
            else if(name.contains("netherite"))
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(nothing, ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_FORCE_P.get())));
            else if(name.contains("redstone")) {
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.fromStacks(SHIELD_REGEN), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_REGEN_P_I.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.fromStacks(SHIELD_VALUE), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_VALUE_P_I.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.fromStacks(MANA_CONSUM_REDUCE), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_CONSUM_REDUCE_P_I.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.fromStacks(MANA_REGEN), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_REGEN_P_I.get())));
            }
            else if(name.contains("glowstone")) {
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.fromStacks(SHIELD_REGEN), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_REGEN_P_II.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.fromStacks(SHIELD_VALUE), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_VALUE_P_II.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.fromStacks(MANA_CONSUM_REDUCE), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_CONSUM_REDUCE_P_II.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.fromStacks(MANA_REGEN), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_REGEN_P_II.get())));
            }
        });
    }

    /*public static class BrewingRecipe implements IBrewingRecipe
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
    }*/
}
