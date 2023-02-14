package com.rogoshum.magickcore.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.api.mana.IManaContextItem;
import com.rogoshum.magickcore.common.network.CSpellSwapPack;
import com.rogoshum.magickcore.common.network.Networking;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.Map;

public class SpellSwapBoxGUI extends Screen{
    ItemStack heldItem = ItemStack.EMPTY;
    ItemStack hoverItem = ItemStack.EMPTY;

    public SpellSwapBoxGUI(Component titleIn) {
        super(titleIn);
    }

    @Override
    protected void init() {
        Player player = Minecraft.getInstance().player;
        if(player == null) {
            onClose();
            return;
        }
        if(player.getMainHandItem().getItem() instanceof IManaContextItem)
            heldItem = player.getMainHandItem();
        else if(player.getOffhandItem().getItem() instanceof IManaContextItem)
            heldItem = player.getOffhandItem();

        if(heldItem.isEmpty()) {
            onClose();
            return;
        }
        NBTTagHelper.PlayerData playerData = NBTTagHelper.PlayerData.playerData(player);
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);

        int x = (int) (this.width * 0.35);
        int y = (int) (this.height * 0.10);
        int i = 0;
        for(Map.Entry<String, ItemStack> entry : playerData.getSpells().entrySet()) {
            if(i == 3 || i == 9) {
                y = (int) (this.height * 0.55);
            } else if(i == 6) {
                x = (int) (this.width * 0.60);
                y = (int) (this.height * 0.10);
            }
            ItemStack item = entry.getValue();

            int finalI = i;
            this.addWidget(new ItemStackButton(x, y, 21, 21, item.getHoverName(), (button) -> {
                Networking.INSTANCE.send(
                        PacketDistributor.SERVER.noArg(), CSpellSwapPack.swapItem(player.getId(), finalI));
                onClose();
            }, item));
            y+=30;
            i++;
        }

        while (i < playerData.getLimit()) {
            if(i == 3 || i == 9) {
                y = (int) (this.height * 0.55);
            } else if(i == 6) {
                x = (int) (this.width * 0.60);
                y = (int) (this.height * 0.10);
            }
            int finalC = i;
            this.addWidget(new ItemStackButton(x, y, 21, 21, new TextComponent(""), (button) -> {
                Networking.INSTANCE.send(
                        PacketDistributor.SERVER.noArg(), CSpellSwapPack.swapItem(player.getId(), finalC));
                onClose();
            }, ItemStack.EMPTY));
            y+=30;
            i++;
        }
        super.init();
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.scale(5, 5, 0);
        minecraft.getItemRenderer().renderGuiItem(heldItem, width / 12, height / 16);
        poseStack.popPose();
        drawCenteredString(matrixStack, this.font, heldItem.getHoverName(), this.width / 2, (int) (this.height * 0.7), 0xFFFFFF);
        boolean hover = false;
        for (Widget button : renderables) {
            if(button instanceof ItemStackButton) {
                ItemStackButton itemStackButton = (ItemStackButton) button;
                if(isSlotSelected(itemStackButton, mouseX, mouseY)) {
                    hoverItem = itemStackButton.stack;
                    hover = true;
                }
            }
        }
        if(hover) {
            renderHoveredTooltip(matrixStack, mouseX, mouseY);
        }
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    protected void renderHoveredTooltip(PoseStack matrixStack, int x, int y) {
        if (this.minecraft.player.getInventory().getSelected().isEmpty() && this.hoverItem != null && !this.hoverItem.isEmpty()) {
            this.renderTooltip(matrixStack, hoverItem, x, y);
        }
    }

    private boolean isSlotSelected(ItemStackButton button, double mouseX, double mouseY) {
        return this.isPointInRegion(button.x, button.y, button.getWidth(), button.getWidth(), mouseX, mouseY);
    }

    protected boolean isPointInRegion(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= (double)(x - 1) && mouseX < (double)(x + width + 1) && mouseY >= (double)(y - 1) && mouseY < (double)(y + height + 1);
    }
}
