package com.rogoshum.magickcore.client.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.common.tileentity.ElementWoolTileEntity;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.BufferContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ElementWoolRenderer implements BlockEntityRenderer<ElementWoolTileEntity> {
    protected static final ResourceLocation wool = new ResourceLocation("textures/block/white_wool.png");

    private static final RenderType RENDER_TYPE_0 = RenderHelper.getTexedOrbSolid(wool);
    private static final RenderType RENDER_TYPE_1 = RenderHelper.getTexedOrbGlint(RenderHelper.SPHERE_ROTATE, 0.1f, 0f);

    public ElementWoolRenderer(BlockEntityRendererProvider.Context p_173554_) {
    }

    @Override
    public void render(ElementWoolTileEntity tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RENDER_TYPE_0), new RenderHelper.RenderContext(1.0f, tile.getColor(), RenderHelper.renderLight));
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RENDER_TYPE_1), new RenderHelper.RenderContext(1.0f, tile.getColor(), RenderHelper.renderLight));
        matrixStackIn.popPose();
    }
}
