package com.rogoshum.magickcore.client.gui;

import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Matrix4f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.item.IManaData;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;

public class ManaBarHUD extends GuiComponent {
    private final int width;
    private final int height;
    private final ResourceLocation mana_bar = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/mana_bar.png");
    private final ResourceLocation mana_bar_bg = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/mana_bar_bg.png");
    private final ResourceLocation mana_element = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/mana_element.png");
    private PoseStack matrixStack;
    private final EntityStateData state;

    public ManaBarHUD(PoseStack matrixStack, EntityStateData state) {
        this.width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        this.height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        this.matrixStack = matrixStack;
        this.state = state;
    }

    public void setMatrixStack(PoseStack stack) {
        this.matrixStack = stack;
    }

    public void render() {
        int leng = 110;
        int wid = 10;
        int y = (int) (height * 0.96);
        int x = (int) (width * 0.72);
        int bar_width = (int) (leng * (state.getManaValue() / state.getMaxManaValue()));
        Color bar_color = state.getElement().getRenderer().getPrimaryColor();
        long i = Util.getMillis() / 4;
        float f = (float)(i % 110000L) / 110000.0F;
        float f1 = (float)(i % 30000L) / 30000.0F;
        Matrix4f matrix4f = Matrix4f.createTranslateMatrix(-f, f1, 0.0F);
        matrix4f.multiply(Matrix4f.createScaleMatrix(1f, 0.1f, 0.0F));

        if(RenderHelper.showDebug() || state.getManaValue() < state.getMaxManaValue() || Minecraft.getInstance().player.getMainHandItem().getItem() instanceof IManaData || Minecraft.getInstance().player.getOffhandItem().getItem() instanceof IManaData) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, mana_bar_bg);
            blit(matrixStack, x, y, 0, 0, leng, wid, leng, wid);

            RenderSystem.setTextureMatrix(matrix4f);
            RenderSystem.setShaderColor(bar_color.r(), bar_color.g(), bar_color.b(), 1.0F);
            RenderSystem.setShaderTexture(0, mana_element);
            blit(matrixStack, x, y, 0, 0, bar_width, wid, leng, wid);
            RenderSystem.setShaderTexture(1, RenderHelper.Noise.DISSOVE.res());
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            render(matrixStack, x, y, 0, 0, bar_width, wid, leng, wid);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.resetTextureMatrix();

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, mana_bar);
            blit(matrixStack, x, y, 0, 0, leng, wid, leng, wid);

            if(RenderHelper.showDebug()) {
                String mana = Float.toString(state.getManaValue());
                Minecraft.getInstance().font.draw(matrixStack, mana + " / " + Float.toString(state.getMaxManaValue()), x +50- Minecraft.getInstance().font.width(mana), y+2, 0);
            }
        }

        if(RenderHelper.showDebug() || state.getElementShieldMana() > 0) {
            y -= 12;
            bar_color = state.getElement().getRenderer().getSecondaryColor();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, mana_bar_bg);
            blit(matrixStack, x, y, 0, 0, leng, wid, leng, wid);

            bar_width = (int) (leng * (state.getElementShieldMana() / Math.max(state.getMaxElementShieldMana(), 0.01f)));

            RenderSystem.setShaderTexture(0, mana_element);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.setShaderColor(bar_color.r(), bar_color.g(), bar_color.b(), 0.25F);
            blit(matrixStack, x, y, 0, 0, bar_width, wid, leng, wid);
            RenderSystem.disableBlend();
            RenderSystem.resetTextureMatrix();

            RenderSystem.setTextureMatrix(matrix4f);
            RenderSystem.setShaderColor(bar_color.r(), bar_color.g(), bar_color.b(), 1.0F);
            RenderSystem.setShaderTexture(1, RenderHelper.Noise.LINE.res());
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            render(matrixStack, x, y, 0, 0, bar_width, wid, leng, wid);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.resetTextureMatrix();

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, mana_bar);
            blit(matrixStack, x, y, 0, 0, leng, wid, leng, wid);

            if(RenderHelper.showDebug()) {
                String mana = Float.toString(state.getElementShieldMana());
                Minecraft.getInstance().font.draw(matrixStack, mana + " / " + Float.toString(state.getMaxElementShieldMana()), x +50- Minecraft.getInstance().font.width(mana), y+2, 0);
            }
        }
    }

    public static void render(PoseStack poseStack, int x, int y, float p_93137_, float p_93138_, int p_93139_, int p_93140_, int p_93141_, int p_93142_) {
        blit(poseStack, x, y, p_93139_, p_93140_, p_93137_, p_93138_, p_93139_, p_93140_, p_93141_, p_93142_);
    }

    public static void blit(PoseStack p_93161_, int p_93162_, int p_93163_, int p_93164_, int p_93165_, float p_93166_, float p_93167_, int p_93168_, int p_93169_, int p_93170_, int p_93171_) {
        innerBlit(p_93161_, p_93162_, p_93162_ + p_93164_, p_93163_, p_93163_ + p_93165_, 0, p_93168_, p_93169_, p_93166_, p_93167_, p_93170_, p_93171_);
    }

    private static void innerBlit(PoseStack p_93188_, int p_93189_, int p_93190_, int p_93191_, int p_93192_, int p_93193_, int p_93194_, int p_93195_, float p_93196_, float p_93197_, int p_93198_, int p_93199_) {
        innerBlit(p_93188_.last().pose(), p_93189_, p_93190_, p_93191_, p_93192_, p_93193_, (p_93196_ + 0.0F) / (float)p_93198_, (p_93196_ + (float)p_93194_) / (float)p_93198_, (p_93197_ + 0.0F) / (float)p_93199_, (p_93197_ + (float)p_93195_) / (float)p_93199_);
    }

    private static void innerBlit(Matrix4f p_93113_, int p_93114_, int p_93115_, int p_93116_, int p_93117_, int p_93118_, float p_93119_, float p_93120_, float p_93121_, float p_93122_) {
        RenderSystem.setShader(RenderHelper::getPositionTextureShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(p_93113_, (float)p_93114_, (float)p_93117_, (float)p_93118_).uv(p_93119_, p_93122_).endVertex();
        bufferbuilder.vertex(p_93113_, (float)p_93115_, (float)p_93117_, (float)p_93118_).uv(p_93120_, p_93122_).endVertex();
        bufferbuilder.vertex(p_93113_, (float)p_93115_, (float)p_93116_, (float)p_93118_).uv(p_93120_, p_93121_).endVertex();
        bufferbuilder.vertex(p_93113_, (float)p_93114_, (float)p_93116_, (float)p_93118_).uv(p_93119_, p_93121_).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
    }
}
