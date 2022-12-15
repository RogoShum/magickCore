package com.rogoshum.magickcore.client.integration.jei;

import com.google.common.collect.ImmutableList;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModRecipes;
import com.rogoshum.magickcore.common.recipe.MagickCraftingTransformRecipe;
import com.rogoshum.magickcore.common.recipe.NBTRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class MagickPlugin implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(MagickCore.MOD_ID, "main");

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addCategoryExtension(NBTRecipe.class, ElementItemRecipeWrapper::new);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new MagickItemRecipe(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ImmutableList.Builder<MagickCraftingTransformRecipe> builder = ImmutableList.builder();
        ModRecipes.getExplosionRecipes().values().forEach(builder::add);
        registration.addRecipes(builder.build(), MagickItemRecipe.UID);
    }

    public static List<NBTRecipe> getRecipes() {
        List<NBTRecipe> list = new ArrayList<>();
        for(IRecipe iRecipe : ModRecipes.getRecipes().values()) {
            if(iRecipe instanceof NBTRecipe)
                list.add((NBTRecipe) iRecipe);
        }
        return list;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(ModItems.ELEMENT_CRYSTAL.get());
        registration.useNbtForSubtypes(ModItems.ELEMENT_WOOL.get());
        registration.useNbtForSubtypes(ModItems.ELEMENT_STRING.get());
    }
}