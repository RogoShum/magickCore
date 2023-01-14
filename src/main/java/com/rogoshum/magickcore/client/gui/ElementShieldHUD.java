package com.rogoshum.magickcore.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class ElementShieldHUD extends AbstractGui {
    private final ResourceLocation TEXTURE = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/element_shield.png");
    private final int width;
    private final int height;
    private final Minecraft minecraft;
    private final EntityStateData state;

    public ElementShieldHUD(EntityStateData state) {
        this.width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        this.height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        this.minecraft = Minecraft.getInstance();
        this.state = state;
    }

    public void render() {
        if(state.getElementShieldMana() <= 0 || state.getMaxElementShieldMana() <= 0) return;
        float alpha = state.getElementShieldMana() / state.getMaxElementShieldMana();
        Color color = state.getElement().color();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(color.r(), color.g(), color.b(), alpha);
        RenderSystem.disableAlphaTest();
        minecraft.getTextureManager().bind(TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.vertex(0.0D, (double)this.height, -90.0D).uv(0.0F, 1.0F).endVertex();
        bufferbuilder.vertex((double)this.width, (double)this.height, -90.0D).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex((double)this.width, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
        tessellator.end();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableAlphaTest();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
