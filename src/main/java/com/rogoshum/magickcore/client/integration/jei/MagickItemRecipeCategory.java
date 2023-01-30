package com.rogoshum.magickcore.client.integration.jei;
/*
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.recipe.MagickWorkbenchRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MagickItemRecipeCategory implements IRecipeCategory<MagickWorkbenchRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(MagickCore.MOD_ID, "mana");
    private final IDrawableStatic background;
    private final IDrawable icon;
    private final String localizedName;

    public MagickItemRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/crafting.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 116, 54);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModItems.SPIRIT_CRYSTAL.get()));
        this.localizedName = new TranslationTextComponent("gui.magickcore.category.magick_crafting").getString();
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends MagickWorkbenchRecipe> getRecipeClass() {
        return MagickWorkbenchRecipe.class;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(MagickWorkbenchRecipe inbtRecipe, IIngredients iIngredients) {
        List<ItemStack> stacks = new ArrayList<>(Arrays.asList(inbtRecipe.getIngredient().getItems()));
        iIngredients.setInputs(VanillaTypes.ITEM, stacks);
        iIngredients.setOutput(VanillaTypes.ITEM, inbtRecipe.getResultItem());
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, MagickWorkbenchRecipe inbtRecipe, IIngredients iIngredients) {
        iRecipeLayout.getItemStacks().init(0, false, 80, 18);
        iRecipeLayout.getItemStacks().init(1, true, 18, 18);
        iRecipeLayout.getItemStacks().set(0, inbtRecipe.getResultItem());
        List<ItemStack> stacks = new ArrayList<>(Arrays.asList(inbtRecipe.getIngredient().getItems()));
        iRecipeLayout.getItemStacks().set(1, stacks);
    }
}
*/