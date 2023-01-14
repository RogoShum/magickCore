package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class SpiritWoodStickRenderer extends ItemStackTileEntityRenderer {
    private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(RenderHelper.blankTex);

    @Override
    public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        if(transformType != ItemCameraTransforms.TransformType.GROUND) {
            if(transformType == ItemCameraTransforms.TransformType.GUI) {
                matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(45));
                matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(30));
                matrixStackIn.mulPose(Vector3f.ZN.rotationDegrees(45));
                matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(45));
            } else {
                matrixStackIn.mulPose(Vector3f.ZN.rotationDegrees(45));
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90));
            }
            matrixStackIn.scale(0.65f, 0.65f, 0.65f);
        }
        matrixStackIn.scale(0.15f, 1.5f, 0.15f);

        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuilder(), RenderType.entityTranslucent(new ResourceLocation( "minecraft:textures/block/quartz_block_top.png")))
                , new RenderHelper.RenderContext(0.8f, Color.ORIGIN_COLOR, combinedLight));
        matrixStackIn.popPose();
    }
}
