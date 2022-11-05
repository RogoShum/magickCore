package com.rogoshum.magickcore.init;

import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraftforge.common.data.ForgeRecipeProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenEvent {
    @SubscribeEvent
    public static void dataGen(GatherDataEvent event) {
        event.getGenerator().addProvider(new Recipes(event.getGenerator()));
    }

    public static class Recipes extends ForgeRecipeProvider {
        public Recipes(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
            ShapedRecipeBuilder shapedRecipeBuilder = ShapedRecipeBuilder.shapedRecipe(ModItems.orb_bottle.get())
                    .patternLine("O O")
                    .patternLine(" O ")
                    .key('O', ModItems.element_crystal.get())
                    .addCriterion("", InventoryChangeTrigger.Instance.forItems(ModItems.orb_bottle.get()));
            shapedRecipeBuilder.build(consumer);
        }
    }
}