package com.rogoshum.magickcore.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;

import java.util.List;


import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraftforge.client.gui.GuiUtils;

public class ItemStackButton extends Button {
    public final ItemStack stack;

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
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(matrixStack, this.x, this.y, 1, 1, 20, 20);
        if(this.isHovered)
            this.blit(matrixStack, this.x-1, this.y-1, 1, 23, 22, 22);
        this.renderBg(matrixStack, minecraft, mouseX, mouseY);
        int j = getFGColor();
        drawCenteredString(matrixStack, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + 21, j | Mth.ceil(this.alpha * 255.0F) << 24);
        minecraft.getItemRenderer().renderGuiItem(stack, this.x+2, this.y+2);
    }
}
