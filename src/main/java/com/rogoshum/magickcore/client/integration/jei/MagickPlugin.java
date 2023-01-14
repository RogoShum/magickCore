package com.rogoshum.magickcore.client.integration.jei;

import com.google.common.collect.ImmutableList;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.recipe.MagickWorkbenchRecipe;
import com.rogoshum.magickcore.common.recipe.NBTRecipe;
import com.rogoshum.magickcore.common.recipe.SpiritCraftingRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

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
        registration.addRecipeCategories(new MagickItemRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SpiritCraftingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ImmutableList.Builder<MagickWorkbenchRecipe> magickWorkbenchRecipeBuilder = ImmutableList.builder();
        Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(MagickWorkbenchRecipe.MAGICK_WORKBENCH).forEach(
                magickWorkbenchRecipeBuilder::add
        );
        registration.addRecipes(magickWorkbenchRecipeBuilder.build(), MagickItemRecipeCategory.UID);
        ImmutableList.Builder<SpiritCraftingRecipe> spiritCraftingRecipeBuilder = ImmutableList.builder();
        Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(SpiritCraftingRecipe.SPIRIT_CRAFTING).forEach(
                spiritCraftingRecipeBuilder::add
        );
        registration.addRecipes(spiritCraftingRecipeBuilder.build(), SpiritCraftingRecipeCategory.UID);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(ModItems.ELEMENT_CRYSTAL.get());
        registration.useNbtForSubtypes(ModItems.ELEMENT_WOOL.get());
        registration.useNbtForSubtypes(ModItems.ELEMENT_STRING.get());
    }
}
