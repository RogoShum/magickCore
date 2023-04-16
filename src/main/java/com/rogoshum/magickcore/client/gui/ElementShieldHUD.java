package com.rogoshum.magickcore.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.ResourceLocation;

public class ElementShieldHUD extends GuiComponent {
    private final ResourceLocation TEXTURE = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/element_shield.png");
    private final int width;
    private final int height;
    private final EntityStateData state;

    public ElementShieldHUD(EntityStateData state) {
        this.width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        this.height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        this.state = state;
    }

    public void render() {
        if(state.getElementShieldMana() <= 0 || state.getMaxElementShieldMana() <= 0) return;
        float alpha = state.getElementShieldMana() / state.getMaxElementShieldMana();
        Color color = state.getElement().primaryColor();

        RenderSystem.setShaderColor(color.r(), color.g(), color.b(), alpha);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderTexture(1, RenderHelper.Noise.STRING.res());
        long i = Util.getMillis() / 2;
        float f = (float)(i % 110000L) / 110000.0F;
        float f1 = (float)(i % 30000L) / 30000.0F;
        Matrix4f matrix4f = Matrix4f.createTranslateMatrix(-f, f1, 0.0F);
        //matrix4f.multiply(Matrix4f.createScaleMatrix(0.5f, 0.5f, 0.0F));
        RenderSystem.setTextureMatrix(matrix4f);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.setShader(RenderHelper::getPositionTextureShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(0.0D, (double)this.height, -90.0D).uv(0.0F, 1.0F).endVertex();
        bufferbuilder.vertex((double)this.width, (double)this.height, -90.0D).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex((double)this.width, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
        tessellator.end();
        RenderSystem.defaultBlendFunc();
        RenderSystem.resetTextureMatrix();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
