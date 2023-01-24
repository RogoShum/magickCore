package com.rogoshum.magickcore.client.integration.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.recipe.SpiritCraftingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpiritCraftingRecipeCategory implements IRecipeCategory<SpiritCraftingRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(MagickCore.MOD_ID, "spirit");
    private final IDrawableStatic background;
    private final IDrawable icon;
    private final String localizedName;

    public SpiritCraftingRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/spirit_crafting.png");
        background = guiHelper.createDrawable(location, 0, 0, 100, 120);
        icon = guiHelper.createDrawableIngredient(new ItemStack(ModItems.SPIRIT_CRYSTAL.get()));
        localizedName = new TranslationTextComponent("gui.magickcore.category.magick_crafting").getString();
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends SpiritCraftingRecipe> getRecipeClass() {
        return SpiritCraftingRecipe.class;
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
    public void setIngredients(SpiritCraftingRecipe inbtRecipe, IIngredients iIngredients) {
        NonNullList<Ingredient>[] ingredientList = inbtRecipe.getIngredientList();
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < ingredientList.length; ++i) {
            NonNullList<Ingredient> ingredients = ingredientList[i];
            ingredients.forEach(ingredient -> {
                if(!ingredient.isEmpty())
                    stacks.addAll(Arrays.asList(ingredient.getItems()));
            });
        }
        iIngredients.setInputs(VanillaTypes.ITEM, stacks);
        iIngredients.setOutput(VanillaTypes.ITEM, inbtRecipe.getResultItem());
    }

    @Override
    public void draw(SpiritCraftingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        NonNullList<Ingredient>[] ingredientList = recipe.getIngredientList();
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrixStack.last().pose());
        RenderSystem.translatef(50, 40, 150);
        RenderSystem.rotatef((MagickCore.proxy.getRunTick() % 201) * 0.005f * 360f, 0, 1, 0);
        RenderSystem.rotatef(-20f, 1, 0, 0);
        float scale1 = 128f / Math.max(recipe.getRecipeY(), Math.max(recipe.getRecipeX(), recipe.getRecipeZ()));
        RenderSystem.translatef(0, ingredientList.length*scale1 * 0.15f, 0);
        int width = Math.max(recipe.getRecipeZ(), recipe.getRecipeX());
        for (int y = 0; y < recipe.getRecipeY(); ++y){
            for (int x = 0; x < recipe.getRecipeX(); ++x){
                for (int z = 0; z < recipe.getRecipeZ(); ++z){
                    Ingredient ingredient = ingredientList[y].get(z + x * recipe.getRecipeZ());
                    if(!ingredient.isEmpty())
                        renderItem(ingredient.getItems()[0].getItem(), scale1, x, y, z, width);
                }
            }
        }
        RenderSystem.popMatrix();
    }

    public void renderItem(Item item, float scale1, int x, int y, int z, int width) {
        float widthF = width * 0.25f;
        float scale = 0.5f * scale1;
        RenderSystem.pushMatrix();
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.translatef((x - widthF) * scale, (y - widthF) * scale, (z - widthF) * scale);
        RenderSystem.scalef(scale1, scale1, scale1);
        IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(item), ItemCameraTransforms.TransformType.GROUND, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, new MatrixStack(), renderTypeBuffer);
        renderTypeBuffer.endBatch();
        RenderSystem.popMatrix();
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, SpiritCraftingRecipe inbtRecipe, IIngredients iIngredients) {
        iRecipeLayout.getItemStacks().init(0, false, 41, 98);
        iRecipeLayout.getItemStacks().set(0, inbtRecipe.getResultItem());
    }
}
