package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.recipe.*;
import com.rogoshum.magickcore.common.event.magickevent.LivingLootsEvent;
import net.minecraft.item.crafting.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import com.rogoshum.magickcore.common.event.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = MagickCore.MOD_ID)
public class ModRecipes {
    //Explosion Recipes(easy recipe)
    private static final HashMap<String, MagickCraftingTransformRecipe> ExplosionRecipesMap = new HashMap<>();

    public static HashMap<String, MagickCraftingTransformRecipe> getExplosionRecipes() {
        return ExplosionRecipesMap;
    }

    //Event registry MagickLogicEvent::onExplosion

    //////////////////////////////
    public static final RecipeSerializer<?> NBT_RECIPE = NBTRecipe.Serializer.INSTANCE;
    public static final RecipeSerializer<?> MAGICK_WORKBENCH_RECIPE = MagickWorkbenchRecipe.Serializer.INSTANCE;
    public static final RecipeSerializer<?> SPIRIT_CRAFTING_RECIPE = SpiritCraftingRecipe.Serializer.INSTANCE;
    public static final ManaItemContextRecipe MANA_ITEM_CONTEXT_RECIPE = new ManaItemContextRecipe(new ResourceLocation("context_tool_recipe"));
    public static final BlockConditionRecipe BLOCK_CONDITION_RECIPE = new BlockConditionRecipe(new ResourceLocation("block_condition_recipe"));
    public static final ManaItemMaterialRecipe MANA_ITEM_MATERIAL_RECIPE = new ManaItemMaterialRecipe(new ResourceLocation("mana_item_material_recipe"));
    public static final ElementToolRecipe ELEMENT_TOOL_RECIPE = new ElementToolRecipe(new ResourceLocation("element_tool_recipe"));

    public static void init() {
        LivingLootsEvent.init();
    }

    @SubscribeEvent
    public static void registerRecipes(final RegistryEvent.Register<RecipeSerializer<?>> event) {
        init();
        event.getRegistry().register(NBT_RECIPE);
        event.getRegistry().register(MAGICK_WORKBENCH_RECIPE);
        event.getRegistry().register(SPIRIT_CRAFTING_RECIPE);
        event.getRegistry().register(MANA_ITEM_CONTEXT_RECIPE.getSerializer());
        event.getRegistry().register(BLOCK_CONDITION_RECIPE.getSerializer());
        event.getRegistry().register(MANA_ITEM_MATERIAL_RECIPE.getSerializer());
        event.getRegistry().register(ELEMENT_TOOL_RECIPE.getSerializer());
        CraftingHelper.register(new ResourceLocation(MagickCore.MOD_ID, "nbt"), NBTIngredient.Serializer.INSTANCE);
    }
}
