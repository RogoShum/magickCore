package com.rogoshum.magickcore.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.gui.ManaBuffHUD;
import com.rogoshum.magickcore.common.buff.ManaBuff;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

@Mixin(EffectRenderingInventoryScreen.class)

public abstract class MixinDisplayEffectsScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    HashMap<String, ManaBuff> manaBuffHashMap = new HashMap<>();
    @Shadow
    protected boolean doRenderEffects;

    public MixinDisplayEffectsScreen(T screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Inject(method = "checkEffectRendering",
            at = @At(value = "RETURN")
            )
    protected void onUpdateActivePotionEffects(CallbackInfo ci) {
        if (Minecraft.getInstance().player.getActiveEffects().isEmpty()) {
            EntityStateData state = ExtraDataUtil.entityStateData(Minecraft.getInstance().player);
            if(state != null && !state.getBuffList().isEmpty()) {
                this.leftPos = 160 + (this.width - this.imageWidth - 200) / 2;
                this.doRenderEffects = true;
            }
        }
    }

    @Inject(method = "renderBackgrounds",
            at = @At(value = "RETURN"),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    protected void onRenderEffectBackground(PoseStack matrixStack, int p_238810_2_, int p_238810_3_, Iterable<EffectInstance> effects, CallbackInfo ci, int i) {
        if(!manaBuffHashMap.isEmpty()) {
            for(int c = 0; c < manaBuffHashMap.keySet().size(); ++c) {
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.blit(matrixStack, p_238810_2_, i, 0, 166, 140, 32);
                i += p_238810_3_;
            }
        }
    }

    @Inject(method = "renderIcons",
            at = @At(value = "RETURN"),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    protected void onRenderEffectSprites(PoseStack matrixStack, int i, int j, Iterable<MobEffectInstance> effects, CallbackInfo ci) {
        if(!manaBuffHashMap.isEmpty()) {
            for(String buff : manaBuffHashMap.keySet()) {
                this.minecraft.getTextureManager().bind(ManaBuffHUD.getBuffTexture(buff));
                blit(matrixStack, j + 6, i + 7, this.getBlitOffset(), 0, 0, 16, 16, 18, 18);
                i += j;
            }
        }
    }

    @Inject(method = "renderLabels",
            at = @At(value = "RETURN"),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    protected void onRenderEffectText(PoseStack matrixStack, int p_238813_2_, int p_238813_3_, Iterable<MobEffectInstance> effects, CallbackInfo ci, int i) {
        if(!manaBuffHashMap.isEmpty()) {
            for(String buff : manaBuffHashMap.keySet()) {
                ManaBuff buffInstance = manaBuffHashMap.get(buff);
                String s = I18n.get(MagickCore.MOD_ID + ".buff." + buff);
                if (buffInstance.getForce() >= 1 && buffInstance.getForce() <= 9) {
                    s = s + ' ' + I18n.get("enchantment.level." + (int)(buffInstance.getForce()));
                }

                this.font.drawShadow(matrixStack, s, (float)(p_238813_2_ + 10 + 18), (float)(i + 6), 16777215);
                String s1 = StringUtil.formatTickDuration(buffInstance.getTick());
                this.font.drawShadow(matrixStack, s1, (float)(p_238813_2_ + 10 + 18), (float)(i + 6 + 10), 8355711);
                i += p_238813_3_;
            }
        }
    }

    @Inject(method = "renderEffects",
            at = @At(value = "HEAD")
    )
    private void onRenderEffects(PoseStack matrixStack, CallbackInfo ci) {
        int i = this.leftPos - 124;
        Collection<MobEffectInstance> collection = this.minecraft.player.getActiveEffects();
        EntityStateData state = ExtraDataUtil.entityStateData(Minecraft.getInstance().player);
        HashMap<String, ManaBuff> buffs = new HashMap<>();
        if(state != null && !state.getBuffList().isEmpty())
            buffs.putAll(state.getBuffList());
        buffs.keySet().removeIf((key) -> ManaBuffHUD.getBuffTexture(key) == RenderHelper.EMPTY_TEXTURE);
        manaBuffHashMap = buffs;
        if (collection.isEmpty() && !buffs.isEmpty()) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int j = 33;
            if (buffs.size() > 5) {
                j = 132 / (buffs.size() - 1);
            }
            this.renderBuffBackground(matrixStack, i, j, buffs.size());
            this.renderBuffSprites(matrixStack, i, j, buffs.keySet());
            this.renderBuffText(matrixStack, i, j, buffs);
        }
    }

    @ModifyVariable(method = "renderEffects",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/EffectRenderingInventoryScreen;renderBackgrounds(Lcom/mojang/blaze3d/vertex/PoseStack;IILjava/lang/Iterable;)V"),
            ordinal = 1
    )
    private int onRenderingEffects(int value) {
        Collection<MobEffectInstance> collection = this.minecraft.player.getActiveEffects();
        if (collection.size() + manaBuffHashMap.size() > 5) {
            return 132 / (collection.size() + manaBuffHashMap.size() - 1);
        }
        return value;
    }

    private void renderBuffBackground(PoseStack matrixStack, int p_238810_2_, int p_238810_3_, int size) {
        this.minecraft.getTextureManager().bind(INVENTORY_LOCATION);
        int i = this.topPos;

        for(int c = 0; c < size; ++c) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.blit(matrixStack, p_238810_2_, i, 0, 166, 140, 32);
            i += p_238810_3_;
        }
    }

    private void renderBuffSprites(PoseStack matrixStack, int p_238812_2_, int p_238812_3_, Set<String> buffs) {
        int i = this.topPos;

        for(String buff : buffs) {
            this.minecraft.getTextureManager().bind(ManaBuffHUD.getBuffTexture(buff));
            blit(matrixStack, p_238812_2_ + 6, i + 7, this.getBlitOffset(), 0, 0, 16, 16, 18, 18);
            i += p_238812_3_;
        }
    }

    private void renderBuffText(PoseStack matrixStack, int p_238813_2_, int p_238813_3_, HashMap<String, ManaBuff> buffs) {
        int i = this.topPos;

        for(String buff : buffs.keySet()) {
            ManaBuff buffInstance = buffs.get(buff);
            String s = I18n.get(MagickCore.MOD_ID + ".buff." + buff);
            if (buffInstance.getForce() >= 1 && buffInstance.getForce() <= 9) {
                s = s + ' ' + I18n.get("enchantment.level." + (int)(buffInstance.getForce()));
            }

            this.font.drawShadow(matrixStack, s, (float)(p_238813_2_ + 10 + 18), (float)(i + 6), 16777215);
            String s1 = StringUtil.formatTickDuration(buffInstance.getTick());
            this.font.drawShadow(matrixStack, s1, (float)(p_238813_2_ + 10 + 18), (float)(i + 6 + 10), 8355711);
            i += p_238813_3_;
        }
    }
}
