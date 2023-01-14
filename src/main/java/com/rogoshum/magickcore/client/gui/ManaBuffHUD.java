package com.rogoshum.magickcore.client.gui;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.shaders.Effect;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.buff.ManaBuff;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Collection;
import java.util.HashMap;

public class ManaBuffHUD extends GuiComponent {
    private final int width;
    private static final HashMap<String, ResourceLocation> buffTexture = new HashMap<>();
    private final Minecraft minecraft;
    private PoseStack matrixStack;
    private final EntityStateData state;

    public ManaBuffHUD(PoseStack matrixStack, EntityStateData state) {
        this.width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
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

    public void setPoseStack(PoseStack stack) {
        this.matrixStack = stack;
    }

    public void render() {
        HashMap<String, ManaBuff> buffHashMap = null;
        if(state != null) {
            if(!state.getBuffList().isEmpty())
                buffHashMap = state.getBuffList();
        }
        if(buffHashMap == null) return;

        Collection<MobEffectInstance> collection = minecraft.player.getActiveEffects();
        int i = 0;
        int j = 0;
        if (!collection.isEmpty()) {
            for(MobEffectInstance effectinstance : Ordering.natural().reverse().sortedCopy(collection)) {
                MobEffect effect = effectinstance.getEffect();
                if (!effectinstance.showIcon()) continue;
                if (effectinstance.showIcon()) {
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
        i = 0;
        j = 0;
        for(String key : buffHashMap.keySet()) {
            if(!buffTexture.containsKey(key)) continue;
            ManaBuff manaBuff = buffHashMap.get(key);
            minecraft.getTextureManager().bind(ContainerScreen.INVENTORY_LOCATION);
            int k = this.width;
            int l = 52;
            if (minecraft.isDemo()) {
                l += 15;
            }

            if (manaBuff.isBeneficial()) {
                ++i;
                k = k - 13 * i;
            } else {
                ++j;
                k = k - 13 * j;
                l += 13;
            }

            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.8F);
            float f = 1.0F;
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.5f, 0.5f, 0.5f);
            this.blit(matrixStack, k*2, l*2, 141, 166, 24, 24);
            RenderSystem.popMatrix();
            if (manaBuff.getTick() <= 200) {
                int i1 = 10 - manaBuff.getTick() / 20;
                f = MathHelper.clamp((float)manaBuff.getTick() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + MathHelper.cos((float)manaBuff.getTick() * (float)Math.PI / 5.0F) * MathHelper.clamp((float)i1 / 10.0F * 0.25F, 0.0F, 0.25F);
            }

            int j1 = k;
            int k1 = l;
            float f1 = f;
            minecraft.getTextureManager().bind(buffTexture.get(key));
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, f1);
            blit(matrixStack, j1 + 2, k1 + 2, this.getBlitOffset(), 0, 0, 8, 8, 9, 9);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
