package com.rogoshum.magickcore.client.integration.jei;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.recipe.MagickWorkbenchRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MagickItemRecipeCategory implements IRecipeCategory<MagickWorkbenchRecipe>{
    private final IDrawableStatic background;
    private final IDrawable icon;
    private final Component localizedName;
    public static final RecipeType<MagickWorkbenchRecipe> RECIPE_TYPE = RecipeType.create(MagickCore.MOD_ID, "mana", MagickWorkbenchRecipe.class);

    public MagickItemRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/crafting.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 116, 54);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.SPIRIT_CRYSTAL.get()));
        this.localizedName = new TranslatableComponent("gui.magickcore.category.magick_crafting");
    }

    @Override
    public RecipeType<MagickWorkbenchRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, MagickWorkbenchRecipe inbtRecipe, IFocusGroup iIngredients) {
        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 80, 18).addItemStack(inbtRecipe.getResultItem());
        List<ItemStack> stacks = new ArrayList<>(Arrays.asList(inbtRecipe.getIngredient().getItems()));
        iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 18, 18).addItemStacks(stacks);
    }

    @Override
    public ResourceLocation getUid() {
        return getRecipeType().getUid();
    }

    @Override
    public Class<? extends MagickWorkbenchRecipe> getRecipeClass() {
        return getRecipeType().getRecipeClass();
    }
}
