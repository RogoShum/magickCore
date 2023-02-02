package com.rogoshum.magickcore.mixin.fabric;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.rogoshum.magickcore.api.itemstack.IColorDurabilityBar;
import com.rogoshum.magickcore.client.item.EasyItemRenderer;
import com.rogoshum.magickcore.proxy.ClientProxy;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
    @Shadow protected abstract void fillRect(BufferBuilder bufferBuilder, int i, int j, int k, int l, int m, int n, int o, int p);

    @Inject(method = "render", at = @At("RETURN"))
    public void onRender(ItemStack itemStack, ItemTransforms.TransformType transformType, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, BakedModel bakedModel, CallbackInfo ci){
        poseStack.pushPose();
        bakedModel.getTransforms().getTransform(transformType).apply(bl, poseStack);
        poseStack.translate(-0.5, -0.5, -0.5);
        EasyItemRenderer renderer = ClientProxy.getItemRenderer(itemStack.getItem());
        if(renderer != null)
            renderer.renderByItem(itemStack, transformType, poseStack, multiBufferSource, i, j);
        poseStack.popPose();
    }

    @Inject(method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isDamaged()Z"))
    public void onRender(Font font, ItemStack itemStack, int i, int j, String string, CallbackInfo ci){
        if(itemStack.getItem() instanceof IColorDurabilityBar) {
            IColorDurabilityBar colorDurabilityBar = (IColorDurabilityBar) itemStack.getItem();
            if(!colorDurabilityBar.showDurabilityBar(itemStack)) return;
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tesselator.getBuilder();
            int k = Math.round(13.0F - (float) colorDurabilityBar.getDurabilityForDisplay(itemStack) * 13.0F);
            int l = colorDurabilityBar.getRGBDurabilityForDisplay(itemStack);
            this.fillRect(bufferBuilder, i + 2, j + 13, 13, 2, 0, 0, 0, 255);
            this.fillRect(bufferBuilder, i + 2, j + 13, k, 1, l >> 16 & 255, l >> 8 & 255, l & 255, 255);
            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
    }
}
