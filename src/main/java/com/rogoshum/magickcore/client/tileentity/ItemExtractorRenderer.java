package com.rogoshum.magickcore.client.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.tileentity.ElementWoolTileEntity;
import com.rogoshum.magickcore.common.tileentity.ItemExtractorTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceLocation;

public class ItemExtractorRenderer extends BlockEntityRenderer<ItemExtractorTileEntity> {
    protected static final ResourceLocation wool = new ResourceLocation("textures/block/glass.png");

    private static final RenderType RENDER_TYPE_0 = RenderHelper.getTexedOrb(wool);
    private static final RenderType RENDER_TYPE_1 = RenderHelper.getTexedOrbGlint(RenderHelper.SPHERE_ROTATE, 0.1f, 0f);

    public ItemExtractorRenderer(BlockEntityRenderDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(ItemExtractorTileEntity tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        matrixStackIn.scale(0.99f, 0.99f, 0.99f);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RenderHelper.getTexedOrbSolid(wool)), new RenderHelper.RenderContext(1.0f, Color.ORIGIN_COLOR, RenderHelper.renderLight));
        //RenderHelper.renderCube(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuffer(), RENDER_TYPE_1), new RenderHelper.RenderContext(1.0f, Color.ORIGIN_COLOR, RenderHelper.renderLight));
        matrixStackIn.popPose();
    }
}
