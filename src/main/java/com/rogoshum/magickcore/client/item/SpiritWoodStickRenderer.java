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
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;

public class SpiritWoodStickRenderer extends BlockEntityWithoutLevelRenderer {
    private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(RenderHelper.BLANK_TEX);

    public SpiritWoodStickRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        if(transformType != ItemTransforms.TransformType.GROUND) {
            if(transformType == ItemTransforms.TransformType.GUI) {
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

        RenderHelper.renderCubeCache(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RenderType.entityTranslucent(new ResourceLocation( "minecraft:textures/block/quartz_block_top.png")))
                , new RenderHelper.RenderContext(0.8f, Color.ORIGIN_COLOR, combinedLight));
        matrixStackIn.popPose();
    }
}
