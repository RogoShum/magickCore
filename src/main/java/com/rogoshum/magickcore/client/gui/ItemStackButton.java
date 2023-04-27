package com.rogoshum.magickcore.client.gui;

import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Matrix4f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;

public class ItemStackButton extends Button {
    public final ItemStack stack;
    private final ResourceLocation TOP = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/top.png");
    private final ResourceLocation BOTTOM = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/bottom.png");
    private final ResourceLocation RED_AND_BLUE = new ResourceLocation(MagickCore.MOD_ID, "textures/red_and_blue_2014.png");

    public ItemStackButton(int x, int y, int width, int height, Component title, OnPress pressedAction, ItemStack stack) {
        super(x, y, width, height, title, pressedAction);
        this.stack = stack;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderTexture(3, RED_AND_BLUE);
        RenderSystem.setShaderTexture(0, TOP);
        float alpha = 0.6f*this.alpha;
        float fog = RenderSystem.getShaderFogStart();
        if(this.isHovered) {
            alpha = 1.0f;
            RenderSystem.setShaderFogStart(0.013f);
        } else
            RenderSystem.setShaderFogStart(0.005f);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int size = 42;
        render(matrixStack, x, y, 0, 0, size, size, size, size);
        RenderSystem.setShaderTexture(0, BOTTOM);
        render(matrixStack, x, y, 0, 0, size, size, size, size);
        int j = getFGColor();
        RenderSystem.setShaderFogStart(fog);
        drawCenteredString(matrixStack, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + 45, j | Mth.ceil(this.alpha * 255.0F) << 24);
        minecraft.getItemRenderer().renderGuiItem(stack, this.x+12, this.y+12);
        RenderSystem.disableBlend();
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
        RenderSystem.setShader(RenderHelper::getPositionColorTexLightmapShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        bufferbuilder.vertex(p_93113_, (float)p_93114_, (float)p_93117_, (float)p_93118_).color(1.0F, 1.0F, 1.0F, 1.0f).uv(p_93119_, p_93122_).uv2(RenderHelper.renderLight).endVertex();
        bufferbuilder.vertex(p_93113_, (float)p_93115_, (float)p_93117_, (float)p_93118_).color(1.0F, 1.0F, 1.0F, 1.0f).uv(p_93120_, p_93122_).uv2(RenderHelper.renderLight).endVertex();
        bufferbuilder.vertex(p_93113_, (float)p_93115_, (float)p_93116_, (float)p_93118_).color(1.0F, 1.0F, 1.0F, 1.0f).uv(p_93120_, p_93121_).uv2(RenderHelper.renderLight).endVertex();
        bufferbuilder.vertex(p_93113_, (float)p_93114_, (float)p_93116_, (float)p_93118_).color(1.0F, 1.0F, 1.0F, 1.0f).uv(p_93119_, p_93121_).uv2(RenderHelper.renderLight).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
    }
}
