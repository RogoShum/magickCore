package com.rogoshum.magickcore.client.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.tileentity.SpiritCrystalTileEntity;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;

public class SpiritCrystalRenderer extends BlockEntityRenderer<SpiritCrystalTileEntity> {
    protected static final ResourceLocation cylinder_bloom = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_bloom.png");

    public SpiritCrystalRenderer(BlockEntityRenderDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(SpiritCrystalTileEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.03, 0.5);
        matrixStackIn.pushPose();
        matrixStackIn.scale(0.2f, 0.07f, 0.2f);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RenderHelper.getTexedEntityGlow(RenderHelper.blankTex))
                , new RenderHelper.RenderContext(0.5f, Color.ORIGIN_COLOR, combinedLightIn));
        matrixStackIn.popPose();
        matrixStackIn.translate(0.0, 0.15, 0.0);
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.05, 0.0, 0.0);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-20));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-20));
        matrixStackIn.pushPose();
        matrixStackIn.scale(0.1f, 0.25f, 0.1f);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(45));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(45));
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RenderHelper.getTexedEntityGlow(RenderHelper.blankTex))
                , new RenderHelper.RenderContext(0.5f, Color.ORIGIN_COLOR, combinedLightIn));
        matrixStackIn.popPose();
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(-0.05, 0.00, -0.05);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-20));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(20));
        matrixStackIn.pushPose();
        matrixStackIn.scale(0.1f, 0.3f, 0.1f);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(45));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-45));
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RenderHelper.getTexedEntityGlow(RenderHelper.blankTex))
                , new RenderHelper.RenderContext(0.5f, Color.ORIGIN_COLOR, combinedLightIn));
        matrixStackIn.popPose();
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(-0.05, 0.00, 0.05);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(20));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(20));
        matrixStackIn.pushPose();
        matrixStackIn.scale(0.1f, 0.2f, 0.1f);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-45));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-45));
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RenderHelper.getTexedEntityGlow(RenderHelper.blankTex))
                , new RenderHelper.RenderContext(0.5f, Color.ORIGIN_COLOR, combinedLightIn));
        matrixStackIn.popPose();
        matrixStackIn.popPose();

        matrixStackIn.popPose();
    }
}
