package com.rogoshum.magickcore.client.integration.patchouli;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.init.ModRecipes;
import com.rogoshum.magickcore.common.recipe.SpiritCraftingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class MagickRecipeComponent implements ICustomComponent {
    @SerializedName("magick_recipe")
    public String magickRecipe;
    public int stack = 0;
    private transient SpiritCraftingRecipe recipe;
    private transient int x, y;
    @Override
    public void build(int componentX, int componentY, int pageNum) {
        this.x = componentX;
        this.y = componentY;
    }

    @Override
    public void render(PoseStack ms, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        if(this.recipe == null) return;
        NonNullList<Ingredient>[] recipe = this.recipe.getIngredientList();
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(ms.last().pose());
        RenderSystem.translatef(this.x+50, this.y+50, 150);
        RenderSystem.rotatef((MagickCore.proxy.getRunTick() % 201) * 0.005f * 360f, 0, 1, 0);
        RenderSystem.rotatef(-20f, 1, 0, 0);
        if(stack <= 0) {
            float scale1 = 128f / Math.max(this.recipe.getRecipeY(), Math.max(this.recipe.getRecipeX(), this.recipe.getRecipeZ()));
            RenderSystem.translatef(0, recipe.length*scale1 * 0.15f, 0);
            int width = Math.max(this.recipe.getRecipeZ(), this.recipe.getRecipeX());
            for (int y = 0; y < this.recipe.getRecipeY(); ++y){
                for (int x = 0; x < this.recipe.getRecipeX(); ++x){
                    for (int z = 0; z < this.recipe.getRecipeZ(); ++z){
                        Ingredient ingredient = recipe[y].get(z + x * this.recipe.getRecipeZ());
                        if(!ingredient.isEmpty())
                            renderItem(ingredient.getItems()[0].getItem(), scale1, x, y, z, width);
                    }
                }
            }
        } else if(recipe.length >= stack){
            RenderSystem.translatef(0, 8, 0);
            float scale1 = 128f / Math.max(this.recipe.getRecipeY(), Math.max(this.recipe.getRecipeX(), this.recipe.getRecipeZ()));
            int width = Math.max(this.recipe.getRecipeZ(), this.recipe.getRecipeX());
            for (int x = 0; x < this.recipe.getRecipeX(); ++x){
                for (int z = 0; z < this.recipe.getRecipeZ(); ++z){
                    Ingredient ingredient = recipe[stack-1].get(z + x * this.recipe.getRecipeZ());
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
        MultiBufferSource.BufferSource renderTypeBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(item), ItemTransforms.TransformType.GROUND, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, new PoseStack(), renderTypeBuffer);
        renderTypeBuffer.endBatch();
        RenderSystem.popMatrix();
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
        this.magickRecipe = lookup.apply(IVariable.wrap(this.magickRecipe)).asString();
        List<SpiritCraftingRecipe> recipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(ModRecipes.SPIRIT_CRAFTING);
        recipes.forEach(magickCraftingRecipe -> magickCraftingRecipe.getId().toString().equals(this.magickRecipe));
        Optional<SpiritCraftingRecipe> optional = recipes.stream().filter(recipe -> recipe.getId().toString().equals(this.magickRecipe)).findAny();
        optional.ifPresent(magickCraftingRecipe -> this.recipe = magickCraftingRecipe);
    }
}
