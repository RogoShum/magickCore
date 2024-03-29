package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.item.model.OrbBottleModel;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

public class OrbBottleRenderer extends EasyItemRenderer {
    protected final ResourceLocation BOTTLE = new ResourceLocation(MagickCore.MOD_ID + ":textures/items/orb_bottle/bottle.png");
    protected final ResourceLocation LIQUID = new ResourceLocation(MagickCore.MOD_ID + ":textures/items/orb_bottle/liquid.png");
    private final OrbBottleModel model = new OrbBottleModel();

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        matrixStackIn.mulPose(Vector3f.ZN.rotationDegrees(180));
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270));
        matrixStackIn.translate(0, -0.95, 0);
        if(stack.hasTag() && NBTTagHelper.hasElement(stack)) {
            Color color = MagickRegistry.getElement(NBTTagHelper.getElement(stack)).color();
            this.model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedEntity(LIQUID)), RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, color.r(), color.g(), color.b(), 1.0F);
        }
        this.model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedEntity(BOTTLE)), combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.3F);
        matrixStackIn.popPose();
    }
}
