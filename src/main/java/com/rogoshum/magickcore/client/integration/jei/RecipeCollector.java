package com.rogoshum.magickcore.client.integration.jei;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModRecipes;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import mezz.jei.api.constants.VanillaTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeCollector {
    public static final HashMap<ResourceLocation, List<List<ItemStack>>> RECIPES = new HashMap<>();
    public static final HashMap<ResourceLocation, List<List<ItemStack>>> INPUTS = new HashMap<>();
    public static final HashMap<ResourceLocation, List<ItemStack>> OUTPUTS = new HashMap<>();

    public static void init() {
        ItemStack crystal = new ItemStack(ModItems.ELEMENT_CRYSTAL.get());
        ItemStack orbBottle = new ItemStack(ModItems.ORB_BOTTLE.get());
        ItemStack string = new ItemStack(ModItems.ELEMENT_STRING.get());
        ItemStack wool = new ItemStack(ModItems.ELEMENT_WOOL.get());
        ItemStack seeds = new ItemStack(ModItems.ELEMENT_CRYSTAL_SEEDS.get());
        ItemStack minecraftString = new ItemStack(Items.STRING);
        ItemStack minecraftWool = new ItemStack(Items.WHITE_WOOL);

        List<ItemStack> seedsList = getItemByContainName("seeds");
        //INPUTS.put(ModRecipes.ELEMENT_ORB_RECIPE.getId(), ImmutableList.of(ImmutableList.of(orbBottle)
                //, seedsList));
        //OUTPUTS.put(ModRecipes.ELEMENT_ORB_RECIPE.getId(), ImmutableList.of(seeds));

        ImmutableList.Builder<ItemStack> element = ImmutableList.builder();
        ImmutableList.Builder<ItemStack> other = ImmutableList.builder();
        ImmutableList.Builder<ItemStack> output = ImmutableList.builder();
        for(ItemStack seedsItem : seedsList) {
            if(seedsItem.getItem() == ModItems.ELEMENT_CRYSTAL_SEEDS.get()) continue;
            for (String elementKey : MagickRegistry.getRegistry(LibRegistry.ELEMENT).registry().keySet()) {
                ItemStack itemStack = orbBottle.copy();
                NBTTagHelper.setElement(itemStack, elementKey);
                element.add(itemStack);

                other.add(seedsItem);
                ItemStack itemStack1 = seeds.copy();
                NBTTagHelper.setElement(itemStack1, elementKey);
                output.add(itemStack1);
            }
        }

        /*
        RECIPES.put(ModRecipes.ELEMENT_ORB_RECIPE.getId(), ImmutableList.of(output.build(), element.build(), other.build()));

        INPUTS.put(ModRecipes.ELEMENT_WOOL_RECIPE.getId(), ImmutableList.of(ImmutableList.of(orbBottle)
                , ImmutableList.of(minecraftWool)));
        OUTPUTS.put(ModRecipes.ELEMENT_WOOL_RECIPE.getId(), ImmutableList.of(wool));
        RECIPES.put(ModRecipes.ELEMENT_WOOL_RECIPE.getId(), elementRecipe(orbBottle, minecraftWool, wool));

        INPUTS.put(ModRecipes.ELEMENT_WOOL_RECIPE_2.getId(), ImmutableList.of(ImmutableList.of(crystal)
                , ImmutableList.of(minecraftWool)));
        OUTPUTS.put(ModRecipes.ELEMENT_WOOL_RECIPE_2.getId(), ImmutableList.of(wool));
        RECIPES.put(ModRecipes.ELEMENT_WOOL_RECIPE_2.getId(), elementRecipe(crystal, minecraftWool, wool));

        INPUTS.put(ModRecipes.ELEMENT_STRING_RECIPE.getId(), ImmutableList.of(ImmutableList.of(wool)));
        OUTPUTS.put(ModRecipes.ELEMENT_STRING_RECIPE.getId(), ImmutableList.of(string));
         */

        element = ImmutableList.builder();
        output = ImmutableList.builder();
        for (String elementKey : MagickRegistry.getRegistry(LibRegistry.ELEMENT).registry().keySet()) {
            ItemStack itemStack = wool.copy();
            NBTTagHelper.setElement(itemStack, elementKey);
            element.add(itemStack);
            ItemStack itemStack1 = string.copy();
            NBTTagHelper.setElement(itemStack1, elementKey);
            itemStack1.setCount(4);
            output.add(itemStack1);
        }

        /*
        RECIPES.put(ModRecipes.ELEMENT_STRING_RECIPE.getId(), ImmutableList.of(output.build(), element.build()));

        INPUTS.put(ModRecipes.ELEMENT_STRING_RECIPE_2.getId(), ImmutableList.of(ImmutableList.of(orbBottle)
                , ImmutableList.of(minecraftString)));
        OUTPUTS.put(ModRecipes.ELEMENT_STRING_RECIPE_2.getId(), ImmutableList.of(string));
        RECIPES.put(ModRecipes.ELEMENT_STRING_RECIPE_2.getId(), elementRecipe(orbBottle, minecraftString, string));

        INPUTS.put(ModRecipes.ELEMENT_STRING_RECIPE_3.getId(), ImmutableList.of(ImmutableList.of(crystal)
                , ImmutableList.of(minecraftString)));
        OUTPUTS.put(ModRecipes.ELEMENT_STRING_RECIPE_3.getId(), ImmutableList.of(string));
        RECIPES.put(ModRecipes.ELEMENT_STRING_RECIPE_3.getId(), elementRecipe(crystal, minecraftString, string));

        INPUTS.put(ModRecipes.ELEMENT_STRING_RECIPE_3.getId(), ImmutableList.of(ImmutableList.of(crystal)
                , ImmutableList.of(minecraftString)));
        OUTPUTS.put(ModRecipes.ELEMENT_STRING_RECIPE_3.getId(), ImmutableList.of(string));
        RECIPES.put(ModRecipes.ELEMENT_STRING_RECIPE_3.getId(), elementRecipe(crystal, minecraftString, string));
         */

        ModRecipes.getExplosionRecipes().forEach((resourceLocation, magickCraftingTransformRecipe) -> {
            List<List<ItemStack>> lists = new ArrayList<>();
            lists.add(RecipeCollector.getItemByContainName(magickCraftingTransformRecipe.getContainer().getItemName()));
            INPUTS.put(magickCraftingTransformRecipe.getId(), lists);
            OUTPUTS.put(magickCraftingTransformRecipe.getId(), Lists.newArrayList(magickCraftingTransformRecipe.getOutput()));

            RECIPES.put(magickCraftingTransformRecipe.getId(), ImmutableList.of(ImmutableList.of(magickCraftingTransformRecipe.getOutput()), lists.get(0)));
        });

        addElementCoreRecipe(LibElements.ARC, new ItemStack(ModItems.ARC.get()));
        addElementCoreRecipe(LibElements.SOLAR, new ItemStack(ModItems.SOLAR.get()));
        addElementCoreRecipe(LibElements.VOID, new ItemStack(ModItems.VOID.get()));
        addElementCoreRecipe(LibElements.STASIS, new ItemStack(ModItems.STASIS.get()));
        addElementCoreRecipe(LibElements.WITHER, new ItemStack(ModItems.WITHER.get()));
        addElementCoreRecipe(LibElements.TAKEN, new ItemStack(ModItems.TAKEN.get()));
    }

    public static void addElementCoreRecipe(String element, ItemStack core) {
        ResourceLocation arc = new ResourceLocation(MagickCore.MOD_ID, element+"_element_recipe");
        ItemStack arcCrystal = new ItemStack(ModItems.ELEMENT_CRYSTAL.get());
        NBTTagHelper.setElement(arcCrystal, element);
        INPUTS.put(arc, ImmutableList.of(ImmutableList.of(arcCrystal)));
        OUTPUTS.put(arc, ImmutableList.of(core));
        RECIPES.put(arc, ImmutableList.of(ImmutableList.of(core),
                ImmutableList.of(arcCrystal), ImmutableList.of(arcCrystal),
                ImmutableList.of(arcCrystal), ImmutableList.of(arcCrystal)));
    }

    public static List<List<ItemStack>> elementRecipe(ItemStack elementStack, ItemStack otherStack, ItemStack outputStack) {
        ImmutableList.Builder<ItemStack> element = ImmutableList.builder();
        ImmutableList.Builder<ItemStack> other = ImmutableList.builder();
        ImmutableList.Builder<ItemStack> output = ImmutableList.builder();

        for (String elementKey : MagickRegistry.getRegistry(LibRegistry.ELEMENT).registry().keySet()) {
            ItemStack itemStack = elementStack.copy();
            NBTTagHelper.setElement(itemStack, elementKey);
            element.add(itemStack);

            other.add(otherStack);
            ItemStack itemStack1 = outputStack.copy();
            NBTTagHelper.setElement(itemStack1, elementKey);
            output.add(itemStack1);
        }

        return ImmutableList.of(output.build(), element.build(), other.build());
    }

    public static List<ItemStack> getItemByContainName(String name) {
        ImmutableList.Builder<ItemStack> items = ImmutableList.builder();
        ForgeRegistries.ITEMS.getKeys().forEach(res -> {
            if(res.toString().contains(name)) {
                items.add(new ItemStack(ForgeRegistries.ITEMS.getValue(res)));
            }
        });
        return items.build();
    }
}
