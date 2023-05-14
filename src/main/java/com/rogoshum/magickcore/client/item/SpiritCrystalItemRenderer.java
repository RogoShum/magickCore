package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ItemStack;
import com.mojang.math.Vector3f;

public class SpiritCrystalItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(RenderHelper.BLANK_TEX);

    public SpiritCrystalItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, -0.15, 0.5);
        if(transformType != ItemTransforms.TransformType.GROUND) {
            matrixStackIn.scale(0.625f, 0.625f, 0.625f);
            matrixStackIn.translate(0.0, 0.6, 0.0);
        }
        if(transformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND || transformType == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND) {
            matrixStackIn.mulPose(Vector3f.ZN.rotationDegrees(45));
        }
        matrixStackIn.scale(2.5f, 2.5f, 2.5f);
        matrixStackIn.pushPose();
        matrixStackIn.scale(0.2f, 0.07f, 0.2f);
        RenderHelper.renderCubeCache(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RENDER_TYPE)
                , new RenderHelper.RenderContext(0.5f, Color.ORIGIN_COLOR, RenderHelper.renderLight));
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
        RenderHelper.renderCubeCache(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RENDER_TYPE)
                , new RenderHelper.RenderContext(0.5f, Color.ORIGIN_COLOR, RenderHelper.renderLight));
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
        RenderHelper.renderCubeCache(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RENDER_TYPE)
                , new RenderHelper.RenderContext(0.5f, Color.ORIGIN_COLOR, RenderHelper.renderLight));
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
        RenderHelper.renderCubeCache(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RENDER_TYPE)
                , new RenderHelper.RenderContext(0.5f, Color.ORIGIN_COLOR, RenderHelper.renderLight));
        matrixStackIn.popPose();
        matrixStackIn.popPose();

        matrixStackIn.popPose();
    }
}
