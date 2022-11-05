package com.rogoshum.magickcore.event;

import com.google.common.collect.ImmutableMap;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.event.ExtraDataEvent;
import com.rogoshum.magickcore.api.event.RecipeLoadedEvent;
import com.rogoshum.magickcore.init.*;
import com.rogoshum.magickcore.lib.LibEntityData;
import com.rogoshum.magickcore.lib.LibRegistry;
import com.rogoshum.magickcore.magick.extradata.entity.ElementToolData;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.magick.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.magick.extradata.item.ItemManaData;
import com.rogoshum.magickcore.recipes.ManaItemMaterialRecipe;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Map;

public class RegisterEvent {

    @SubscribeEvent
    public void entityExtraData(ExtraDataEvent.Entity event) {
        event.add(LibEntityData.ENTITY_STATE, EntityStateData::new);
        event.add(LibEntityData.ELEMENT_TOOL, ElementToolData::new);
        event.add(LibEntityData.TAKEN_ENTITY, TakenEntityData::new);
    }

    @SubscribeEvent
    public void itemExtraData(ExtraDataEvent.ItemStack event) {
        event.add(LibRegistry.ITEM_DATA, ItemManaData::new);
    }

    @SubscribeEvent
    public void tradeEvent(VillagerTradesEvent event) {
        if(event.getType() == ModVillager.MAGE.get()) {
            for (int i = 1; i < 6; i++) {
                ArrayList<VillagerTrades.ITrade> list = new ArrayList<>();
                for (int c = 0; c < i; ++c) {
                    list.add(new ModVillager.EntityTypeTrade());
                }
                event.getTrades().put(i, list);
            }
        }
    }

    @SubscribeEvent
    public void onBiomesLoad(BiomeLoadingEvent event) {
        if(event.getClimate().temperature > 0.5 && event.getClimate().temperature < 1.3) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(ModEntities.MAGE.get(), 10, 1, 1));
        }
        event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD,
                ModBlocks.SPIRIT_ORE.get().getDefaultState(),
                6)
        ).range(64).square().func_242731_b(20));
    }

    @SubscribeEvent
    public void onAddReload(RecipeLoadedEvent event) {
        Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> recipes = event.getRecipes();
        NonNullList<Ingredient> nonNullList = NonNullList.from(Ingredient.EMPTY, Ingredient.fromItems(ModItems.spirit_wood_stick.get()), Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.fromItems(ModItems.spirit_wood_stick.get()));
        ShapedRecipe shapedRecipe = new ShapedRecipe(new ResourceLocation(MagickCore.MOD_ID, "spirit_stick"), "", 2, 2, nonNullList, new ItemStack(ModItems.wand.get()));
        ImmutableMap.Builder<ResourceLocation, IRecipe<?>> function = recipes.computeIfAbsent(IRecipeType.CRAFTING, (recipeType) -> {
                    return ImmutableMap.builder();
                });
        function.put(shapedRecipe.getId(), shapedRecipe);
        ManaItemMaterialRecipe recipe = new ManaItemMaterialRecipe(new ResourceLocation(MagickCore.MOD_ID, "mana_item_material_recipe"));
        function.put(recipe.getId(), recipe);
    }
}
