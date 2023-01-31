package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.recipe.*;
import com.rogoshum.magickcore.common.event.magickevent.LivingLootsEvent;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import com.rogoshum.magickcore.common.event.SubscribeEvent;

import java.util.HashMap;
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
    public static final ManaItemContextRecipe MANA_ITEM_CONTEXT_RECIPE = new ManaItemContextRecipe(MagickCore.fromId("context_tool_recipe"));
    public static final BlockConditionRecipe BLOCK_CONDITION_RECIPE = new BlockConditionRecipe(MagickCore.fromId("block_condition_recipe"));
    public static final ManaItemMaterialRecipe MANA_ITEM_MATERIAL_RECIPE = new ManaItemMaterialRecipe(MagickCore.fromId("mana_item_material_recipe"));
    public static final ElementToolRecipe ELEMENT_TOOL_RECIPE = new ElementToolRecipe(MagickCore.fromId("element_tool_recipe"));

    public static void init() {
        LivingLootsEvent.init();
        registerRecipes();
    }

    @SubscribeEvent
    public static void registerRecipes() {
        init();
        Registry.register(Registry.RECIPE_SERIALIZER, NBTRecipe.Serializer.NAME,
                NBT_RECIPE);
        Registry.register(Registry.RECIPE_SERIALIZER, MagickWorkbenchRecipe.Serializer.NAME,
                MAGICK_WORKBENCH_RECIPE);
        Registry.register(Registry.RECIPE_SERIALIZER, SpiritCraftingRecipe.Serializer.NAME,
                SPIRIT_CRAFTING_RECIPE);

        Registry.register(Registry.RECIPE_SERIALIZER, MANA_ITEM_CONTEXT_RECIPE.getId(),
                MANA_ITEM_CONTEXT_RECIPE.getSerializer());
        Registry.register(Registry.RECIPE_SERIALIZER, SpiritCraftingRecipe.Serializer.NAME,
                BLOCK_CONDITION_RECIPE.getSerializer());
        Registry.register(Registry.RECIPE_SERIALIZER, SpiritCraftingRecipe.Serializer.NAME,
                MANA_ITEM_MATERIAL_RECIPE.getSerializer());
        Registry.register(Registry.RECIPE_SERIALIZER, SpiritCraftingRecipe.Serializer.NAME,
                ELEMENT_TOOL_RECIPE.getSerializer());
        //CraftingHelper.register(new ResourceLocation(MagickCore.MOD_ID, "nbt"), NBTIngredient.Serializer.INSTANCE);
    }
}
