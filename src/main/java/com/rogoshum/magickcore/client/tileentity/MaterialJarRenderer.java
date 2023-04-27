package com.rogoshum.magickcore.client.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.common.tileentity.MaterialJarTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import com.mojang.math.Vector3f;

public class MaterialJarRenderer implements BlockEntityRenderer<MaterialJarTileEntity> {
    public MaterialJarRenderer(BlockEntityRendererProvider.Context p_173554_) {
    }

    @Override
    public void render(MaterialJarTileEntity tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.5, 0.5);

        if(!tile.getStack().isEmpty()) {
            matrixStackIn.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
            matrixStackIn.pushPose();
            matrixStackIn.translate(0, 0.3, 0);
            matrixStackIn.scale(0.01f, 0.01f, .01f);
            matrixStackIn.mulPose(Vector3f.ZN.rotationDegrees(180));
            String count = String.valueOf(tile.getCount());
            Minecraft.getInstance().font.draw(matrixStackIn, count, -count.length()*3, 2, 0);
            matrixStackIn.popPose();
            matrixStackIn.translate(0, -0.2f, 0);
            //matrixStackIn.scale(0.5f, 0.5f,0.5f);
            BakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getModel(tile.getStack(), null, null, 0);
            MultiBufferSource.BufferSource renderTypeBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            Minecraft.getInstance().getItemRenderer().render(tile.getStack(), ItemTransforms.TransformType.GROUND, false, matrixStackIn, renderTypeBuffer, combinedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel_);
            renderTypeBuffer.endBatch();
        }

        matrixStackIn.popPose();
    }
}
