package com.rogoshum.magickcore.client.patchouli;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.recipe.MagickCraftingRecipe;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.util.MultiBlockUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import java.util.function.UnaryOperator;

public class MagickRecipeComponent implements ICustomComponent {
    @SerializedName("magick_recipe")
    public String magickRecipe;
    public int stack = 0;
    private transient MagickCraftingRecipe recipe;
    private transient int x, y;
    @Override
    public void build(int componentX, int componentY, int pageNum) {
        this.x = componentX;
        this.y = componentY;
    }

    @Override
    public void render(MatrixStack ms, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        if(this.recipe == null) return;
        String[][][] recipe = this.recipe.getRecipe();
        MultiBlockUtil.PlaceableEntityPattern[] patterns = this.recipe.getPattern();
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(ms.getLast().getMatrix());
        RenderSystem.translatef(this.x+50, this.y+50, 150);
        RenderSystem.rotatef((MagickCore.proxy.getRunTick() % 201) * 0.005f * 360f, 0, 1, 0);
        RenderSystem.rotatef(-20f, 1, 0, 0);
        if(stack <= 0) {
            float scale1 = 128f / Math.max(recipe.length, Math.max(recipe[0][0].length, recipe[0].length));
            RenderSystem.translatef(0, recipe.length*scale1 * 0.15f, 0);
            int width = Math.max(recipe[0][0].length, recipe[0].length);
            for (int y = 0; y < recipe.length; ++y){
                for (int x = 0; x < recipe[y].length; ++x){
                    for (int z = 0; z < recipe[y][x].length; ++z){
                        String s = recipe[y][x][z];
                        for (MultiBlockUtil.PlaceableEntityPattern pattern : patterns) {
                            if(pattern.getPattern().equals(s)) {
                                if(pattern.item != null) {
                                    renderItem(pattern.item, scale1, x, y, z, width);
                                }
                            }
                        }
                    }
                }
            }
        } else if(recipe.length >= stack){
            RenderSystem.translatef(0, 8, 0);
            float scale1 = 128f / Math.max(recipe.length, Math.max(recipe[0][0].length, recipe[0].length));
            int width = Math.max(recipe[0][0].length, recipe[0].length);
            for (int x = 0; x < recipe[stack-1].length; ++x){
                for (int z = 0; z < recipe[stack-1][x].length; ++z){
                    String s = recipe[stack-1][x][z];
                    for (MultiBlockUtil.PlaceableEntityPattern pattern : patterns) {
                        if(pattern.getPattern().equals(s)) {
                            if(pattern.item != null) {
                                renderItem(pattern.item, scale1, x, 0, z, width);
                            }
                        }
                    }
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
        IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        Minecraft.getInstance().getItemRenderer().renderItem(new ItemStack(item), ItemCameraTransforms.TransformType.GROUND, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, new MatrixStack(), renderTypeBuffer);
        renderTypeBuffer.finish();
        RenderSystem.popMatrix();
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
        this.magickRecipe = lookup.apply(IVariable.wrap(this.magickRecipe)).asString();
        //this.stack = lookup.apply(IVariable.wrap(this.stack)).asNumber().intValue();
        this.recipe = MagickRegistry.getMagickCraftingRecipe(this.magickRecipe);
    }
}
