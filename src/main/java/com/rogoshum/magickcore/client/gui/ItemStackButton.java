package com.rogoshum.magickcore.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.text.ITextComponent;

import java.util.List;


import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.world.item.ItemStack;

public class ItemStackButton extends Button {
    public final ItemStack stack;

    public ItemStackButton(int x, int y, int width, int height, ITextComponent title, IPressable pressedAction, ItemStack stack) {
        super(x, y, width, height, title, pressedAction);
        this.stack = stack;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.font;
        minecraft.getTextureManager().bind(new ResourceLocation("textures/gui/widgets.png"));
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(matrixStack, this.x, this.y, 1, 1, 20, 20);
        if(this.isHovered())
            this.blit(matrixStack, this.x-1, this.y-1, 1, 23, 22, 22);
        this.renderBg(matrixStack, minecraft, mouseX, mouseY);
        int j = getFGColor();
        drawCenteredString(matrixStack, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + 21, j | Mth.ceil(this.alpha * 255.0F) << 24);
        minecraft.getItemRenderer().renderGuiItem(stack, this.x+2, this.y+2);
    }

    @Override
    public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        net.minecraftforge.fml.client.gui.GuiUtils.preItemToolTip(stack);
        net.minecraftforge.fml.client.gui.GuiUtils.drawHoveringText(
                matrixStack, stack.getTooltipLines(Minecraft.getInstance().player,
                        Minecraft.getInstance().options.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL)
                , mouseX, mouseY, width, height, -1, font == null ? Minecraft.getInstance().font : font);
        net.minecraftforge.fml.client.gui.GuiUtils.postItemToolTip();
    }
}
