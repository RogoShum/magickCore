package com.rogoshum.magickcore.mixin.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.api.mixin.IItemUpdate;
import com.rogoshum.magickcore.client.item.EasyItemRenderer;
import com.rogoshum.magickcore.proxy.ClientProxy;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
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
}
