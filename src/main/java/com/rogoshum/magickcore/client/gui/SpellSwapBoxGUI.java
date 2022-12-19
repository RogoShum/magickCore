package com.rogoshum.magickcore.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaContextItem;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.network.CSpellSwapPack;
import com.rogoshum.magickcore.common.network.Networking;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;
import java.util.Map;

public class SpellSwapBoxGUI extends Screen {
    /*
    player 持续Tag记录法术Stack
    初始栈长度3，钻石+灵魂水晶合成可以逐步增加到6
    岩浆膏+灵魂水晶逐步到9
    紫颂果+灵魂水晶逐步到12

    手持法术长按R键推送法术入栈
    空手长按R出栈 潜行长按一次出栈3个

    手持法杖按R交换ItemStack （GUI）

    法杖改名：合成时法杖NBT记录原名，核心名字赋予法杖，复原时法杖名字跟随核心，NBT变回原名
     */
    //private final ResourceLocation TEXTURE = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/element_shield.png");
    List<Button> buttons;
    TranslationTextComponent content = new TranslationTextComponent("gui." + MagickCore.MOD_ID + ".first");
    ItemStack heldItem = ItemStack.EMPTY;

    public SpellSwapBoxGUI(ITextComponent titleIn) {
        super(titleIn);
    }

    @Override
    protected void init() {
        PlayerEntity player = Minecraft.getInstance().player;
        if(player == null) {
            closeScreen();
            return;
        }
        if(player.getHeldItemMainhand().getItem() instanceof IManaContextItem)
            heldItem = player.getHeldItemMainhand();
        else if(player.getHeldItemOffhand().getItem() instanceof IManaContextItem)
            heldItem = player.getHeldItemOffhand();

        if(heldItem.isEmpty()) {
            closeScreen();
            return;
        }
        NBTTagHelper.PlayerData playerData = NBTTagHelper.PlayerData.playerData(player);
        this.minecraft.keyboardListener.enableRepeatEvents(true);

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
            this.addButton(new ItemStackButton(x, y, 21, 21, item.getDisplayName(), (button) -> {
                Networking.INSTANCE.send(
                        PacketDistributor.SERVER.noArg(), CSpellSwapPack.swapItem(player.getEntityId(), finalI));
                closeScreen();
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
            this.addButton(new ItemStackButton(x, y, 21, 21, new StringTextComponent(""), (button) -> {
                Networking.INSTANCE.send(
                        PacketDistributor.SERVER.noArg(), CSpellSwapPack.swapItem(player.getEntityId(), finalC));
                closeScreen();
            }, ItemStack.EMPTY));
            y+=30;
            i++;
        }

        super.init();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.pushMatrix();
        RenderSystem.scaled(5, 5, 5);
        minecraft.getItemRenderer().renderItemIntoGUI(heldItem, width / 12, height / 16);
        RenderSystem.popMatrix();
        drawCenteredString(matrixStack, this.font, heldItem.getDisplayName(), this.width / 2, (int) (this.height * 0.7), 0xFFFFFF);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
