package com.rogoshum.magickcore.client.gui;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.itemstack.IManaData;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.buff.ManaBuff;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.block.PumpkinBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.Collection;
import java.util.HashMap;

public class ManaBuffGUI extends AbstractGui {
    private final int width;
    private static final HashMap<String, ResourceLocation> buffTexture = new HashMap<>();
    private final Minecraft minecraft;
    private MatrixStack matrixStack;
    private final EntityStateData state;

    public ManaBuffGUI(MatrixStack matrixStack, EntityStateData state) {
        this.width = Minecraft.getInstance().getMainWindow().getScaledWidth();
        this.minecraft = Minecraft.getInstance();
        this.matrixStack = matrixStack;
        this.state = state;
    }

    public static void addBuffTexture(String buff, ResourceLocation texture) {
        buffTexture.put(buff, texture);
    }

    public static ResourceLocation getBuffTexture(String buff) {
        if(buffTexture.containsKey(buff))
            return buffTexture.get(buff);
        return RenderHelper.EMPTY_TEXTURE;
    }

    public void setMatrixStack(MatrixStack stack) {
        this.matrixStack = stack;
    }

    public void render() {
        HashMap<String, ManaBuff> buffHashMap = null;
        if(state != null) {
            if(!state.getBuffList().isEmpty())
                buffHashMap = state.getBuffList();
        }
        if(buffHashMap == null) return;

        Collection<EffectInstance> collection = minecraft.player.getActivePotionEffects();
        int i = 0;
        int j = 0;
        if (!collection.isEmpty()) {
            for(EffectInstance effectinstance : Ordering.natural().reverse().sortedCopy(collection)) {
                Effect effect = effectinstance.getPotion();
                if (!effectinstance.shouldRenderHUD()) continue;
                if (effectinstance.isShowIcon()) {
                    if (effect.isBeneficial()) {
                        ++i;
                    } else {
                        ++j;
                    }
                }
            }
        }
        renderBuff(buffHashMap, i, j);
    }

    protected void renderBuff(HashMap<String, ManaBuff> buffHashMap, int i, int j) {
        for(String key : buffHashMap.keySet()) {
            if(!buffTexture.containsKey(key)) continue;
            ManaBuff manaBuff = buffHashMap.get(key);
            minecraft.getTextureManager().bindTexture(ContainerScreen.INVENTORY_BACKGROUND);
            int k = this.width;
            int l = 1;
            if (minecraft.isDemo()) {
                l += 15;
            }

            if (manaBuff.isBeneficial()) {
                ++i;
                k = k - 25 * i;
            } else {
                ++j;
                k = k - 25 * j;
                l += 26;
            }

            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            float f = 1.0F;
            this.blit(matrixStack, k, l, 141, 166, 24, 24);
            if (manaBuff.getTick() <= 200) {
                int i1 = 10 - manaBuff.getTick() / 20;
                f = MathHelper.clamp((float)manaBuff.getTick() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + MathHelper.cos((float)manaBuff.getTick() * (float)Math.PI / 5.0F) * MathHelper.clamp((float)i1 / 10.0F * 0.25F, 0.0F, 0.25F);
            }

            int j1 = k;
            int k1 = l;
            float f1 = f;
            minecraft.getTextureManager().bindTexture(buffTexture.get(key));
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, f1);
            blit(matrixStack, j1 + 3, k1 + 3, this.getBlitOffset(), 0, 0, 16, 16, 18, 18);
        }
    }
}
