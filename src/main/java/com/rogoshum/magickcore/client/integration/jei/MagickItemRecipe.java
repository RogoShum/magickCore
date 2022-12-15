package com.rogoshum.magickcore.client.integration.jei;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.recipe.MagickCraftingTransformRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public class MagickItemRecipe implements IRecipeCategory<MagickCraftingTransformRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(MagickCore.MOD_ID, "mana");
    private final IDrawableStatic background;
    private final IDrawable icon;
    private final String localizedName;

    public MagickItemRecipe(IGuiHelper guiHelper) {
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
    public Class<? extends MagickCraftingTransformRecipe> getRecipeClass() {
        return MagickCraftingTransformRecipe.class;
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
    public void setIngredients(MagickCraftingTransformRecipe inbtRecipe, IIngredients iIngredients) {
        iIngredients.setInputLists(VanillaTypes.ITEM, RecipeCollector.INPUTS.get(inbtRecipe.getId()));
        iIngredients.setOutputs(VanillaTypes.ITEM, RecipeCollector.OUTPUTS.get(inbtRecipe.getId()));
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, MagickCraftingTransformRecipe inbtRecipe, IIngredients iIngredients) {
        if(!RecipeCollector.RECIPES.containsKey(inbtRecipe.getId())) return;
        List<List<ItemStack>> recipes = RecipeCollector.RECIPES.get(inbtRecipe.getId());
        iRecipeLayout.getItemStacks().init(0, false, 80, 18);
        iRecipeLayout.getItemStacks().init(1, true, 18, 18);
        List<ItemStack> list = new ArrayList<>();
        ItemStack output = inbtRecipe.getOutput();
        for (ItemStack itemStack : recipes.get(1)) {
            if(itemStack.getItem() != output.getItem())
                list.add(itemStack);
        }
        iRecipeLayout.getItemStacks().set(0, output);
        iRecipeLayout.getItemStacks().set(1, list);
    }
}
