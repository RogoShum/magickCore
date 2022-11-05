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
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public class ModBrew {

    public static void registryBrewing()
    {
        Ingredient nothing = PotionIngredient.fromStacks(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.NOTHING.get()));
        ItemStack SHIELD_REGEN = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_REGEN_P.get());
        ItemStack SHIELD_VALUE = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_VALUE_P.get());
        ItemStack MANA_REGEN = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_REGEN_P.get());
        ItemStack MANA_CONSUM_REDUCE = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_CONSUM_REDUCE_P.get());

        Registry.ITEM.forEach( item -> {
            String name = item.getRegistryName().toString();
            Ingredient ingredient = Ingredient.fromItems(item);
            if(name.contains("element_crystal"))
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromItems(Items.POTION), Ingredient.fromItems(item), PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.NOTHING.get())));
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
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(SHIELD_REGEN), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_REGEN_P_I.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(SHIELD_VALUE), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_VALUE_P_I.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(MANA_CONSUM_REDUCE), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_CONSUM_REDUCE_P_I.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(MANA_REGEN), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_REGEN_P_I.get())));
            }
            else if(name.contains("glowstone")) {
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(SHIELD_REGEN), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_REGEN_P_II.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(SHIELD_VALUE), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_VALUE_P_II.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(MANA_CONSUM_REDUCE), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_CONSUM_REDUCE_P_II.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(MANA_REGEN), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_REGEN_P_II.get())));
            }
        });
    }

    public static class PotionIngredient extends Ingredient {
        protected PotionIngredient(Stream<? extends IItemList> itemLists) {
            super(itemLists);
        }

        @Override
        public boolean test(@Nullable ItemStack p_test_1_) {
            if(p_test_1_ == null) return false;
            for (ItemStack stack : this.getMatchingStacks()) {
                if(PotionUtils.getPotionFromItem(p_test_1_).getEffects().equals(PotionUtils.getPotionFromItem(stack).getEffects()))
                    return true;
            }
            return false;
        }
    }
}
