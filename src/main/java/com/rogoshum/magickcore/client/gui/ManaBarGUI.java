package com.rogoshum.magickcore.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public class ManaBarGUI extends AbstractGui {
    private final int width;
    private final int height;
    private final Minecraft minecraft;
    private final ResourceLocation mana_bar = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/mana_bar.png");
    private final ResourceLocation mana_bar_bg = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/mana_bar_bg.png");
    private final ResourceLocation mana_element = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/mana_element.png");
    private final ResourceLocation cylinder_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_rotate.png");
    private final ResourceLocation sphere_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/sphere_rotate.png");
    private MatrixStack matrixStack;
    private IEntityState state;

    public ManaBarGUI(MatrixStack matrixStack, IEntityState state) {
        this.width = Minecraft.getInstance().getMainWindow().getScaledWidth();
        this.height = Minecraft.getInstance().getMainWindow().getScaledHeight();
        this.minecraft = Minecraft.getInstance();
        this.matrixStack = matrixStack;
        this.state = state;
    }

    public void setMatrixStack(MatrixStack stack) {
        this.matrixStack = stack;
    }

    public void render() {
        int leng = 96;
        matrixStack.push();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(mana_bar_bg);
        blit(matrixStack, (int) (width * 0.75), (int) (height * 0.95), 0, 0, leng, 5, leng, 5);
        matrixStack.pop();

        int bar_width = (int) (leng * (state.getManaValue() / state.getMaxManaValue()));

        matrixStack.push();
        float[] bar_color = state.getElement().getRenderer().getColor();
        RenderSystem.color4f(bar_color[0], bar_color[1], bar_color[2], 1.0F);
        this.minecraft.getTextureManager().bindTexture(mana_element);
        blit(matrixStack, (int) (width * 0.75), (int) (height * 0.95), 0, 0, bar_width, 5, bar_width, 5);
        matrixStack.pop();

        matrixStack.push();
        RenderSystem.color4f(bar_color[0], bar_color[1], bar_color[2], 1.0F);
        RenderSystem.matrixMode(5890);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        long i = Util.milliTime() * 8L;
        float f = (float)(i % 110000L) / 110000.0F;
        float f1 = (float)(i % 30000L) / 30000.0F;
        RenderSystem.translatef(-f, f1, 0.0F);
        RenderSystem.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
        RenderSystem.scalef(0.25f, 0.25f, 0.25f);
        RenderSystem.matrixMode(5888);
        this.minecraft.getTextureManager().bindTexture(sphere_rotate);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.enableAlphaTest();
        RenderSystem.alphaFunc(516, 0.003921569F);
        blit(matrixStack, (int) (width * 0.75), (int) (height * 0.95) + 1, 0, 0, bar_width, 3, bar_width, 3);
        RenderSystem.defaultAlphaFunc();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.matrixMode(5890);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
        matrixStack.pop();

        matrixStack.push();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(mana_bar);
        blit(matrixStack, (int) (width * 0.75), (int) (height * 0.95), 0, 0, leng, 5, leng, 5);
        matrixStack.pop();

        /*matrixStack.push();
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, Float.toString(state.getManaValue()) + " " + Float.toString(state.getMaxManaValue()), (int) (width * 0.75), (int) (height * 0.95), 0);
        matrixStack.pop();*/
    }
}
