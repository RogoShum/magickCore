package com.rogoshum.magickcore.client.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.tileentity.DimensionInflateTileEntity;
import com.rogoshum.magickcore.common.tileentity.MaterialJarTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;

public class DimensionInflateRenderer implements BlockEntityRenderer<DimensionInflateTileEntity> {
    public DimensionInflateRenderer(BlockEntityRendererProvider.Context p_173554_) {
    }

    @Override
    public void render(DimensionInflateTileEntity tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.7, 0.5);

        if(!tile.getItemStack().isEmpty()) {
            double floa = Math.cos(Math.toRadians(MagickCore.proxy.getRunTick() % 360) * Math.PI)*0.1;
            matrixStackIn.translate(0, floa, 0);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(MagickCore.proxy.getRunTick() % 360));
            BakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getModel(tile.getItemStack(), null, null, 0);
            Minecraft.getInstance().getItemRenderer().render(tile.getItemStack(), ItemTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel_);
        }

        matrixStackIn.popPose();
    }
}
