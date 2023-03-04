package com.rogoshum.magickcore.client.integration.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.recipe.MagickWorkbenchRecipe;
import com.rogoshum.magickcore.common.recipe.SpiritCraftingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpiritCraftingRecipeCategory implements IRecipeCategory<SpiritCraftingRecipe>{
    private final IDrawableStatic background;
    private final IDrawable icon;
    private final Component localizedName;
    public static final RecipeType<SpiritCraftingRecipe> RECIPE_TYPE = RecipeType.create(MagickCore.MOD_ID, "spirit", SpiritCraftingRecipe.class);

    public SpiritCraftingRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/spirit_crafting.png");
        background = guiHelper.createDrawable(location, 0, 0, 100, 120);
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.SPIRIT_CRYSTAL.get()));
        localizedName = new TranslatableComponent("gui.magickcore.category.magick_crafting");
    }

    @Override
    public RecipeType<SpiritCraftingRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public ResourceLocation getUid() {
        return getRecipeType().getUid();
    }

    @Override
    public Class<? extends SpiritCraftingRecipe> getRecipeClass() {
        return getRecipeType().getRecipeClass();
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
    public void draw(SpiritCraftingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        NonNullList<Ingredient>[] ingredientList = recipe.getIngredientList();
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.mulPoseMatrix(matrixStack.last().pose());
        poseStack.translate(50, 40, 150);
        poseStack.mulPose(Vector3f.YP.rotationDegrees((MagickCore.proxy.getRunTick() % 201) * 0.005f * 360f));
        poseStack.mulPose(Vector3f.XN.rotationDegrees(20f));
        float scale1 = 128f / Math.max(recipe.getRecipeY(), Math.max(recipe.getRecipeX(), recipe.getRecipeZ()));
        poseStack.translate(0, ingredientList.length*scale1 * 0.15f, 0);
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
        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    public void renderItem(Item item, float scale1, int x, int y, int z, int width) {
        float widthF = width * 0.25f;
        float scale = 0.5f * scale1;
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, 1.0F);
        poseStack.translate((x - widthF) * scale, (y - widthF) * scale, (z - widthF) * scale);
        poseStack.scale(scale1, scale1, scale1);
        RenderSystem.applyModelViewMatrix();
        MultiBufferSource.BufferSource renderTypeBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(item), ItemTransforms.TransformType.GROUND, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, new PoseStack(), renderTypeBuffer, 0);
        renderTypeBuffer.endBatch();
        poseStack.popPose();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, SpiritCraftingRecipe inbtRecipe, IFocusGroup iIngredients) {
        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 41, 98).addItemStack(inbtRecipe.getResultItem());
    }
}
