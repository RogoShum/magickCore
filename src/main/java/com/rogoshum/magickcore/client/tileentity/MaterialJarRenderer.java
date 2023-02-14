package com.rogoshum.magickcore.client.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.tileentity.MaterialJarTileEntity;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;

public class MaterialJarRenderer implements BlockEntityRenderer<MaterialJarTileEntity> {
    protected static final ResourceLocation cylinder_bloom = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_bloom.png");

    public MaterialJarRenderer(BlockEntityRendererProvider.Context p_173554_) {
    }

    @Override
    public void render(MaterialJarTileEntity tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.5, 0.5);

        matrixStackIn.pushPose();
        matrixStackIn.scale(0.6f, 0.99f, 0.6f);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RenderHelper.getTexedOrb(RenderHelper.blankTex))
                , new RenderHelper.RenderContext(0.2f, Color.ORIGIN_COLOR, combinedLightIn));
        matrixStackIn.scale(0.9f, 0.9f, 0.9f);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RenderHelper.getTexedOrb(RenderHelper.blankTex))
                , new RenderHelper.RenderContext(0.05f, Color.ORIGIN_COLOR, combinedLightIn));
        matrixStackIn.popPose();

/*
        matrixStackIn.push();
        matrixStackIn.translate(0, 0.5, 0.0);
        matrixStackIn.scale(0.3f, 0.1f, 0.3f);
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuffer(), RenderHelper.getTexedOrbSolid(RenderHelper.blankTex))
                , new RenderHelper.RenderContext(1.0f, Color.create(0.4f, 0.3f, 0), combinedLightIn));
        matrixStackIn.pop();
 */

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
