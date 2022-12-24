package com.rogoshum.magickcore.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.gui.ManaBuffHUD;
import com.rogoshum.magickcore.common.buff.ManaBuff;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
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

@Mixin(DisplayEffectsScreen.class)

public abstract class MixinDisplayEffectsScreen<T extends Container> extends ContainerScreen<T> {
    HashMap<String, ManaBuff> manaBuffHashMap = new HashMap<>();
    @Shadow
    protected boolean hasActivePotionEffects;

    public MixinDisplayEffectsScreen(T screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Inject(method = "updateActivePotionEffects",
            at = @At(value = "RETURN")
            )
    protected void onUpdateActivePotionEffects(CallbackInfo ci) {
        if (Minecraft.getInstance().player.getActivePotionEffects().isEmpty()) {
            EntityStateData state = ExtraDataUtil.entityStateData(Minecraft.getInstance().player);
            if(state != null && !state.getBuffList().isEmpty()) {
                if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.PotionShiftEvent(this)))
                    this.guiLeft = (this.width - this.xSize) / 2;
                else
                    this.guiLeft = 160 + (this.width - this.xSize - 200) / 2;
                this.hasActivePotionEffects = true;
            }
        }
    }

    @Inject(method = "renderEffectBackground",
            at = @At(value = "RETURN"),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    protected void onRenderEffectBackground(MatrixStack matrixStack, int p_238810_2_, int p_238810_3_, Iterable<EffectInstance> effects, CallbackInfo ci, int i) {
        if(!manaBuffHashMap.isEmpty()) {
            for(int c = 0; c < manaBuffHashMap.keySet().size(); ++c) {
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.blit(matrixStack, p_238810_2_, i, 0, 166, 140, 32);
                i += p_238810_3_;
            }
        }
    }

    @Inject(method = "renderEffectSprites",
            at = @At(value = "RETURN"),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    protected void onRenderEffectSprites(MatrixStack matrixStack, int p_238812_2_, int p_238812_3_, Iterable<EffectInstance> effects, CallbackInfo ci, PotionSpriteUploader potionspriteuploader, int i) {
        if(!manaBuffHashMap.isEmpty()) {
            for(String buff : manaBuffHashMap.keySet()) {
                this.minecraft.getTextureManager().bindTexture(ManaBuffHUD.getBuffTexture(buff));
                blit(matrixStack, p_238812_2_ + 6, i + 7, this.getBlitOffset(), 0, 0, 16, 16, 18, 18);
                i += p_238812_3_;
            }
        }
    }

    @Inject(method = "renderEffectText",
            at = @At(value = "RETURN"),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    protected void onRenderEffectText(MatrixStack matrixStack, int p_238813_2_, int p_238813_3_, Iterable<EffectInstance> effects, CallbackInfo ci, int i) {
        if(!manaBuffHashMap.isEmpty()) {
            for(String buff : manaBuffHashMap.keySet()) {
                ManaBuff buffInstance = manaBuffHashMap.get(buff);
                String s = I18n.format(MagickCore.MOD_ID + ".buff." + buff);
                if (buffInstance.getForce() >= 1 && buffInstance.getForce() <= 9) {
                    s = s + ' ' + I18n.format("enchantment.level." + (int)(buffInstance.getForce()));
                }

                this.font.drawStringWithShadow(matrixStack, s, (float)(p_238813_2_ + 10 + 18), (float)(i + 6), 16777215);
                String s1 = StringUtils.ticksToElapsedTime(buffInstance.getTick());
                this.font.drawStringWithShadow(matrixStack, s1, (float)(p_238813_2_ + 10 + 18), (float)(i + 6 + 10), 8355711);
                i += p_238813_3_;
            }
        }
    }

    @Inject(method = "renderEffects",
            at = @At(value = "HEAD")
    )
    private void onRenderEffects(MatrixStack matrixStack, CallbackInfo ci) {
        int i = this.guiLeft - 124;
        Collection<EffectInstance> collection = this.minecraft.player.getActivePotionEffects();
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
            at = @At(value = "INVOKE", target = "java/util/Collection.stream ()Ljava/util/stream/Stream;"),
            ordinal = 1
    )
    private int onRenderingEffects(int value) {
        Collection<EffectInstance> collection = this.minecraft.player.getActivePotionEffects();
        if (collection.size() + manaBuffHashMap.size() > 5) {
            return 132 / (collection.size() + manaBuffHashMap.size() - 1);
        }
        return value;
    }

    private void renderBuffBackground(MatrixStack matrixStack, int p_238810_2_, int p_238810_3_, int size) {
        this.minecraft.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
        int i = this.guiTop;

        for(int c = 0; c < size; ++c) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.blit(matrixStack, p_238810_2_, i, 0, 166, 140, 32);
            i += p_238810_3_;
        }
    }

    private void renderBuffSprites(MatrixStack matrixStack, int p_238812_2_, int p_238812_3_, Set<String> buffs) {
        int i = this.guiTop;

        for(String buff : buffs) {
            this.minecraft.getTextureManager().bindTexture(ManaBuffHUD.getBuffTexture(buff));
            blit(matrixStack, p_238812_2_ + 6, i + 7, this.getBlitOffset(), 0, 0, 16, 16, 18, 18);
            i += p_238812_3_;
        }
    }

    private void renderBuffText(MatrixStack matrixStack, int p_238813_2_, int p_238813_3_, HashMap<String, ManaBuff> buffs) {
        int i = this.guiTop;

        for(String buff : buffs.keySet()) {
            ManaBuff buffInstance = buffs.get(buff);
            String s = I18n.format(MagickCore.MOD_ID + ".buff." + buff);
            if (buffInstance.getForce() >= 1 && buffInstance.getForce() <= 9) {
                s = s + ' ' + I18n.format("enchantment.level." + (int)(buffInstance.getForce()));
            }

            this.font.drawStringWithShadow(matrixStack, s, (float)(p_238813_2_ + 10 + 18), (float)(i + 6), 16777215);
            String s1 = StringUtils.ticksToElapsedTime(buffInstance.getTick());
            this.font.drawStringWithShadow(matrixStack, s1, (float)(p_238813_2_ + 10 + 18), (float)(i + 6 + 10), 8355711);
            i += p_238813_3_;
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {

    }
}
