package com.rogoshum.magickcore.client.gui;

import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaContextItem;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.init.ModKeyBind;
import com.rogoshum.magickcore.common.network.CSpellSwapPack;
import com.rogoshum.magickcore.common.network.Networking;
import com.rogoshum.magickcore.common.util.MutableInteger;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.Map;

public class SpellSwapBoxGUI extends Screen {
    ItemStack heldItem = ItemStack.EMPTY;
    ItemStack hoverItem = ItemStack.EMPTY;
    boolean releaseR = false;
    int x3;
    int x6;
    int x9;
    int baseY;
    int playerId;
    private final ResourceLocation TOP = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/top.png");
    private final ResourceLocation BOTTOM = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/bottom.png");
    public SpellSwapBoxGUI(Component titleIn) {
        super(titleIn);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(PoseStack p_96557_) {
        RenderSystem.setShaderTexture(3, RenderHelper.RED_AND_BLUE);
        RenderSystem.setShaderTexture(0, TOP);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderGameTime(MagickCore.proxy.getRunTick() * 50L, 0);
        float fog = RenderSystem.getShaderFogStart();
        RenderSystem.setShaderFogStart(0.0025f);
        RenderSystem.setShader(RenderHelper::getPositionColorTexLightmapShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        bufferbuilder.vertex((double)this.width*0.415, (double)this.height*0.6, 90.0D).color(1.0F, 1.0F, 1.0F, 1.0f).uv(0.0F, 1.0F).uv2(RenderHelper.renderLight).endVertex();
        bufferbuilder.vertex((double)this.width*0.58, (double)this.height*0.6, 90.0D).color(1.0F, 1.0F, 1.0F, 1.0f).uv(1.0F, 1.0F).uv2(RenderHelper.renderLight).endVertex();
        bufferbuilder.vertex((double)this.width*0.58, (double)this.height*0.32, 90.0D).color(1.0F, 1.0F, 1.0F, 1.0f).uv(1.0F, 0.0F).uv2(RenderHelper.renderLight).endVertex();
        bufferbuilder.vertex((double)this.width*0.415, (double)this.height*0.32, 90.0D).color(1.0F, 1.0F, 1.0F, 1.0f).uv(0.0F, 0.0F).uv2(RenderHelper.renderLight).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
        RenderSystem.setShaderTexture(0, BOTTOM);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        bufferbuilder.vertex((double)this.width*0.415, (double)this.height*0.6, 90.0D).color(1.0F, 1.0F, 1.0F, 1.0f).uv(0.0F, 1.0F).uv2(RenderHelper.renderLight).endVertex();
        bufferbuilder.vertex((double)this.width*0.58, (double)this.height*0.6, 90.0D).color(1.0F, 1.0F, 1.0F, 1.0f).uv(1.0F, 1.0F).uv2(RenderHelper.renderLight).endVertex();
        bufferbuilder.vertex((double)this.width*0.58, (double)this.height*0.32, 90.0D).color(1.0F, 1.0F, 1.0F, 1.0f).uv(1.0F, 0.0F).uv2(RenderHelper.renderLight).endVertex();
        bufferbuilder.vertex((double)this.width*0.415, (double)this.height*0.32, 90.0D).color(1.0F, 1.0F, 1.0F, 1.0f).uv(0.0F, 0.0F).uv2(RenderHelper.renderLight).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
        RenderSystem.setShaderFogStart(fog);
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderGameTime(Minecraft.getInstance().level.getGameTime(), Minecraft.getInstance().getFrameTime());
    }

    @Override
    public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_) {
        if (this.minecraft.options.keyInventory.matches(p_96552_, p_96553_)) {
            this.onClose();
            return true;
        } else if (this.minecraft.options.keyPlayerList.matches(p_96552_, p_96553_)) {
            this.onClose();
            return true;
        }  else if (ModKeyBind.SWAP_KEY.matches(p_96552_, p_96553_)) {
            this.onClose();
            return true;
        } else {
            return super.keyPressed(p_96552_, p_96553_, p_96554_);
        }
    }

    @Override
    public boolean keyReleased(int p_94715_, int p_94716_, int p_94717_) {
        if (releaseR && ModKeyBind.SWAP_KEY.matches(p_94715_, p_94716_)) {
            releaseR = true;
        }
        return super.keyReleased(p_94715_, p_94716_, p_94717_);
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

        MutableInteger x = new MutableInteger((int) (this.width * 0.27));
        MutableInteger y = new MutableInteger((int) (this.height * 0.22));
        x3 = (int) (this.width * 0.34);
        x6 = (int) (this.width * 0.59);
        x9 = (int) (this.width * 0.66);
        baseY = y.get();
        playerId = player.getId();
        MutableInteger i = new MutableInteger();
        for(Map.Entry<String, ItemStack> entry : playerData.getSpells().entrySet()) {
            ItemStack item = entry.getValue();
            addButton(i, x, y, item.getHoverName(), item);
        }

        while (i.get() < playerData.getLimit()) {
            addButton(i, x, y, new TextComponent(""), ItemStack.EMPTY);
        }
        super.init();
    }

    protected void addButton(MutableInteger i, MutableInteger x, MutableInteger y, Component title, ItemStack stack) {
        if(i.get() == 3) {
            x.set(x3);
            y.set(baseY);
        } else if(i.get() == 6) {
            x.set(x6);
            y.set(baseY);
        } else if(i.get() == 9) {
            x.set(x9);
            y.set(baseY);
        }
        final int index = i.get();
        this.addWidget(new ItemStackButton(x.get(), y.get(), 42, 42, title, (button) -> {
            Networking.INSTANCE.send(
                    PacketDistributor.SERVER.noArg(), CSpellSwapPack.swapItem(playerId, index));
            onClose();
        }, stack));
        y.add(70);
        i.add();
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.scale(3, 3, 0);
        minecraft.getItemRenderer().renderGuiItem(heldItem, (int) (width / 6.5), (int) (height / 7.65));
        poseStack.popPose();
        boolean hover = false;
        List<? extends GuiEventListener> children = children();
        for (GuiEventListener button : children) {
            if(button instanceof ItemStackButton itemStackButton) {
                if(isSlotSelected(itemStackButton, mouseX, mouseY)) {
                    hoverItem = itemStackButton.stack;
                    hover = true;
                }
            }
        }


        for (GuiEventListener button : children) {
            if(button instanceof ItemStackButton itemStackButton) {
                itemStackButton.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }
        drawCenteredString(matrixStack, this.font, heldItem.getHoverName(), this.width / 2, (int) (this.height * 0.63), 0xFFFFFF);
        if(hover) {
            renderHoveredTooltip(matrixStack, mouseX, mouseY);
        }
        hoverItem = ItemStack.EMPTY;
        this.renderBackground(matrixStack);
    }

    protected void renderHoveredTooltip(PoseStack matrixStack, int x, int y) {
        if (this.hoverItem != null && !this.hoverItem.isEmpty()) {
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
