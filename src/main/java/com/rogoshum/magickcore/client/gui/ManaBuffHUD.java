package com.rogoshum.magickcore.client.gui;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Matrix4f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.common.buff.ManaBuff;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Collection;
import java.util.HashMap;

public class ManaBuffHUD extends GuiComponent {
    private final int width;
    private final int height;
    private static final HashMap<String, ResourceLocation> buffTexture = new HashMap<>();
    private final Minecraft minecraft;
    private PoseStack matrixStack;
    private final EntityStateData state;
    private final ResourceLocation BUFF_TEXTURE = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/buff.png");
    private final ResourceLocation DE_BUFF_TEXTURE = new ResourceLocation(MagickCore.MOD_ID, "textures/gui/debuff.png");

    public ManaBuffHUD(PoseStack matrixStack, EntityStateData state) {
        this.width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        this.height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
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

    public void setMatrixStack(PoseStack stack) {
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

        boolean hasDeBuff = false;
        Color buffColor = Color.BLACK_COLOR;
        Color deBuffColor = Color.BLACK_COLOR;
        for(ManaBuff buff : buffHashMap.values()) {
            if(buff.isBeneficial()) {
                Color color = MagickCore.proxy.getElementRender(buff.getElement()).getPrimaryColor();
                buffColor = Color.create((buffColor.r() + color.r()) * 0.5f, (buffColor.g() + color.g()) * 0.5f, (buffColor.b() + color.b()) * 0.5f);

            } else {
                hasDeBuff = true;
                Color color = MagickCore.proxy.getElementRender(buff.getElement()).getPrimaryColor();
                deBuffColor = Color.create((deBuffColor.r() + color.r()) * 0.5f, (deBuffColor.g() + color.g()) * 0.5f, (deBuffColor.b() + color.b()) * 0.5f);
            }
        }

        if(hasDeBuff) {
            renderBuffHUD(deBuffColor, 1.0f, DE_BUFF_TEXTURE, RenderHelper.Noise.LINE.res());
        } else {
            renderBuffHUD(buffColor, 1.0f, BUFF_TEXTURE, RenderHelper.BLANK_TEX);
        }
    }

    public void renderBuffHUD(Color color, float alpha, ResourceLocation texture, ResourceLocation noise) {
        RenderSystem.setShaderColor(color.r(), color.g(), color.b(), alpha);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderTexture(1, noise);
        long time = Util.getMillis() / 2;
        float f = (float)(time % 110000L) / 110000.0F;
        float f1 = (float)(time % 30000L) / 30000.0F;
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

    protected void renderBuff(HashMap<String, ManaBuff> buffHashMap, int i, int j) {
        i = 0;
        j = 0;
        int bC = buffHashMap.values().stream().filter(ManaBuff::isBeneficial).toList().size();
        int nBC = buffHashMap.size() - bC;

        for(String key : buffHashMap.keySet()) {
            if(!buffTexture.containsKey(key)) continue;
            ManaBuff manaBuff = buffHashMap.get(key);
            RenderSystem.setShaderTexture(0, AbstractContainerScreen.INVENTORY_LOCATION);
            int k = this.width / 2+(manaBuff.isBeneficial()?bC:nBC)*6;
            int l = this.height / 2+5;
            if (minecraft.isDemo()) {
                l += 15;
            }

            if (manaBuff.isBeneficial()) {
                ++i;
                k = k - 12 * i;
            } else {
                ++j;
                k = k - 12 * j;
                l += 12;
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.8F);
            PoseStack poseStack = RenderSystem.getModelViewStack();
            float f = 1.0F;
            poseStack.pushPose();
            poseStack.scale(0.5f, 0.5f, 0.5f);
            this.blit(matrixStack, k*2, l*2, 141, 166, 24, 24);
            poseStack.popPose();
            if (manaBuff.getTick() <= 200) {
                int i1 = 10 - manaBuff.getTick() / 20;
                f = Mth.clamp((float)manaBuff.getTick() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + Mth.cos((float)manaBuff.getTick() * (float)Math.PI / 5.0F) * Mth.clamp((float)i1 / 10.0F * 0.25F, 0.0F, 0.25F);
            }

            f = Math.min(f, 0.7f);

            int j1 = k;
            int k1 = l;
            float f1 = f;
            RenderSystem.setShaderTexture(0, getBuffTexture(key));
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f1);
            blit(matrixStack, j1 + 2, k1 + 2, this.getBlitOffset(), 0, 0, 8, 8, 8, 8);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
