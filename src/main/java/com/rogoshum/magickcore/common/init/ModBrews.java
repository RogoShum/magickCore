package com.rogoshum.magickcore.common.init;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class ModBrews {

    public static void registryBrewing() {
        Ingredient nothing = PotionIngredient.fromStacks(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.NOTHING.get()));
        ItemStack SHIELD_REGEN = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_REGEN_P.get());
        ItemStack SHIELD_VALUE = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_VALUE_P.get());
        ItemStack MANA_REGEN = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_REGEN_P.get());
        ItemStack MANA_CONSUME_REDUCE = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_CONSUM_REDUCE_P.get());
        ItemStack MANA_FORCE = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_FORCE_P.get());
        ItemStack MANA_TICK = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_TICK_P.get());
        ItemStack MANA_RANGE = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_RANGE_P.get());
        //ItemStack MANA_MULTI_CAST = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MULTI_RELEASE_P.get());
        ItemStack MANA_CHAOS = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.CHAOS_THEOREM_P.get());
        ItemStack MANA_CONVERT = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_CONVERT_P.get());

        ForgeRegistries.ITEMS.forEach(item -> {
            String name = item.getRegistryName().toString();
            Ingredient ingredient = Ingredient.fromItems(item);
            if(name.contains("spirit_crystal"))
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromItems(Items.POTION), Ingredient.fromItems(item), PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.NOTHING.get())));
            else if(name.contains("dragon_breath"))
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(nothing, ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.TRACE_P.get())));
            else if(name.contains("shulker"))
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(nothing, ingredient, SHIELD_REGEN));
            else if(name.contains("scute"))
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(nothing, ingredient, SHIELD_VALUE));
            else if(name.contains("golden_carrot"))
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(nothing, ingredient, MANA_CONSUME_REDUCE));
            else if(name.contains("nautilus"))
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(nothing, ingredient, MANA_REGEN));
            else if(name.contains("netherite"))
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(nothing, ingredient, MANA_FORCE));
            else if(name.contains("rabbit_foot"))
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(nothing, ingredient, MANA_RANGE));
            else if(name.contains("sugar"))
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(nothing, ingredient, MANA_TICK));
            //else if(name.contains("pufferfish"))
                //BrewingRecipeRegistry.addRecipe(new BrewingRecipe(nothing, ingredient, MANA_MULTI_CAST));
            else if(name.contains("phantom_membrane"))
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(nothing, ingredient, MANA_CHAOS));
            else if(name.contains("blaze_powder"))
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(nothing, ingredient, MANA_CONVERT));
            else if(name.contains("redstone")) {
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(SHIELD_REGEN), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_REGEN_P_I.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(SHIELD_VALUE), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_VALUE_P_I.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(MANA_CONSUME_REDUCE), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_CONSUM_REDUCE_P_I.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(MANA_REGEN), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_REGEN_P_I.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(MANA_FORCE), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_FORCE_P_I.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(MANA_RANGE), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_RANGE_P_I.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(MANA_TICK), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_TICK_P_I.get())));
            }
            else if(name.contains("glowstone")) {
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(SHIELD_REGEN), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_REGEN_P_II.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(SHIELD_VALUE), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.SHIELD_VALUE_P_II.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(MANA_CONSUME_REDUCE), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_CONSUM_REDUCE_P_II.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(MANA_REGEN), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_REGEN_P_II.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(MANA_FORCE), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_FORCE_P_II.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(MANA_RANGE), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_RANGE_P_II.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(MANA_TICK), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_TICK_P_II.get())));
                //BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(MANA_MULTI_CAST), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MULTI_RELEASE_P_I.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionIngredient.fromStacks(MANA_CONVERT), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MANA_CONVERT_P_I.get())));
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
