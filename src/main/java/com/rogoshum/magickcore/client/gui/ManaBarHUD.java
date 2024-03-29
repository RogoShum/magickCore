package com.rogoshum.magickcore.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.itemstack.IManaData;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;

public class ManaBarHUD extends GuiComponent {
    private final int width;
    private final int height;
    private final Minecraft minecraft;
    private final ResourceLocation mana_bar = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/mana_bar.png");
    private final ResourceLocation mana_bar_bg = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/mana_bar_bg.png");
    private final ResourceLocation mana_element = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/mana_element.png");
    private final ResourceLocation cylinder_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_bloom.png");
    private final ResourceLocation sphere_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/sphere_rotate.png");
    private PoseStack matrixStack;
    private EntityStateData state;

    public ManaBarHUD(PoseStack matrixStack, EntityStateData state) {
        this.width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        this.height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        this.minecraft = Minecraft.getInstance();
        this.matrixStack = matrixStack;
        this.state = state;
    }

    public void setPoseStack(PoseStack stack) {
        this.matrixStack = stack;
    }

    public void render() {
        int leng = 96;
        int y = (int) (height * 0.96);
        int x = (int) (width * 0.75);
        int bar_width = (int) (leng * (state.getManaValue() / state.getMaxManaValue()));
        Color bar_color = state.getElement().getRenderer().getColor();
        long i = Util.getMillis() * 8L;
        float f = (float)(i % 110000L) / 110000.0F;
        float f1 = (float)(i % 30000L) / 30000.0F;

        if(RenderHelper.showDebug() || state.getManaValue() < state.getMaxManaValue() || Minecraft.getInstance().player.getMainHandItem().getItem() instanceof IManaData || Minecraft.getInstance().player.getOffhandItem().getItem() instanceof IManaData) {
            matrixStack.pushPose();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.minecraft.getTextureManager().bind(mana_bar_bg);
            blit(matrixStack, x, y, 0, 0, leng, 5, leng, 5);
            matrixStack.popPose();

            matrixStack.pushPose();
            RenderSystem.color4f(bar_color.r(), bar_color.g(), bar_color.b(), 1.0F);
            this.minecraft.getTextureManager().bind(mana_element);
            blit(matrixStack, x, y, 0, 0, bar_width, 5, bar_width, 5);
            matrixStack.popPose();

            matrixStack.pushPose();
            RenderSystem.color4f(bar_color.r(), bar_color.g(), bar_color.b(), 1.0F);
            RenderSystem.matrixMode(5890);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();

            RenderSystem.translatef(-f, f1, 0.0F);
            //RenderSystem.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
            RenderSystem.scalef(0.25f, 0.25f, 0.25f);
            RenderSystem.matrixMode(5888);
            this.minecraft.getTextureManager().bind(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_bloom.png"));

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.enableAlphaTest();
            RenderSystem.alphaFunc(516, 0.003921569F);
            blit(matrixStack, x, y + 1, 0, 0, bar_width, 3, bar_width, 3);
            RenderSystem.defaultAlphaFunc();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();

            RenderSystem.matrixMode(5890);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(5888);
            matrixStack.popPose();


            matrixStack.pushPose();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.minecraft.getTextureManager().bind(mana_bar);
            blit(matrixStack, x, y, 0, 0, leng, 5, leng, 5);
            matrixStack.popPose();

            if(RenderHelper.showDebug()) {
                matrixStack.pushPose();
                matrixStack.scale(0.5f, 0.5f, 1);
                String mana = Float.toString(state.getManaValue());
                Minecraft.getInstance().font.draw(matrixStack, mana + " / " + Float.toString(state.getMaxManaValue()), x*2 + 96 - mana.length()*7, y*2+1, 0);
                matrixStack.popPose();
            }
        }

        if(RenderHelper.showDebug() || state.getElementShieldMana() > 0) {
            y -= 6;
            matrixStack.pushPose();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.minecraft.getTextureManager().bind(mana_bar_bg);
            blit(matrixStack, x, y, 0, 0, leng, 5, leng, 5);
            matrixStack.popPose();

            bar_width = (int) (leng * (state.getElementShieldMana() / Math.max(state.getMaxElementShieldMana(), 0.01)));
            if(bar_width > leng)
                bar_width = leng;

            matrixStack.pushPose();
            bar_color = state.getElement().getRenderer().getColor();
            RenderSystem.enableBlend();
            RenderSystem.color4f(bar_color.r(), bar_color.g(), bar_color.b(), 0.5F);
            this.minecraft.getTextureManager().bind(mana_element);
            blit(matrixStack, x, y, 0, 0, bar_width, 5, bar_width, 5);
            RenderSystem.disableBlend();
            matrixStack.popPose();

            matrixStack.pushPose();
            RenderSystem.color4f(bar_color.r(), bar_color.g(), bar_color.b(), 1.0F);
            RenderSystem.matrixMode(5890);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            RenderSystem.translatef(0, f1, 0.0F);
            RenderSystem.rotatef(90.0F, 90.0F, 0.0F, 1.0F);
            RenderSystem.scalef(1f, 9f, 1f);
            RenderSystem.matrixMode(5888);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.enableAlphaTest();
            RenderSystem.alphaFunc(516, 0.003921569F);
            this.minecraft.getTextureManager().bind(RenderHelper.ripple_4);
            blit(matrixStack, x, y + 1, 0, 0, bar_width, 3, bar_width, 3);
            this.minecraft.getTextureManager().bind(RenderHelper.ripple_2);
            blit(matrixStack, x, y + 1, 0, 0, bar_width, 3, bar_width, 3);
            RenderSystem.defaultAlphaFunc();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();

            RenderSystem.matrixMode(5890);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(5888);
            matrixStack.popPose();

            matrixStack.pushPose();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.minecraft.getTextureManager().bind(mana_bar);
            blit(matrixStack, x, y, 0, 0, leng, 5, leng, 5);
            matrixStack.popPose();

            if(RenderHelper.showDebug()) {
                matrixStack.pushPose();
                matrixStack.scale(0.5f, 0.5f, 1);
                String mana = Float.toString(state.getElementShieldMana());
                Minecraft.getInstance().font.draw(matrixStack, mana + " / " + Float.toString(state.getMaxElementShieldMana()), x*2 + 96 - mana.length()*7, y*2+1, 0);
                matrixStack.popPose();
            }
        }
    }
}
